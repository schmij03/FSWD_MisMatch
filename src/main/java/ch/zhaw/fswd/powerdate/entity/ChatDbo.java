package ch.zhaw.fswd.powerdate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatDbo extends BaseDbo {
    @ManyToOne
    private ProfileDbo participantOne;

    @ManyToOne
    private ProfileDbo participantTwo;

    private boolean deleted;
}
