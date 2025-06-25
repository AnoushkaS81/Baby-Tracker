package application.Functionality;

import application.*;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ReportGenerator {
    // Use a private and empty constructor, to ensure objects can't be instantiated.
    private ReportGenerator() {}
    // Generates a report based on the given type.
    // Returns a string, which will be the filename of the generated report.
    // Returns null on any problems.
    // It needs to get the ChildDatabase from the host, so it can find the reports.
    public static String generate(String child, String type, ChildDatabase childDatabase) {
        // Only support the given types.
        if (!type.equals("Feeding") && !type.equals("Sleep") && !type.equals("Medication") && !type.equals("Diaper Change") && !type.equals("Growth Record")) {
            return null;
        }
        // Get the date and time, which will be displayed in the heading.
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = now.format(DateTimeFormatter.ofPattern("HH.mm"));
        // Set up the folder to store the report, which will be 'Documents/Health Reports'.
        String folder = System.getProperty("user.home") + "/Documents/Health Reports/";
        // Determine what the filename shall be, based on collected details.
        String filename = folder + child + "_" + type + "_" + date + "_" + time + ".pdf";
        // Try to create the parent directories of the file if they don't already exist.
        new File(filename).getParentFile().mkdirs();
        // Set up the document.
        try {
            PDDocument doc = new PDDocument();
            // Add the main page to the document, using the A4 page size.
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            // Use the page size to determine the maximum coordinates we can use.
            // Note that the coordinates go from the bottom left, not the top left.
            float xmax = PDRectangle.A4.getWidth();
            float ymax = PDRectangle.A4.getHeight();
            // Set up the content stream for the page.
            PDPageContentStream content = new PDPageContentStream(doc, page);
            // Set the font for the heading (Helvetica Bold 16pt).
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
            // Start the heading text section.
            content.beginText();
            // Set the position to be based on 90% of the page; these are sensible margins.
            // The f after the number is needed because we are using floats, not doubles.
            content.newLineAtOffset(xmax * 0.1f, ymax * 0.9f);
            // Display the text.
            content.showText(child + "'s " + type + " report, generated on " + date + " at " + time);
            // End the heading text section.
            content.endText();
            // Set the font for the main text section (Helvetica 10pt).
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            // Begin the main text section.
            content.beginText();
            // Set the starting point for the text based on the heading, but slightly below.
            content.newLineAtOffset(xmax * 0.1f, (ymax * 0.9f) - 30);
            // Initial new line, to prevent first 2 lines overlaying each other.
            content.newLine();
            content.setLeading(14.5f);
            // Depending on the record type, loop through each record and write it as a line.
            int countlines = 0;
            switch (type) {
                case "Feeding":
                    for (FeedingRecord fr : childDatabase.getChild(child).getFeedingRecords()) {
                        content.showText(fr.toString());
                        content.newLine();
                        content.setLeading(14.5f);
                        countlines++;
                    }
                    break;
                case "Sleep":
                    for (SleepSession sr : childDatabase.getChild(child).getSleepRecords()) {
                        content.showText(sr.toString());
                        content.newLine();
                        content.setLeading(14.5f);
                        countlines++;
                    }
                    break;
                case "Medication":
                    for (MedicationRecord mr : childDatabase.getChild(child).getMedicationRecords()) {
                        content.showText(mr.toString());
                        content.newLine();
                        content.setLeading(14.5f);
                        countlines++;
                    }
                    break;
                case "Diaper Change":
                    for (DiaperChangeRecord dr : childDatabase.getChild(child).getDiaperChangeRecords()) {
                        content.showText(dr.toString());
                        content.newLine();
                        content.setLeading(14.5f);
                        countlines++;
                    }
                    break;
                case "Growth Record":
                    for (GrowthData gr : childDatabase.getChild(child).getGrowthRecords()) {
                        content.showText(gr.toString());
                        content.newLine();
                        content.setLeading(14.5f);
                        countlines++;
                    }
                    break;
            }
            // End this text area.
            content.endText();
            // Depending on the type, generate a graph.
            // Set to null initially, so we can deal with unsupported types, if needed.
            PDImageXObject graph = null;
            String graphfile = null;
            switch (type) {
                case "Feeding":
                    graphfile = GraphGenerator.feedingGraph(childDatabase.getChild(child).getFeedingRecords());
                    break;
                case "Sleep":
                    graphfile = GraphGenerator.sleepGraph(childDatabase.getChild(child).getSleepRecords());
                    break;
                case "Medication":
                    graphfile = GraphGenerator.medicationGraph(childDatabase.getChild(child).getMedicationRecords());
                    break;
                case "Diaper Change":
                    graphfile = GraphGenerator.diaperChangeGraph(childDatabase.getChild(child).getDiaperChangeRecords());
                    break;
                case "Growth Record":
                    graphfile = GraphGenerator.growthRecordGraph(childDatabase.getChild(child).getGrowthRecords());
                    break;
            }
            // Only set up the graph if the generation completed successfully.
            if (graphfile != null) {
                graph = PDImageXObject.createFromFile(graphfile, doc);
                // Delete the raw image file after it has been loaded as a PDImageXObject.
                new File(graphfile).delete();
            }
            // Add the graph image to the document, if generation was successful.
            if (graph != null) {
                // Draw the image at the center, and below the text from above.
                // The height is found by accounting for how many record entries there were.
                content.drawImage(
                        // Draw the graph itself.
                        graph,
                        // Center the image by finding middle X point minus 1/4 of image width.
                        // This is in order to account for the fact that we are scaling to half.
                        (xmax * 0.5f) - (graph.getWidth() * 0.25f),
                        // Display the image at a Y position determined by number of text lines.
                        // The second number is just an additional offset, to avoid overlaying text.
                        (ymax * 0.9f) - (countlines * 14.5f) - 350,
                        // Scale the image to half its size, so it fits on the page.
                        graph.getWidth() * 0.5f,
                        graph.getHeight() * 0.5f
                );
            }
            // Close the content, save the document, and then close the document.
            content.close();
            doc.save(filename);
            doc.close();
        } catch (IOException e) {
            // Print the stack trace for debugging, and then return null to indicate failure.
            e.printStackTrace();
            return null;
        }
        // Return the filename to indicate that it completed successfully.
        return filename;
    }
    // Deletes a report from the disk, given its filename.
    public static void delete(String filename) {
        // Set up the file path based on main docs directory, and then delete it.
        // This will return either true or false based on whether the deletion was successul.
        // We're just going to ignore that and assume it deleted successfully.
        new File(System.getProperty("user.home") + "/Documents/Health Reports/" + filename).delete();
    }
    // Gets a list of current reports, by scanning the 'Documents/Health Reports' directory.
    public static ArrayList<String> findexisting() {
        // Specify the folder which should contain the existing documents.
        File folder = new File(System.getProperty("user.home") + "/Documents/Health Reports/");
        // Create the parent directories of the file if needed, for redundancy purposes.
        folder.mkdirs();
        // Find a list of files that match.
        // Do this by setting up a FilenameFilter object, which we override to match only PDF files.
        File[] list = folder.listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
                return name.endsWith(".pdf");
            }
        });
        // Add each file to the ArrayList.
        ArrayList<String> arraylist = new ArrayList<>();
        for (File file : list) {
            arraylist.add(file.getName());
        }
        // Return the ArrayList.
        return arraylist;
    }
}
