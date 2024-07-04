package ch.zhaw.fswd.powerdate.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor()
@NoArgsConstructor
public class MismatchDbo extends BaseDbo {

    @ManyToOne
    private ProfileDbo mismatcherId;

    @ManyToOne
    private ProfileDbo mismatcheeId;

    private LocalDate date;

}
