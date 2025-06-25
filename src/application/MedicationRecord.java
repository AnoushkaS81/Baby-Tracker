package application;

import java.time.LocalDateTime;

public class MedicationRecord {
    private LocalDateTime time;
    private String name;
    private double dosage;

    public MedicationRecord(LocalDateTime time, String name, double dosage) {
        this.time = time;
        this.name = name;
        this.dosage = dosage;
    }

    // Getters for medication record
    public LocalDateTime getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public double getDosage() {
        return dosage;
    }

    @Override
    public String toString() {
        return "Medication: " + name + " at " + time + ", Dosage: " + dosage + " mg";
    }

}
