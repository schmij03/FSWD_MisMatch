package ch.zhaw.fswd.powerdate.dto;

import ch.zhaw.fswd.powerdate.entity.MessageDbo;
import ch.zhaw.fswd.powerdate.entity.enums.MessageType;
import ch.zhaw.fswd.powerdate.entity.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    String content;
    LocalDateTime sentAt;
    ReadStatus readStatus;
    MessageType messageType;
    UUID senderUuid;

    public MessageDto fromDbo(MessageDbo dbo) {
        this.content = dbo.getContent();
        this.sentAt = dbo.getTimestamp();
        this.readStatus = dbo.getReadStatus();
        this.messageType = dbo.getMessageType();
        this.senderUuid = dbo.getSender().getUuid();
        return this;
    }
}
