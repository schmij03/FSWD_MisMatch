package ch.zhaw.fswd.powerdate.repository;

import ch.zhaw.fswd.powerdate.entity.UserDbo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserDbo, String> {

}