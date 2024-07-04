package ch.zhaw.fswd.powerdate.repository;

import ch.zhaw.fswd.powerdate.entity.ChatDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<ChatDbo, UUID> {
    List<ChatDbo> findByParticipantOne(ProfileDbo participantOne);
    List<ChatDbo> findByParticipantTwo(ProfileDbo participantTwo);
}
