package ch.zhaw.fswd.powerdate.repository;

import java.util.UUID;

import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ProfileRepository extends JpaRepository<ProfileDbo, UUID>{
    List<ProfileDbo> findByUuidIn(List<UUID> uuid);
}
