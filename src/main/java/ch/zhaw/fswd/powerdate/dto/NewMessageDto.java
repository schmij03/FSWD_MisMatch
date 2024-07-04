package ch.zhaw.fswd.powerdate.dto;

import ch.zhaw.fswd.powerdate.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMessageDto {
    private UUID chatId;
    private String content;
    private MessageType messageType;
    private UUID senderUUID;
}
