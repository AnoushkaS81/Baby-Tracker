package application.Functionality;

import application.*;

import org.jfree.chart.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

// This is a sister class of ReportGenerator which will generate the graphs.
// Again, all its methods are static for simplicity.
public class GraphGenerator {
    // Ensure the constructor is private and empty, to prevent instantiation of objects.
    private GraphGenerator() {}
    // Each method will generate an appropriate graph for each record type.
    // They will return a string, which will be the name of the image file.
    // That image file will then be put into the PDF document by ReportGenerator.
    // TODO: Support all record types.
    public static String feedingGraph(ArrayList<FeedingRecord> records) {
        // Set up the dataset for the graph.
        DefaultPieDataset dataset = new DefaultPieDataset();
        // Count the number of each food type by looping through the records.
        // We will use a frequency map (HashMap) to store the counts of each food type.
        HashMap<String, Integer> foodtypes = new HashMap<>();
        for (FeedingRecord fr : records) {
            foodtypes.put(fr.getType(), foodtypes.getOrDefault(fr.getType(), 0) + 1);
        }
        // Convert the map to the dataset.
        for (HashMap.Entry<String, Integer> entry : foodtypes.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        // Create the pie chart using the dataset.
        JFreeChart chart = ChartFactory.createPieChart("Frequency of food items fed", dataset, true, true, false);
        // Try to save the graph as an image file by using our helper method.
        File image = save(chart);
        // Return the image filename, or nothing if it was unsuccessful.
        if (image == null) {
            return null;
        }
        return image.getAbsolutePath();
    }
    public static String sleepGraph(ArrayList<SleepSession> records) {
        // Set up the dataset for the graph.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Go through each record and add it to the dataset.
        for (SleepSession sr : records) {
            // Only get the date of the record, not the time too, since it's not needed.
            dataset.addValue(sr.getDuration(), "Duration (hours)", sr.getRecordTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        // Create the line graph using the dataset.
        JFreeChart chart = ChartFactory.createLineChart("Sleep duration per night", "Date", "Duration (hours)", dataset);
        // Try to save the graph as an image file by using our helper method.
        File image = save(chart);
        // Return the image filename, or nothing if it was unsuccessful.
        if (image == null) {
            return null;
        }
        return image.getAbsolutePath();
    }
    public static String medicationGraph(ArrayList<MedicationRecord> records) {
        // Set up the dataset for the graph.
        DefaultPieDataset dataset = new DefaultPieDataset();
        // Count the number of each medication type by looping through the records.
        // We will use a frequency map (HashMap) to store the counts of each medication type.
        HashMap<String, Integer> medicationtypes = new HashMap<>();
        for (MedicationRecord mr : records) {
            medicationtypes.put(mr.getName(), medicationtypes.getOrDefault(mr.getName(), 0) + 1);
        }
        // Convert the map to the dataset.
        for (HashMap.Entry<String, Integer> entry : medicationtypes.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        // Create the pie chart using the dataset.
        JFreeChart chart = ChartFactory.createPieChart("Frequency of medication types given", dataset, true, true, false);
        // Try to save the graph as an image file by using our helper method.
        File image = save(chart);
        // Return the image filename, or nothing if it was unsuccessful.
        if (image == null) {
            return null;
        }
        return image.getAbsolutePath();
    }
    public static String diaperChangeGraph(ArrayList<DiaperChangeRecord> records) {
        // Set up the dataset for the graph.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Count the number of each nappy type by looping through the records.
        int soiledcount = 0;
        int wetcount = 0;
        int mixedcount = 0;
        for (DiaperChangeRecord dr : records) {
            switch (dr.getType()) {
                case "Soiled nappy":
                    soiledcount++;
                    break;
                case "Wet nappy":
                    wetcount++;
                    break;
                case "Mixed nappy":
                    mixedcount++;
                    break;
            }
        }
        // Set up the dataset for the counted values.
        dataset.addValue(soiledcount, "Occurrences", "Soiled nappy");
        dataset.addValue(wetcount, "Occurrences", "Wet nappy");
        dataset.addValue(mixedcount, "Occurrences", "Mixed nappy");
        // Create a bar chart using the dataset.
        JFreeChart chart = ChartFactory.createBarChart("Occurrences of soiled, wet and mixed nappies", "Nappy type", "Occurrences", dataset);
        // Try to save the graph as an image file by using our helper method.
        File image = save(chart);
        // Return the image filename, or nothing if it was unsuccessful.
        if (image == null) {
            return null;
        }
        return image.getAbsolutePath();
    }
    public static String growthRecordGraph(ArrayList<GrowthData> records) {
        // Set up the dataset for the graph.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (GrowthData gr : records) {
            dataset.addValue(gr.getWeight(), "Weight (kg)", String.valueOf(gr.getHeight()));
        }
        // Create the line graph using the dataset.
        JFreeChart chart = ChartFactory.createLineChart("Measured weight per height", "Height (cm)", "Weight (kg)", dataset);
        // Try to save the graph as an image file by using our helper method.
        File image = save(chart);
        // Return the image filename, or nothing if it was unsuccessful.
        if (image == null) {
            return null;
        }
        return image.getAbsolutePath();
    }
    // Helper method for writing the image to disk after the graph is set up.
    // This is used to avoid duplication of code.
    private static File save(JFreeChart chart) {
        // Set up the file object to use, ensuring it's out of the try/catch block.
        File image;
        // Try to save the graph as a PNG image file.
        try {
            // Set up the file. ReportGenerator will auto-delete it after it has been used.
            image = new File(AppStorage.getDirectory() + "/.tmp_graphgenerator.png");
            // Save the chart as a PNG image to the file.
            // Make it big and let ReportGenerator scale it down, to reduce blurriness.
            ChartUtils.saveChartAsPNG(image, chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Return the image file object.
        return image;
    }
}