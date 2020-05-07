package cz.mendelu.dnaAnalyser.sequence;

import java.util.UUID;

/**
 * Created by Jan Kolomazník on 25.6.17.
 */
public class Sequence {

    private UUID bufferId;

    private SequenceType type;

    private Boolean circular;

    public Sequence(String bufferId, SequenceType type, Boolean circular) {
        this.bufferId = UUID.fromString(bufferId);
        this.type = type;
        this.circular = circular;
    }

    public UUID getBufferId() {
        return bufferId;
    }

    public SequenceType getType() {
        return type;
    }

    public Boolean getCircular() {
        return circular;
    }

    @Override
    public String toString() {
        return "Sequence{" +
                "bufferId=" + bufferId +
                ", type=" + type +
                ", circular=" + circular +
                '}';
    }
}
