package application;

import java.time.LocalDateTime;

public class FeedingRecord {
    private LocalDateTime time;
    private String type;
    private double amount;

    public FeedingRecord(LocalDateTime time, String type, double amount) {
        this.time = time;
        this.type = type;
        this.amount = amount;
    }

    // Getters for feeding record
    public LocalDateTime getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Feeding: " + type + " at " + time + ", Amount: " + amount + " ml";
    }

}
