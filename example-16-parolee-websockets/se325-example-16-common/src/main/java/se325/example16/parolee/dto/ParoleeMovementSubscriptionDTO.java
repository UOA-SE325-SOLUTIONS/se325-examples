package se325.example16.parolee.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se325.example16.parolee.jackson.websocket.JSONCoder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Sent via WebSocket clients through to the service. Will let the server know this client is interested to hear
 * about when all parolees with id matching those in the given {@link #paroleeIds} list move about.
 */
public class ParoleeMovementSubscriptionDTO {

    public static class Coder extends JSONCoder<ParoleeMovementSubscriptionDTO> {

    }

    private final List<Long> paroleeIds;

    @JsonCreator
    public ParoleeMovementSubscriptionDTO(@JsonProperty("paroleeIds") List<Long> ids) {
        this.paroleeIds = ids;
    }

    public ParoleeMovementSubscriptionDTO(Long... ids) {
        this.paroleeIds = List.of(ids);
    }

    public boolean isInterestedIn(long paroleeId) {
        return this.paroleeIds.contains(paroleeId);
    }

    public List<Long> getParoleeIds() {
        return Collections.unmodifiableList(paroleeIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParoleeMovementSubscriptionDTO that = (ParoleeMovementSubscriptionDTO) o;
        return paroleeIds.equals(that.paroleeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paroleeIds);
    }
}
