package ch.zhaw.fswd.powerdate.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.fswd.powerdate.dto.ChatDto;
import ch.zhaw.fswd.powerdate.dto.MessageDto;
import ch.zhaw.fswd.powerdate.dto.NewMessageDto;
import ch.zhaw.fswd.powerdate.entity.ChatDbo;
import ch.zhaw.fswd.powerdate.entity.MessageDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.entity.enums.ReadStatus;
import ch.zhaw.fswd.powerdate.repository.ChatRepository;
import ch.zhaw.fswd.powerdate.repository.MessageRepository;
import ch.zhaw.fswd.powerdate.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ChatController {
    private final ChatRepository chatRepository;
    private final ProfileRepository profileRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ChatController(ChatRepository chatRepository, ProfileRepository profileRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.profileRepository = profileRepository;
        this.messageRepository = messageRepository;
    }

    public List<ChatDto> getChatsByUserId(UUID uuid) {
        ProfileDbo profileDbo = profileRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("No user found with given UUID"));
        List<ChatDbo> chatDbos = findChatsByProfile(profileDbo);
        List<ChatDto> result = new ArrayList<>();
        for (ChatDbo chatDbo : chatDbos) {
            ProfileDbo chatWith = !chatDbo.getParticipantOne().getUuid().equals(uuid) ? chatDbo.getParticipantOne() : chatDbo.getParticipantTwo();
            result.add(new ChatDto(chatDbo.getUuid(), chatWith.getDisplayName(), chatWith.getUuid(), chatWith.getRawPNGImageData()));
        }
        return result;
    }

    public List<MessageDto> getMessagesByChatId(UUID uuid) {
        ChatDbo chat = chatRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        List<MessageDbo> messageDbos = messageRepository.findByChatOrderByTimestampAsc(chat);
        messageDbos = markMessagesAsRead(messageDbos);
        List<MessageDto> result = new ArrayList<>();
        messageDbos.forEach(dbo -> result.add(new MessageDto().fromDbo(dbo)));
        return result;
    }

    public void saveNewMessage(NewMessageDto newMessageDto) {
        ChatDbo chat = chatRepository.findById(newMessageDto.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        ProfileDbo sender = profileRepository.findById(newMessageDto.getSenderUUID())
                .orElseThrow(() -> new EntityNotFoundException("Sender profile not found"));

        MessageDbo newMessage = new MessageDbo();
        newMessage.setChat(chat);
        newMessage.setSender(sender);
        newMessage.setContent(newMessageDto.getContent());
        newMessage.setMessageType(newMessageDto.getMessageType());
        newMessage.setTimestamp(LocalDateTime.now());
        newMessage.setReadStatus(ReadStatus.SENT);
        messageRepository.save(newMessage);
    }

    public Integer getAmountUnreadChats(UUID userUuid) {
        ProfileDbo profileDbo = profileRepository.findById(userUuid).orElseThrow(() -> new EntityNotFoundException("No user found with given UUID"));
        List<ChatDbo> chats = findChatsByProfile(profileDbo);
        List<MessageDbo> messagesNotFromUser = messageRepository.findByChatInAndReadStatusAndSenderNot(chats, ReadStatus.SENT, profileDbo);
        return messagesNotFromUser.size();
    }

    public void createChat(UUID participantOne, UUID participantTwo) {
        ProfileDbo profileOneDbo = profileRepository.findById(participantOne).orElseThrow(EntityNotFoundException::new);
        ProfileDbo profileTwoDbo = profileRepository.findById(participantTwo).orElseThrow(EntityNotFoundException::new);

        if (isMessageExisting(profileOneDbo, profileTwoDbo)) {
            return; // Chat already exists, do nothing
        }
        chatRepository.save(new ChatDbo(profileOneDbo, profileTwoDbo, false));
    }

    private boolean isMessageExisting(ProfileDbo participantOne, ProfileDbo participantTwo) {
        List<ChatDbo> chats = new ArrayList<>();
        chats.addAll(chatRepository.findByParticipantOne(participantOne));
        chats.addAll(chatRepository.findByParticipantOne(participantTwo));
        chats.addAll(chatRepository.findByParticipantTwo(participantOne));
        chats.addAll(chatRepository.findByParticipantTwo(participantTwo));

        return chats.stream().anyMatch(chat ->
                (chat.getParticipantOne().equals(participantOne) && chat.getParticipantTwo().equals(participantTwo)) ||
                        (chat.getParticipantOne().equals(participantTwo) && chat.getParticipantTwo().equals(participantOne))
        );
    }

    private List<ChatDbo> findChatsByProfile(ProfileDbo profileDbo) {
        List<ChatDbo> chatsOne = chatRepository.findByParticipantOne(profileDbo);
        List<ChatDbo> chatsTwo = chatRepository.findByParticipantTwo(profileDbo);
        List<ChatDbo> result = new ArrayList<>();
        result.addAll(chatsOne);
        result.addAll(chatsTwo);
        return result;
    }

    private List<MessageDbo> markMessagesAsRead(List<MessageDbo> dbos) {
        dbos.forEach(dbo -> dbo.setReadStatus(ReadStatus.READ));
        return messageRepository.saveAll(dbos);
    }
}
