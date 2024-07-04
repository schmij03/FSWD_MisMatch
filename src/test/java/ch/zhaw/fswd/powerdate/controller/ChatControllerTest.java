package ch.zhaw.fswd.powerdate.controller;

import ch.zhaw.fswd.powerdate.dto.ChatDto;
import ch.zhaw.fswd.powerdate.dto.MessageDto;
import ch.zhaw.fswd.powerdate.dto.NewMessageDto;
import ch.zhaw.fswd.powerdate.entity.ChatDbo;
import ch.zhaw.fswd.powerdate.entity.MessageDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.entity.enums.MessageType;
import ch.zhaw.fswd.powerdate.entity.enums.ReadStatus;
import ch.zhaw.fswd.powerdate.repository.ChatRepository;
import ch.zhaw.fswd.powerdate.repository.MessageRepository;
import ch.zhaw.fswd.powerdate.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

class ChatControllerTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ChatController chatController;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    @Test
    void getChatsByUserId_UserWithChats_ReturnsListOfChats() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ProfileDbo userProfile = new ProfileDbo();
        userProfile.setUuid(userId);
        userProfile.setDisplayName("User");

        ProfileDbo otherProfile1 = new ProfileDbo();
        otherProfile1.setUuid(UUID.randomUUID());
        otherProfile1.setDisplayName("Other1");
        otherProfile1.setRawPNGImageData("image1");

        ProfileDbo otherProfile2 = new ProfileDbo();
        otherProfile2.setUuid(UUID.randomUUID());
        otherProfile2.setDisplayName("Other2");
        otherProfile2.setRawPNGImageData("image2");

        ChatDbo chat1 = new ChatDbo();
        chat1.setUuid(UUID.randomUUID());
        chat1.setParticipantOne(userProfile);
        chat1.setParticipantTwo(otherProfile1);

        ChatDbo chat2 = new ChatDbo();
        chat2.setUuid(UUID.randomUUID());
        chat2.setParticipantOne(otherProfile2);
        chat2.setParticipantTwo(userProfile);

        Mockito.when(profileRepository.findById(userId)).thenReturn(Optional.of(userProfile));
        Mockito.when(chatRepository.findByParticipantOne(userProfile)).thenReturn(new ArrayList<>(List.of(chat1)));
        Mockito.when(chatRepository.findByParticipantTwo(userProfile)).thenReturn(new ArrayList<>(List.of(chat2)));

        // Act
        List<ChatDto> result = chatController.getChatsByUserId(userId);

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(otherProfile1.getDisplayName(), result.get(0).getDisplayName());
        Assertions.assertEquals(otherProfile1.getRawPNGImageData(), result.get(0).getRawPNGImageData());
        Assertions.assertEquals(otherProfile2.getDisplayName(), result.get(1).getDisplayName());
        Assertions.assertEquals(otherProfile2.getRawPNGImageData(), result.get(1).getRawPNGImageData());

        Mockito.verify(profileRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(chatRepository, Mockito.times(1)).findByParticipantOne(userProfile);
        Mockito.verify(chatRepository, Mockito.times(1)).findByParticipantTwo(userProfile);
    }

    @Test
    void getChatsByUserId_UserWithNoChats_ReturnsEmptyList() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ProfileDbo userProfile = new ProfileDbo();
        userProfile.setUuid(userId);

        Mockito.when(profileRepository.findById(userId)).thenReturn(Optional.of(userProfile));
        Mockito.when(chatRepository.findByParticipantOne(userProfile)).thenReturn(new ArrayList<>());
        Mockito.when(chatRepository.findByParticipantTwo(userProfile)).thenReturn(new ArrayList<>());

        // Act
        List<ChatDto> result = chatController.getChatsByUserId(userId);

        // Assert
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(profileRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(chatRepository, Mockito.times(1)).findByParticipantOne(userProfile);
        Mockito.verify(chatRepository, Mockito.times(1)).findByParticipantTwo(userProfile);
    }

    @Test
    void getMessagesByChatId_ChatWithNoMessages_ReturnsEmptyList() {
        // Arrange
        UUID chatId = UUID.randomUUID();
        ChatDbo chatDbo = new ChatDbo();
        chatDbo.setUuid(chatId);

        Mockito.when(chatRepository.findById(chatId)).thenReturn(Optional.of(chatDbo));
        Mockito.when(messageRepository.findByChatOrderByTimestampAsc(chatDbo)).thenReturn(new ArrayList<>());

        // Act
        List<MessageDto> result = chatController.getMessagesByChatId(chatId);

        // Assert
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(chatRepository, Mockito.times(1)).findById(chatId);
        Mockito.verify(messageRepository, Mockito.times(1)).findByChatOrderByTimestampAsc(chatDbo);
    }

    @Test
    void saveNewMessage_ValidMessage_MessageSaved() {
        // Arrange
        UUID chatId = UUID.randomUUID();
        UUID senderUUID = UUID.randomUUID();

        ChatDbo chatDbo = new ChatDbo();
        chatDbo.setUuid(chatId);

        ProfileDbo senderProfile = new ProfileDbo();
        senderProfile.setUuid(senderUUID);

        NewMessageDto newMessageDto = new NewMessageDto();
        newMessageDto.setContent("Test message");
        newMessageDto.setMessageType(MessageType.TEXT);
        newMessageDto.setSenderUUID(senderUUID);
        newMessageDto.setChatId(chatId);

        Mockito.when(chatRepository.findById(chatId)).thenReturn(Optional.of(chatDbo));
        Mockito.when(profileRepository.findById(senderUUID)).thenReturn(Optional.of(senderProfile));

        // Act
        chatController.saveNewMessage(newMessageDto);

        // Assert
        Mockito.verify(chatRepository, Mockito.times(1)).findById(chatId);
        Mockito.verify(profileRepository, Mockito.times(1)).findById(senderUUID);
        Mockito.verify(messageRepository, Mockito.times(1)).save(Mockito.any(MessageDbo.class));
    }

    @Test
    void saveNewMessage_ChatNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        UUID chatId = UUID.randomUUID();
        NewMessageDto newMessageDto = new NewMessageDto();
        newMessageDto.setChatId(chatId);

        Mockito.when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> chatController.saveNewMessage(newMessageDto));

        Mockito.verify(chatRepository, Mockito.times(1)).findById(chatId);
        Mockito.verify(profileRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(messageRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveNewMessage_SenderNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        UUID chatId = UUID.randomUUID();
        UUID senderUUID = UUID.randomUUID();

        ChatDbo chatDbo = new ChatDbo();
        chatDbo.setUuid(chatId);

        NewMessageDto newMessageDto = new NewMessageDto();
        newMessageDto.setSenderUUID(senderUUID);
        newMessageDto.setChatId(chatId);

        Mockito.when(chatRepository.findById(chatId)).thenReturn(Optional.of(chatDbo));
        Mockito.when(profileRepository.findById(senderUUID)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> chatController.saveNewMessage(newMessageDto));

        Mockito.verify(chatRepository, Mockito.times(1)).findById(chatId);
        Mockito.verify(profileRepository, Mockito.times(1)).findById(senderUUID);
        Mockito.verify(messageRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getAmountUnreadChats_UserWithUnreadMessages_ReturnsCorrectCount() {
        // Arrange
        UUID userUuid = UUID.randomUUID();
        ProfileDbo profileDbo = new ProfileDbo();
        profileDbo.setUuid(userUuid);

        ChatDbo chat1 = new ChatDbo();
        ChatDbo chat2 = new ChatDbo();
        List<ChatDbo> chats = Arrays.asList(chat1, chat2);

        MessageDbo unreadMessage1 = new MessageDbo();
        MessageDbo unreadMessage2 = new MessageDbo();
        MessageDbo unreadMessage3 = new MessageDbo();
        List<MessageDbo> unreadMessages = Arrays.asList(unreadMessage1, unreadMessage2, unreadMessage3);

        Mockito.when(profileRepository.findById(userUuid)).thenReturn(Optional.of(profileDbo));
        Mockito.when(chatRepository.findByParticipantOne(profileDbo)).thenReturn(List.of(chat1));
        Mockito.when(chatRepository.findByParticipantTwo(profileDbo)).thenReturn(List.of(chat2));
        Mockito.when(messageRepository.findByChatInAndReadStatusAndSenderNot(chats, ReadStatus.SENT, profileDbo))
                .thenReturn(unreadMessages);

        // Act
        Integer result = chatController.getAmountUnreadChats(userUuid);

        // Assert
        Assertions.assertEquals(3, result);
        Mockito.verify(profileRepository, Mockito.times(1)).findById(userUuid);
        Mockito.verify(chatRepository, Mockito.times(1)).findByParticipantOne(profileDbo);
        Mockito.verify(chatRepository, Mockito.times(1)).findByParticipantTwo(profileDbo);
        Mockito.verify(messageRepository, Mockito.times(1))
                .findByChatInAndReadStatusAndSenderNot(chats, ReadStatus.SENT, profileDbo);
    }

    @Test
    void getAmountUnreadChats_UserNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        UUID userUuid = UUID.randomUUID();
        Mockito.when(profileRepository.findById(userUuid)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> chatController.getAmountUnreadChats(userUuid));
        Mockito.verify(profileRepository, Mockito.times(1)).findById(userUuid);
        Mockito.verify(chatRepository, Mockito.never()).findByParticipantOne(Mockito.any());
        Mockito.verify(chatRepository, Mockito.never()).findByParticipantTwo(Mockito.any());
        Mockito.verify(messageRepository, Mockito.never()).findByChatInAndReadStatusAndSenderNot(Mockito.any(), Mockito.any(), Mockito.any());
    }
}