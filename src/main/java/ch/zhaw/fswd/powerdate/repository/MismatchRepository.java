package ch.zhaw.fswd.powerdate.repository;

import ch.zhaw.fswd.powerdate.entity.MismatchDbo;

import java.util.UUID;

import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDate;

public interface MismatchRepository extends JpaRepository<MismatchDbo, UUID> {
    List<MismatchDbo> findByDate(LocalDate date);

    @Query("SELECT m.uuid FROM MismatchDbo m WHERE m.date = :date AND m.mismatcherId = :mismatcherId")
    List<UUID> findUuidsByDateAndMismatcherId(@Param("date") LocalDate date, @Param("mismatcherId") ProfileDbo mismatcherId);

    @Query("SELECT m.mismatcheeId FROM MismatchDbo m WHERE m.uuid = :uuid")
    ProfileDbo findMismatcheeByUuid(@Param("uuid") UUID uuid);
}