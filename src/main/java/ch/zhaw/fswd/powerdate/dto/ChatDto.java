package ch.zhaw.fswd.powerdate.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private UUID uuid;
    private String displayName;
    private UUID chatWithUUID;
    private String rawPNGImageData;
}
