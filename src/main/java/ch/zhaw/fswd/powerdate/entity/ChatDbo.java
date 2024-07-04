package ch.zhaw.fswd.powerdate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChatDbo extends BaseDbo {
    @ManyToOne
    private ProfileDbo participantOne;

    @ManyToOne
    private ProfileDbo participantTwo;

    private boolean deleted;
}
