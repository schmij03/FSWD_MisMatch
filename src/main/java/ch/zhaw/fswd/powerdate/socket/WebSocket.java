package ch.zhaw.fswd.powerdate.socket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@Scope("singleton")
public class WebSocket extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        sessions.remove(session);
        super.afterConnectionClosed(session, status);
    }

    public void send(String message) {
        List<WebSocketSession> closedSessions = new ArrayList<>();
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
                } catch (Exception e) {
                    closedSessions.add(session);
                }
            } else {
                closedSessions.add(session);
            }
            sessions.removeAll(closedSessions);
        }
    }
}