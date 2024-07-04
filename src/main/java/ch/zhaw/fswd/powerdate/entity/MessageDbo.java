package ch.zhaw.fswd.powerdate.entity;

import ch.zhaw.fswd.powerdate.entity.enums.MessageType;
import ch.zhaw.fswd.powerdate.entity.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageDbo extends BaseDbo {
    @ManyToOne
    private ChatDbo chat;
    @ManyToOne
    private ProfileDbo sender;
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
}
