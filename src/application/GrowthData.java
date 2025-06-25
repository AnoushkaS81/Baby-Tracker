package application;

import java.time.LocalDateTime;

public class GrowthData {
    private LocalDateTime time;
    private double weight;  // Weight in kilograms
    private double height;  // Height in centimeters

    public GrowthData(LocalDateTime time, double weight, double height) {
        this.time = time;
        this.weight = weight;
        this.height = height;
    }

    // Getters for growth data
    public LocalDateTime getTime() {
        return time;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Growth Data: " + "Weight: " + weight + " kg, Height: " + height + " cm at " + time;
    }
}
