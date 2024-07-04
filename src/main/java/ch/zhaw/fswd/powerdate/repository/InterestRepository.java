package ch.zhaw.fswd.powerdate.repository;

import ch.zhaw.fswd.powerdate.entity.InterestDbo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<InterestDbo, Long> {
}
