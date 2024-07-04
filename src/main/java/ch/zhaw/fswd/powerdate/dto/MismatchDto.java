package ch.zhaw.fswd.powerdate.dto;

import ch.zhaw.fswd.powerdate.entity.MismatchDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MismatchDto {
    UUID matchUuid;
    ProfileDbo matcher;
    ProfileDbo matchee;
    LocalDate date;

    public MismatchDto fromDbo(MismatchDbo dbo) {
        this.matchUuid = dbo.getUuid();
        this.matcher = dbo.getMismatcherId();
        this.matchee = dbo.getMismatcheeId();
        this.date = dbo.getDate();
        return this;
    }
}
