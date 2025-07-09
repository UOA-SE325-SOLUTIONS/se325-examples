package se325.example16.parolee.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se325.example16.parolee.jackson.websocket.JSONCoder;

import java.util.Objects;

/**
 * A DTO to be sent by a WebSocket server back to a client who wishes to be notified whenever parolees with a certain
 * id move about.
 */
public class ParoleeMovementNotificationDTO {

    public static class Coder extends JSONCoder<ParoleeMovementNotificationDTO> {
    }

    private long paroleeId;
    private MovementDTO movement;

    @JsonCreator
    public ParoleeMovementNotificationDTO(@JsonProperty("paroleeId") long paroleeId,
                                          @JsonProperty("movement") MovementDTO movement) {
        this.paroleeId = paroleeId;
        this.movement = movement;
    }

    public long getParoleeId() {
        return paroleeId;
    }

    public MovementDTO getMovement() {
        return movement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParoleeMovementNotificationDTO that = (ParoleeMovementNotificationDTO) o;
        return paroleeId == that.paroleeId && movement.equals(that.movement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paroleeId, movement);
    }
}
