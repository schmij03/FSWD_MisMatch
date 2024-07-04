package ch.zhaw.fswd.powerdate.boundary;

import ch.zhaw.fswd.powerdate.controller.ChatController;
import ch.zhaw.fswd.powerdate.dto.ChatDto;
import ch.zhaw.fswd.powerdate.dto.MessageDto;
import ch.zhaw.fswd.powerdate.dto.NewMessageDto;
import ch.zhaw.fswd.powerdate.entity.ChatRequestDto;
import ch.zhaw.fswd.powerdate.socket.WebSocket;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatEndpointREST {
    private final ChatController chatController;
    private final WebSocket socket;

    @Autowired
    public ChatEndpointREST(ChatController chatController, WebSocket socket) {
        this.chatController = chatController;
        this.socket = socket;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<List<ChatDto>> getChatsByUserId(@PathVariable UUID uuid) {
        return ResponseEntity.ok().body(chatController.getChatsByUserId(uuid));
    }

    @GetMapping("/detail/{uuid}")
    public ResponseEntity<List<MessageDto>> getMessagesByChatId(@PathVariable UUID uuid) {
        return ResponseEntity.ok().body(chatController.getMessagesByChatId(uuid));
    }

    @PutMapping("/detail/")
    public ResponseEntity<Void> sendMessage(@RequestBody NewMessageDto newMessageDto) {
        chatController.saveNewMessage(newMessageDto);
        socket.send("New message received");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/amountUnreadChats/{userUuid}")
    public ResponseEntity<Integer> getAmountUnreadChats(@PathVariable UUID userUuid) {
        try {
            return ResponseEntity.ok(chatController.getAmountUnreadChats(userUuid));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/createChat")
    public ResponseEntity<Void> createChat(@RequestBody ChatRequestDto chatRequestDto) {
        try {
            chatController.createChat(chatRequestDto.getParticipantOne(), chatRequestDto.getParticipantTwo());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
