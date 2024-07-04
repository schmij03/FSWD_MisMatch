package ch.zhaw.fswd.powerdate.repository;

import ch.zhaw.fswd.powerdate.entity.InterestDbo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<InterestDbo, Long> {
    List<InterestDbo> findByIdIn(List<Long> ids);
}
