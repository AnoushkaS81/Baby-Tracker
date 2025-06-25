package application;

import java.time.Duration;
import java.time.LocalDateTime;

public class SleepSession {
    private LocalDateTime recordTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public SleepSession(LocalDateTime recordTime, LocalDateTime startTime, LocalDateTime endTime) {
        this.recordTime = recordTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Updated to return the number of hours, since default duration format is unhelpful.
    // toHours() returns a long value, not an int, so just make this getter return that too.
    public long getDuration() {
        return Duration.between(startTime, endTime).toHours();
    }

    // Getters for sleep session
    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Slept from " + startTime + " to " + endTime + ", Duration: " + getDuration() + " hours";
    }

}
