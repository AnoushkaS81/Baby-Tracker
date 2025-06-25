package application;

import java.time.LocalDateTime;

// New class for diaper change records
public class DiaperChangeRecord {
    private LocalDateTime time;
    private String type; // E.g., "Wet", "Soiled", etc.

    public DiaperChangeRecord(LocalDateTime time, String type) {
        this.time = time;
        this.type = type;
    }

    // Getters for diaper change record
    public LocalDateTime getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Diaper Change: " + type + " at " + time;
    }

}
