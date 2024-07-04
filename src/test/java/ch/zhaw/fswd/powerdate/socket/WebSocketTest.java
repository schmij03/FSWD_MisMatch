package ch.zhaw.fswd.powerdate.socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

class WebSocketTest {

    @InjectMocks
    private WebSocket webSocket;

    @Mock
    private WebSocketSession session;

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
    void afterConnectionEstablished_ShouldAddSession() throws Exception {
        // Act
        webSocket.afterConnectionEstablished(session);

        // Assert
        List<WebSocketSession> sessions = new ArrayList<>();
        sessions.add(session);
        Assertions.assertEquals(sessions, getSessions());
    }

    @Test
    void afterConnectionClosed_ShouldRemoveSession() throws Exception {
        // Arrange
        webSocket.afterConnectionEstablished(session);

        // Act
        webSocket.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Assert
        Assertions.assertTrue(getSessions().isEmpty());
    }

    @SuppressWarnings("unchecked")
    private List<WebSocketSession> getSessions() {
        Object sessionsObj = ReflectionTestUtils.getField(webSocket, "sessions");
        if (sessionsObj instanceof List<?>) {
            return (List<WebSocketSession>) sessionsObj;
        }
        throw new IllegalStateException("Expected sessions field to be a List<WebSocketSession>");
    }
}