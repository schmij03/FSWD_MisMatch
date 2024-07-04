package ch.zhaw.fswd.powerdate.repository;

import ch.zhaw.fswd.powerdate.entity.ChatDbo;
import ch.zhaw.fswd.powerdate.entity.MessageDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.entity.enums.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageDbo, UUID> {
    List<MessageDbo> findByChatOrderByTimestampAsc(ChatDbo chat);
    List<MessageDbo> findByChatInAndReadStatusAndSenderNot(List<ChatDbo> chatDbo, ReadStatus readStatus, ProfileDbo sender);
}
