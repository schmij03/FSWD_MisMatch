package ch.zhaw.fswd.powerdate.entity;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDto {
    private UUID participantOne;
    private UUID participantTwo;

    public UUID getParticipantOne() {
        return participantOne;
    }

    public void setParticipantOne(UUID participantOne) {
        this.participantOne = participantOne;
    }

    public UUID getParticipantTwo() {
        return participantTwo;
    }

    public void setParticipantTwo(UUID participantTwo) {
        this.participantTwo = participantTwo;
    }
}
