package application.Functionality;

// We are required to use classes from the non-functionality part.
// Unless, of course, they get moved to functionality.
import application.*;

// Modules for file handling, file reading/writing, and supporting necessary data formats.
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

// This is the class that shall contain all the children per account.
// It again also handles saving data to the user's device.
public class ChildDatabase {
    // The name of the account using this database.
    private String owner;
    // The list of children for the account.
    private ArrayList<Child> children;
    private File childrenfile;
    public ChildDatabase(String owner) {
        this.owner = owner;
        this.children = new ArrayList<Child>();
        // Initialise the top-level file that will contain these.
        this.childrenfile = new File(AppStorage.getDirectory() + this.owner + "/children.dat");
        // We need to use try/catch because it can throw an IOException error.
        try {
            // Create parent directories if they don't already exist.
            this.childrenfile.getParentFile().mkdirs();
            // Again this returns true or false, but we are ignoring it.
            this.childrenfile.createNewFile();
        } catch (IOException e) {
            // Crash the program with a RuntimeException.
            throw new RuntimeException("Unable to initialise the accounts file.", e);
        }
        // Now loop through every child defined in the file and add it if found.
        try {
            // Set up a scanner to read through the lines of the file.
            Scanner childrenfileread = new Scanner(this.childrenfile);
            // Detect whether or not an account has been found.
            // Loop through while there are still lines in the file.
            // If the file is empty, this loop will be skipped entirely.
            while (childrenfileread.hasNextLine()) {
                // Read the line of the file (single line, so no need for splitting).
                String line = childrenfileread.nextLine();
                // Ensure the line is not empty.
                if (line.equals("")) {
                    // This is an invalid entry, skip over it.
                    continue;
                }
                // Create a new account in the database using the read values.
                this.children.add(new Child(line));
            }
            // Close the stream.
            childrenfileread.close();
        } catch (FileNotFoundException e) {
            // We've already verified the existence of the file.
            // Therefore, this error should never occur in real-world situations.
            // Crash the program with a RuntimeException.
            throw new RuntimeException("An unknown error occurred while attempting to read the children file.", e);
        }
        // Now, for each child, do the same for records: diaperchange, feeding, growth, medication, sleep.
        for (int i = 0; i < this.children.size(); i++) {
            // Record files (shorthand names, described above).
            File drfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/dr.dat");
            File frfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/fr.dat");
            File grfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/gr.dat");
            File mrfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/mr.dat");
            File srfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/sr.dat");
            // Try to create all these files if they don't exist, same as before, crashing if it fails.
            try {
                // They all have the same parent directory, but it still needs to exist.
                drfile.getParentFile().mkdirs();
                // Now create the files themselves.
                drfile.createNewFile();
                frfile.createNewFile();
                grfile.createNewFile();
                mrfile.createNewFile();
                srfile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Unable to initialise the record files for child " + this.children.get(i).getName() + ".", e);
            }
            // Now fetch each record to add to the child object.
            try {
                // Diaper change records.
                Scanner drfileread = new Scanner(drfile);
                while (drfileread.hasNextLine()) {
                    String[] line = drfileread.nextLine().split(",");
                    // Bad entry if not exactly 2 fields.
                    if (line.length != 2) {
                        continue;
                    }
                    // Needs to convert to a localdatetime object, and this can again fail if input is bad.
                    LocalDateTime time;
                    try {
                        time = LocalDateTime.parse(line[0]);
                    } catch (DateTimeParseException e) {
                        // Invalid time format that could not be parsed; skip past this.
                        continue;
                    }
                    // Type is a standard String.
                    String type = line[1];
                    // Add the record.
                    this.children.get(i).addDiaperChangeRecord(new DiaperChangeRecord(time, type));
                }
                // End of diaper change records.
                drfileread.close();
                // Feeding records.
                Scanner frfileread = new Scanner(frfile);
                while (frfileread.hasNextLine()) {
                    String[] line = frfileread.nextLine().split(",");
                    // Bad entry if not exactly 3 fields.
                    if (line.length != 3) {
                        continue;
                    }
                    // Need to convert to both a LocalDateTime object and a double.
                    LocalDateTime time;
                    double amount;
                    try {
                        time = LocalDateTime.parse(line[0]);
                        amount = Double.parseDouble(line[2]);
                    } catch (DateTimeParseException e) {
                        // Time could not be parsed.
                        continue;
                    } catch (NumberFormatException e) {
                        // Amount could not be parsed.
                        continue;
                    }
                    // Type is a standard string.
                    String type = line[1];
                    // Add the record.
                    this.children.get(i).addFeedingRecord(new FeedingRecord(time, type, amount));
                }
                // End of feeding records.
                frfileread.close();
                // Growth records.
                Scanner grfileread = new Scanner(grfile);
                while (grfileread.hasNextLine()) {
                    String[] line = grfileread.nextLine().split(",");
                    // Bad entry if not exactly 3 fields.
                    if (line.length != 3) {
                        continue;
                    }
                    // Need to convert to LocalDateTime and two doubles.
                    LocalDateTime time;
                    double weight;
                    double height;
                    try {
                        time = LocalDateTime.parse(line[0]);
                        weight = Double.parseDouble(line[1]);
                        height = Double.parseDouble(line[2]);
                    } catch (DateTimeParseException e) {
                        // Time could not be parsed.
                        continue;
                    } catch (NumberFormatException e) {
                        // Either of the doubles could not be parsed.
                        continue;
                    }
                    // Add the record.
                    this.children.get(i).addGrowthData(new GrowthData(time, weight, height));
                }
                // End of growth records.
                grfileread.close();
                // Medication records.
                Scanner mrfileread = new Scanner(mrfile);
                while (mrfileread.hasNextLine()) {
                    String[] line = mrfileread.nextLine().split(",");
                    // Bad entry if not exactly 3 fields.
                    if (line.length != 3) {
                        continue;
                    }
                    // Need to convert to LocalDateTime and one double.
                    LocalDateTime time;
                    double dosage;
                    try {
                        time = LocalDateTime.parse(line[0]);
                        dosage = Double.parseDouble(line[2]);
                    } catch (DateTimeParseException e) {
                        // Time could not be parsed.
                        continue;
                    } catch (NumberFormatException e) {
                        // Either of the doubles could not be parsed.
                        continue;
                    }
                    // Name is a normal string.
                    String name = line[1];
                    // Add the record.
                    this.children.get(i).addMedicationRecord(new MedicationRecord(time, name, dosage));
                }
                // End of medication records.
                mrfileread.close();
                // Sleeping records.
                Scanner srfileread = new Scanner(srfile);
                while (srfileread.hasNextLine()) {
                    String[] line = srfileread.nextLine().split(",");
                    // Bad entry if not exactly 3 fields.
                    if (line.length != 3) {
                        continue;
                    }
                    // Both are LocalDateTime objects.
                    LocalDateTime record;
                    LocalDateTime start;
                    LocalDateTime end;
                    try {
                        record = LocalDateTime.parse(line[0]);
                        start = LocalDateTime.parse(line[1]);
                        end = LocalDateTime.parse(line[2]);
                    } catch (DateTimeParseException e) {
                        // Time could not be parsed.
                        continue;
                    }
                    // Add the record.
                    this.children.get(i).addSleepRecord(new SleepSession(record, start, end));
                }
                // End of sleeping records.
                srfileread.close();
            } catch (FileNotFoundException e) {
                // Again, we've already verified the existence of all the files.
                // Crash with a generic runtime exception.
                throw new RuntimeException("An unknown error occurred while attempting to read the record files for child " + this.children.get(i).getName() + ".", e);
            }
        }
    }

    // Method to check if this database is empty. May be unneeded, but doesn't hurt to have it anyway.
    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    // Method to add a new child under this account.
    public void addChild(String name) {
        // NOTE: The code that calls this method is responsible for ensuring duplicates are avoided.
        // We are going to throw an exception if so, so remember to preemptively ensure we can't!
        if (this.getChild(name) == null) {
            this.children.add(new Child(name));
        } else {
            throw new RuntimeException("Child " + name + " already exists in the database. Ensure each child has a unique name.");
        }
        // Sync changes to the disk.
        this.sync();
    }

    // Method to get a child object from its name.
    public Child getChild(String name) {
        // Loop through every child to find the one we are looking for.
        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).getName().equals(name)) {
                return this.children.get(i);
            }
        }
        // Nothing was found, return empty object.
        return null;
    }

    // Method to delete a child object from the database.
    public void deleteChild(String name) {
        // Loop through every child to find the one we should delete.
        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).getName().equals(name)) {
                this.children.remove(i);
            }
        }
        // Sync changes to the disk.
        this.sync();
        // Delete leftover child's data from the disk.
        new File(AppStorage.getDirectory() + this.owner + "/" + name + "/dr.dat").delete();
        new File(AppStorage.getDirectory() + this.owner + "/" + name + "/fr.dat").delete();
        new File(AppStorage.getDirectory() + this.owner + "/" + name + "/gr.dat").delete();
        new File(AppStorage.getDirectory() + this.owner + "/" + name + "/mr.dat").delete();
        new File(AppStorage.getDirectory() + this.owner + "/" + name + "/sr.dat").delete();
        new File(AppStorage.getDirectory() + this.owner + "/" + name).delete();
    }

    // Method to get the full list of children from the database.
    public ArrayList<Child> getAllChildren() {
        return this.children;
    }

    // Method to sync any changes to the disk, e.g., when a record is added to a child.
    // Will be called automatically for addChild and deleteChild, but not for records!
    // This should be manually called as often as is necessary to prevent data loss.
    public void sync() {
        // First, update the list of children.
        try {
            // Open the file in append = false mode, because we are re-writing all the data.
            FileWriter childrenfilewrite = new FileWriter(this.childrenfile, false);
            // Loop through every child in the database.
            for (int i = 0; i < this.children.size(); i++) {
                // Write the child's name to the file, remembering the newline at the end of each line.
                childrenfilewrite.write(this.children.get(i).getName() + "\n");
            }
            // Close the file.
            childrenfilewrite.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to write data to the children file.", e);
        }
        // Now go through each child in the database and sync their records.
        for (int i = 0; i < this.children.size(); i++) {
            // Files for each record type - diaper, feeding, growth, medication, sleeping
            File drfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/dr.dat");
            File frfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/fr.dat");
            File grfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/gr.dat");
            File mrfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/mr.dat");
            File srfile = new File(AppStorage.getDirectory() + this.owner + "/" + this.children.get(i).getName() + "/sr.dat");
            // Try to create all these files if they don't exist, same as before, crashing if it fails.
            try {
                // They all have the same parent directory, but it still needs to exist.
                drfile.getParentFile().mkdirs();
                // Now create the files themselves.
                drfile.createNewFile();
                frfile.createNewFile();
                grfile.createNewFile();
                mrfile.createNewFile();
                srfile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Unable to initialise the record files for child " + this.children.get(i).getName() + ".", e);
            }
            // Now try to update each file.
            try {
                // Diaper change records (1/5).
                FileWriter drfilewrite = new FileWriter(drfile, false);
                // Go through every diaper change record.
                for (int j = 0; j < this.children.get(i).getDiaperChangeRecords().size(); j++) {
                    // Write the diaper change record in the way we expect to read it back.
                    drfilewrite.write(this.children.get(i).getDiaperChangeRecords().get(j).getTime() + "," + this.children.get(i).getDiaperChangeRecords().get(j).getType() + "\n");
                }
                // End of diaper change records.
                drfilewrite.close();
                // Feeding records (2/5).
                FileWriter frfilewrite = new FileWriter(frfile, false);
                // Go through every feeding record.
                for (int j = 0; j < this.children.get(i).getFeedingRecords().size(); j++) {
                    // Write the feeling record in the way we expect to read it back.
                    frfilewrite.write(this.children.get(i).getFeedingRecords().get(j).getTime() + "," + this.children.get(i).getFeedingRecords().get(j).getType() + "," + this.children.get(i).getFeedingRecords().get(j).getAmount() + "\n");
                }
                // End of feeding records.
                frfilewrite.close();
                // Growth records (3/5).
                FileWriter grfilewrite = new FileWriter(grfile, false);
                // Go through every growth record.
                for (int j = 0; j < this.children.get(i).getGrowthRecords().size(); j++) {
                    // Write the growth record in the way we expect to read it back.
                    grfilewrite.write(this.children.get(i).getGrowthRecords().get(j).getTime() + "," + this.children.get(i).getGrowthRecords().get(j).getWeight() + "," + this.children.get(i).getGrowthRecords().get(j).getHeight() + "\n");
                }
                // End of growth records.
                grfilewrite.close();
                // Medication records (4/5).
                FileWriter mrfilewrite = new FileWriter(mrfile, false);
                // Go through every medication record.
                for (int j = 0; j < this.children.get(i).getMedicationRecords().size(); j++) {
                    // Write the medication record in the way we expect to read it back.
                    mrfilewrite.write(this.children.get(i).getMedicationRecords().get(j).getTime() + "," + this.children.get(i).getMedicationRecords().get(j).getName() + "," + this.children.get(i).getMedicationRecords().get(j).getDosage() + "\n");
                }
                // End of medication records.
                mrfilewrite.close();
                // Sleeping records.
                FileWriter srfilewrite = new FileWriter(srfile, false);
                // Go through every sleeping record.
                for (int j = 0; j < this.children.get(i).getSleepRecords().size(); j++) {
                    // Write the sleeping record in the way we expect to read it back.
                    srfilewrite.write(this.children.get(i).getSleepRecords().get(j).getRecordTime() + "," + this.children.get(i).getSleepRecords().get(j).getStartTime() + "," + this.children.get(i).getSleepRecords().get(j).getEndTime() + "\n");
                }
                // End of sleeping records.
                srfilewrite.close();
            } catch (IOException e) {
                throw new RuntimeException("Unable to write data to the record files for child " + this.children.get(i).getName() + ".", e);
            }
        }
    }
}
