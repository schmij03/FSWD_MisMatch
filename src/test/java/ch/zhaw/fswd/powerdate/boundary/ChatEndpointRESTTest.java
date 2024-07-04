package ch.zhaw.fswd.powerdate.boundary;

import ch.zhaw.fswd.powerdate.controller.ChatController;
import ch.zhaw.fswd.powerdate.dto.ChatDto;
import ch.zhaw.fswd.powerdate.dto.MessageDto;
import ch.zhaw.fswd.powerdate.dto.NewMessageDto;
import ch.zhaw.fswd.powerdate.entity.enums.MessageType;
import ch.zhaw.fswd.powerdate.entity.enums.ReadStatus;
import ch.zhaw.fswd.powerdate.socket.WebSocket;
import jakarta.persistence.EntityNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

class ChatEndpointRESTTest {

    @Mock
    private ChatController chatController;

    @Mock
    private WebSocket webSocket;

    @InjectMocks
    private ChatEndpointREST chatEndpointREST;

    private EasyRandom generator;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        generator = new EasyRandom();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getChatsByUserId_ShouldReturnListOfChats() {
        // Arrange
        UUID userId = UUID.fromString("36a3119d-3fa6-49de-a510-eabfcc7222f2");
        List<ChatDto> expectedChats = Arrays.asList(
                new ChatDto(UUID.randomUUID(), "user1", UUID.randomUUID(), ""),
                new ChatDto(UUID.randomUUID(), "user1", UUID.randomUUID(), "")
        );

        Mockito.when(chatController.getChatsByUserId(userId)).thenReturn(expectedChats);

        // Act
        ResponseEntity<List<ChatDto>> response = chatEndpointREST.getChatsByUserId(userId);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedChats, response.getBody());
    }

    @Test
    void getChatsByUserId_ShouldReturnEmptyList_WhenNoChatsFound() {
        // Arrange
        UUID userId = UUID.fromString("36a3119d-3fa6-49de-a510-eabfcc7222f4");
        List<ChatDto> emptyList = List.of();
        Mockito.when(chatController.getChatsByUserId(userId)).thenReturn(emptyList);

        // Act
        ResponseEntity<List<ChatDto>> response = chatEndpointREST.getChatsByUserId(userId);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(emptyList, response.getBody());
    }

    @Test
    void getMessagesByChatId_ShouldReturnListOfMessages() {
        // Arrange
        UUID chatId = UUID.randomUUID();
        List<MessageDto> expectedMessages = Arrays.asList(
                new MessageDto("Hello", LocalDateTime.now(), ReadStatus.READ, MessageType.TEXT, UUID.randomUUID()),
                new MessageDto("Hi there!", LocalDateTime.now().plusMinutes(1), ReadStatus.SENT, MessageType.TEXT, UUID.randomUUID())
        );
        Mockito.when(chatController.getMessagesByChatId(chatId)).thenReturn(expectedMessages);

        // Act
        ResponseEntity<List<MessageDto>> response = chatEndpointREST.getMessagesByChatId(chatId);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedMessages, response.getBody());
    }

    @Test
    void getMessagesByChatId_ShouldReturnEmptyList_WhenNoMessagesFound() {
        // Arrange
        UUID chatId = UUID.randomUUID();
        List<MessageDto> emptyList = List.of();
        Mockito.when(chatController.getMessagesByChatId(chatId)).thenReturn(emptyList);

        // Act
        ResponseEntity<List<MessageDto>> response = chatEndpointREST.getMessagesByChatId(chatId);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(emptyList, response.getBody());
    }

    @Test
    void sendMessage_ShouldReturnOkResponse() {
        // Arrange
        NewMessageDto newMessageDto = generator.nextObject(NewMessageDto.class);

        // Act
        ResponseEntity<Void> response = chatEndpointREST.sendMessage(newMessageDto);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNull(response.getBody());

        // Verify
        Mockito.verify(chatController, Mockito.times(1)).saveNewMessage(newMessageDto);
        Mockito.verify(webSocket, Mockito.times(1)).send("New message received");
    }

    @Test
    void getAmountUnreadChats_ShouldReturnOkResponse() {
        // Arrange
        UUID someUuid = UUID.randomUUID();
        Integer expectedResult = 5;

        Mockito.when(chatController.getAmountUnreadChats(someUuid)).thenReturn(expectedResult);

        // Act
        ResponseEntity<Integer> response = chatEndpointREST.getAmountUnreadChats(someUuid);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(response.getBody(), expectedResult);

        // Verify
        Mockito.verify(chatController, Mockito.times(1)).getAmountUnreadChats(someUuid);
    }

    @Test
    void getAmountUnreadChats_ShouldReturnNotFound_WhenEntityNotFound() {
        // Arrange
        UUID nonExistentUuid = UUID.randomUUID();
        Mockito.when(chatController.getAmountUnreadChats(nonExistentUuid))
                .thenThrow(new EntityNotFoundException("User not found"));

        // Act
        ResponseEntity<Integer> response = chatEndpointREST.getAmountUnreadChats(nonExistentUuid);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());

        // Verify
        Mockito.verify(chatController, Mockito.times(1)).getAmountUnreadChats(nonExistentUuid);
    }
}