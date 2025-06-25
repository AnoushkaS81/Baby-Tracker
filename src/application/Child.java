package application;

import java.util.ArrayList;

public class Child {
    private String name;
    private ArrayList<SleepSession> sleepRecords;
    private ArrayList<FeedingRecord> feedingRecords;
    private ArrayList<MedicationRecord> medicationRecords;
    private ArrayList<DiaperChangeRecord> diaperChangeRecords;  // New list for diaper change records
    private ArrayList<GrowthData> growthRecords;  // New list for growth records

    public Child(String name) {
        this.name = name;
        this.sleepRecords = new ArrayList<>();
        this.feedingRecords = new ArrayList<>();
        this.medicationRecords = new ArrayList<>();
        this.diaperChangeRecords = new ArrayList<>();
        this.growthRecords = new ArrayList<>();
    }

    // Getter methods
    public String getName() {
        return name;
    }
    public ArrayList<FeedingRecord> getFeedingRecords() {
        return feedingRecords;
    }
    public ArrayList<SleepSession> getSleepRecords() {
        return sleepRecords;
    }
    public ArrayList<MedicationRecord> getMedicationRecords() {
        return medicationRecords;
    }
    public ArrayList<DiaperChangeRecord> getDiaperChangeRecords() {  // Getter for diaper change records
        return diaperChangeRecords;
    }
    public ArrayList<GrowthData> getGrowthRecords() {  // Getter for growth records
        return growthRecords;
    }
    // Methods to add records
    public void addSleepRecord(SleepSession session) {
        sleepRecords.add(session);
    }
    public void addFeedingRecord(FeedingRecord record) {
        feedingRecords.add(record);
    }
    public void addMedicationRecord(MedicationRecord record) {
        medicationRecords.add(record);
    }
    public void addDiaperChangeRecord(DiaperChangeRecord record) {  // Method to add diaper change record
        diaperChangeRecords.add(record);
    }
    public void addGrowthData(GrowthData record) {  // Method to add growth record
        growthRecords.add(record);
    }
    // Methods to delete records.
    // Based around index in ArrayList, rather than the record object itself.
    // This is for simplicity, since it will always match table row index in ChildProfile.
    public void deleteFeedingRecord(int index) {
        feedingRecords.remove(index);
    }
    public void deleteSleepRecord(int index) {
        sleepRecords.remove(index);
    }
    public void deleteMedicationRecord(int index) {
        medicationRecords.remove(index);
    }
    public void deleteDiaperChangeRecord(int index) {
        diaperChangeRecords.remove(index);
    }
    public void deleteGrowthRecord(int index) {
        growthRecords.remove(index);
    }
}

