package application;

import application.Functionality.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.toedter.calendar.JDateChooser;

public class ChildProfile extends JPanel {
    private ChildDatabase childDatabase;
    private String childName;
    private DefaultTableModel feedingModel, sleepModel, medicationModel, diaperChangeModel, growthModel, reportModel;

    public ChildProfile(ChildDatabase childDatabase, String childName, JPanel cardPanel, CardLayout cardLayout) {
        // We need to sync data to the disk, so hopefully inherit the DB from the previous screen.
        this.childDatabase = childDatabase;
        // Need the name of the child for displaying, and using in the DB.
        this.childName = childName;

        setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel("Child Profile: " + childDatabase.getChild(childName).getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(nameLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        feedingModel = new DefaultTableModel(new Object[]{"S.No", "Item Type", "Date and Time", "Feeding Type", "Amount (ml)"}, 0);
        sleepModel = new DefaultTableModel(new Object[]{"S.No", "Item Type", "Date and Time", "Start Time", "End Time"}, 0);
        medicationModel = new DefaultTableModel(new Object[]{"S.No", "Item Type", "Date and Time", "Medication Name", "Dosage (mg)"}, 0);
        diaperChangeModel = new DefaultTableModel(new Object[]{"S.No", "Item Type", "Time of Change", "Nappy Type"}, 0);
        growthModel = new DefaultTableModel(new Object[]{"S.No", "Item Type", "Date and Time", "Height (cm)", "Weight (kg)"}, 0);
        reportModel = new DefaultTableModel(new Object[]{"S.No", "Item Type", "Report Type", "Filename"}, 0);

        tabbedPane.addTab("Feeding", createTablePanel(feedingModel));
        tabbedPane.addTab("Sleep", createTablePanel(sleepModel));
        tabbedPane.addTab("Medication", createTablePanel(medicationModel));
        tabbedPane.addTab("Diaper Change", createTablePanel(diaperChangeModel));
        tabbedPane.addTab("Growth Record", createTablePanel(growthModel));
        tabbedPane.addTab("Reports", createTablePanel(reportModel)); // New Reports tab

        add(tabbedPane, BorderLayout.CENTER);

        // Preload existing records from the disk and convert them to the table model format.
        for (FeedingRecord fr : this.childDatabase.getChild(childName).getFeedingRecords()) {
            feedingModel.addRow(new Object[]{
                    feedingModel.getRowCount() + 1,
                    "Feeding",
                    fr.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    fr.getType(),
                    fr.getAmount()
            });
        }
        for (SleepSession sr : this.childDatabase.getChild(childName).getSleepRecords()) {
            sleepModel.addRow(new Object[]{
                    sleepModel.getRowCount() + 1,
                    "Sleep",
                    sr.getRecordTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    sr.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    sr.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            });
        }
        for (MedicationRecord mr : this.childDatabase.getChild(childName).getMedicationRecords()) {
            medicationModel.addRow(new Object[]{
                    medicationModel.getRowCount() + 1,
                    "Medication",
                    mr.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    mr.getName(),
                    mr.getDosage()
            });
        }
        for (DiaperChangeRecord dr : this.childDatabase.getChild(childName).getDiaperChangeRecords()) {
            diaperChangeModel.addRow(new Object[]{
                    diaperChangeModel.getRowCount() + 1,
                    "Diaper Change",
                    dr.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    dr.getType()
            });
        }
        for (GrowthData gr : this.childDatabase.getChild(childName).getGrowthRecords()) {
            growthModel.addRow(new Object[]{
                    growthModel.getRowCount() + 1,
                    "Growth Record",
                    gr.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    gr.getHeight(),
                    gr.getWeight()
            });
        }

        // Now preload existing PDF reports from the disk.
        this.loadReports();

        JPanel buttonPanel = new JPanel(new CardLayout());

        JButton logFeedingButton = new JButton("Log Feeding");
        JButton logSleepButton = new JButton("Log Sleep");
        JButton logMedicationButton = new JButton("Log Medication");
        JButton logDiaperChangeButton = new JButton("Log Diaper Change");
        JButton logGrowthButton = new JButton("Log Growth");

        logFeedingButton.addActionListener(e -> logFeeding());
        logSleepButton.addActionListener(e -> logSleep());
        logMedicationButton.addActionListener(e -> logMedication());
        logDiaperChangeButton.addActionListener(e -> logDiaperChange());
        logGrowthButton.addActionListener(e -> logGrowth());

        buttonPanel.add(logFeedingButton, "Feeding");
        buttonPanel.add(logSleepButton, "Sleep");
        buttonPanel.add(logMedicationButton, "Medication");
        buttonPanel.add(logDiaperChangeButton, "Diaper Change");
        buttonPanel.add(logGrowthButton, "Growth Record");

        JButton backButton = new JButton("Back to Profile");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "UserProfile"));

        // Adding dropdown to select reports
        String[] reportOptions = {"Select Report", "Feeding", "Sleep", "Medication", "Diaper Change", "Growth Record"};
        JComboBox<String> reportDropdown = new JComboBox<>(reportOptions);

        // Add the button to generate the actual report.
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(e -> this.generateReport(String.valueOf(reportDropdown.getSelectedItem())));

        // Create a panel to center the dropdown and generate report button
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Center alignment
        reportPanel.add(reportDropdown);
        reportPanel.add(generateReportButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(backButton, BorderLayout.SOUTH);

        tabbedPane.addChangeListener(e -> {
            CardLayout cl = (CardLayout) buttonPanel.getLayout();
            int selectedIndex = tabbedPane.getSelectedIndex();

            if (selectedIndex == 5) { // Reports tab index
                bottomPanel.add(reportPanel, BorderLayout.CENTER);
            } else {
                bottomPanel.remove(reportPanel);
            }

            // Show corresponding log buttons for other tabs
            if (selectedIndex != 5) {
                bottomPanel.add(buttonPanel, BorderLayout.CENTER);
            } else {
                bottomPanel.remove(buttonPanel);
            }

            bottomPanel.revalidate();
            bottomPanel.repaint();
        });

        add(bottomPanel, BorderLayout.SOUTH);

        tabbedPane.addChangeListener(e -> {
            CardLayout cl = (CardLayout) buttonPanel.getLayout();
            cl.show(buttonPanel, tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
        });
    }

    private JPanel createTablePanel(DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout());

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 16));  // Increase font size for entire table
        JScrollPane scrollPane = new JScrollPane(table);

        // Adjust column width for the serial number column
        TableColumn serialColumn = table.getColumnModel().getColumn(0);
        serialColumn.setPreferredWidth(50);  // Adjust width for the serial number column

        // Resize other columns to fit content
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            // Determine the row which was selected.
            int row = table.getSelectedRow();
            // We can't delete anything if no row was selected for deletion.
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row from the table to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Determine the type, so we know how to delete the item on the backend.
            String type = table.getValueAt(row, 1).toString();
            // For all record types, the table row index should match the child object index.
            switch (type) {
                case "Feeding":
                    // Remove the feeding record.
                    this.childDatabase.getChild(childName).deleteFeedingRecord(row);
                    this.childDatabase.sync();
                    break;
                case "Sleep":
                    // Remove the sleep record.
                    this.childDatabase.getChild(childName).deleteSleepRecord(row);
                    this.childDatabase.sync();
                    break;
                case "Medication":
                    // Remove the medication record.
                    this.childDatabase.getChild(childName).deleteMedicationRecord(row);
                    this.childDatabase.sync();
                    break;
                case "Diaper Change":
                    // Remove the diaper change record.
                    this.childDatabase.getChild(childName).deleteDiaperChangeRecord(row);
                    this.childDatabase.sync();
                    break;
                case "Growth Record":
                    this.childDatabase.getChild(childName).deleteGrowthRecord(row);
                    this.childDatabase.sync();
                    break;
                case "Report":
                    // Specify the filename to delete, which will be the fourth column (index 3).
                    ReportGenerator.delete(table.getValueAt(row, 3).toString());
                    // Sync displayed reports.
                    this.loadReports();
                    break;
                default:
                    // Display a message when trying to delete unknown thing.
                    JOptionPane.showMessageDialog(this, "Failed to delete item of unknown type. This is likely a bug in the application.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
            // Reports will be auto-removed. Records will not, so we need to manually do that.
            if (!type.equals("Report")) {
                model.removeRow(row);
            }
            // Update table serial numbers.
            this.updateSerialNumbers(model);
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(deleteButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateSerialNumbers(DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0); // Update serial number
        }
    }

    private void logFeeding() {
        JPanel datePanel = new JPanel();

        // Create JDateChooser for date selection
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd HH:mm");
        datePanel.add(new JLabel("Select Date and Time:"));
        datePanel.add(dateChooser);

        // Prompt user for date
        int dateOption = JOptionPane.showConfirmDialog(this, datePanel, "Select Date and Time", JOptionPane.OK_CANCEL_OPTION);
        if (dateOption == JOptionPane.OK_OPTION) {
            if (dateChooser.getDate() != null) {
                // Extract the selected date and time from the date chooser
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateChooser.getDate());

                JTextField typeField = new JTextField(10);
                JTextField amountField = new JTextField(10);
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);

                panel.add(new JLabel("Feeding Type:"), gbc);
                gbc.gridx = 1;
                panel.add(typeField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(new JLabel("Amount (ml):"), gbc);
                gbc.gridx = 1;
                panel.add(amountField, gbc);

                int option = JOptionPane.showConfirmDialog(this, panel, "Log Feeding", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    // Validate feeding amount
                    try {
                        double amount = Double.parseDouble(amountField.getText());
                        if (amount <= 0) {
                            throw new NumberFormatException("Amount must be positive.");
                        }

                        feedingModel.addRow(new Object[]{
                                feedingModel.getRowCount() + 1,
                                "Feeding",
                                dateTime,  // Use the selected date and time
                                typeField.getText(),
                                amount
                        });
                        // Add it to the records in the child object.
                        this.childDatabase.getChild(childName).addFeedingRecord(new FeedingRecord(
                                LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                typeField.getText(),
                                amount
                        ));
                        // Do not forget to sync changes to the disk after adding new records!
                        this.childDatabase.sync();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid positive number for the amount.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid date and time.");
            }
        }
    }

    private void logSleep() {
        JPanel datePanel = new JPanel();

        // Create JDateChooser for date selection.
        // This makes use of the jcalendar library.
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd HH:mm");
        datePanel.add(new JLabel("Select Date and Time:"));
        datePanel.add(dateChooser);

        // Prompt user for date
        int dateOption = JOptionPane.showConfirmDialog(this, datePanel, "Select Date and Time", JOptionPane.OK_CANCEL_OPTION);
        if (dateOption == JOptionPane.OK_OPTION) {
            if (dateChooser.getDate() != null) {
                // Extract the selected date and time from the date chooser
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateChooser.getDate());

                // Create JDateChooser for Start and End time selection
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);

                // Start Time Picker
                JLabel startLabel = new JLabel("Start Time:");
                JDateChooser startDateChooser = new JDateChooser();
                startDateChooser.setDateFormatString("yyyy-MM-dd HH:mm");

                // End Time Picker
                JLabel endLabel = new JLabel("End Time:");
                JDateChooser endDateChooser = new JDateChooser();
                endDateChooser.setDateFormatString("yyyy-MM-dd HH:mm");

                gbc.gridx = 0;
                gbc.gridy = 0;
                panel.add(startLabel, gbc);
                gbc.gridx = 1;
                panel.add(startDateChooser, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(endLabel, gbc);
                gbc.gridx = 1;
                panel.add(endDateChooser, gbc);

                int option = JOptionPane.showConfirmDialog(this, panel, "Log Sleep", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    // Validate start and end time selection
                    try {
                        if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
                            throw new IllegalArgumentException("Start time and End time cannot be empty.");
                        }

                        LocalDateTime startTime = LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(startDateChooser.getDate()), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        LocalDateTime endTime = LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(endDateChooser.getDate()), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                        if (startTime.isAfter(endTime)) {
                            throw new IllegalArgumentException("Start time cannot be after End time.");
                        }

                        sleepModel.addRow(new Object[]{
                                sleepModel.getRowCount() + 1,
                                "Sleep",
                                dateTime,  // Use the selected date and time
                                startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        });
                        // Add it to the records in the child object.
                        this.childDatabase.getChild(childName).addSleepRecord(new SleepSession(
                                LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                startTime,
                                endTime
                        ));
                        // Do not forget to sync changes to the disk after adding new records!
                        this.childDatabase.sync();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid date and time.");
            }
        }
    }

    private void logMedication() {
        JPanel datePanel = new JPanel();

        // Create JDateChooser for date selection
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd HH:mm");
        datePanel.add(new JLabel("Select Date and Time:"));
        datePanel.add(dateChooser);

        // Prompt user for date
        int dateOption = JOptionPane.showConfirmDialog(this, datePanel, "Select Date and Time", JOptionPane.OK_CANCEL_OPTION);
        if (dateOption == JOptionPane.OK_OPTION) {
            if (dateChooser.getDate() != null) {
                // Extract the selected date and time from the date chooser
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateChooser.getDate());

                JTextField nameField = new JTextField(10);
                JTextField dosageField = new JTextField(10);
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);

                panel.add(new JLabel("Medication Name:"), gbc);
                gbc.gridx = 1;
                panel.add(nameField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(new JLabel("Dosage (mg):"), gbc);
                gbc.gridx = 1;
                panel.add(dosageField, gbc);

                int option = JOptionPane.showConfirmDialog(this, panel, "Log Medication", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    // Validate dosage
                    try {
                        double dosage = Double.parseDouble(dosageField.getText());
                        if (dosage <= 0) {
                            throw new NumberFormatException("Dosage must be a positive number.");
                        }

                        medicationModel.addRow(new Object[]{
                                medicationModel.getRowCount() + 1,
                                "Medication",
                                dateTime,  // Use the selected date and time
                                nameField.getText(),
                                dosage
                        });
                        // Add it to the records in the child object.
                        this.childDatabase.getChild(childName).addMedicationRecord(new MedicationRecord(
                                LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                nameField.getText(),
                                dosage
                        ));
                        // Do not forget to sync changes to the disk after adding new records!
                        this.childDatabase.sync();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid positive number for the dosage.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid date and time.");
            }
        }
    }
    private void logDiaperChange() {
        JPanel datePanel = new JPanel();

        // Create JDateChooser for date selection
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd HH:mm");
        datePanel.add(new JLabel("Select Date and Time of Change:"));
        datePanel.add(dateChooser);

        // Prompt user for date
        int dateOption = JOptionPane.showConfirmDialog(this, datePanel, "Select Date and Time", JOptionPane.OK_CANCEL_OPTION);
        if (dateOption == JOptionPane.OK_OPTION) {
            if (dateChooser.getDate() != null) {
                // Extract the selected date and time from the date chooser
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateChooser.getDate());

                // Create a combo box for Nappy Type selection
                String[] nappyTypes = {"Soiled nappy", "Wet nappy", "Mixed nappy"};
                JComboBox<String> nappyTypeComboBox = new JComboBox<>(nappyTypes);

                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);

                // Only ask for nappy type (no time of change)
                gbc.gridx = 0;
                panel.add(new JLabel("Nappy Type:"), gbc);
                gbc.gridx = 1;
                panel.add(nappyTypeComboBox, gbc);  // Add combo box for Nappy Type

                int option = JOptionPane.showConfirmDialog(this, panel, "Log Diaper Change", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    // Check if a valid Nappy Type is selected
                    if (nappyTypeComboBox.getSelectedIndex() == -1) {
                        JOptionPane.showMessageDialog(this, "Please select a valid Nappy Type.");
                        return;
                    }

                    // Add the entry to the diaper change table with the selected date and time
                    diaperChangeModel.addRow(new Object[]{
                            diaperChangeModel.getRowCount() + 1,
                            "Diaper Change",
                            dateTime,  // Use the selected date and time for the Date and Time of Change column
                            nappyTypeComboBox.getSelectedItem().toString()  // Get the selected Nappy Type
                    });
                    // Add it to the records in the child object.
                    this.childDatabase.getChild(childName).addDiaperChangeRecord(new DiaperChangeRecord(
                            LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            nappyTypeComboBox.getSelectedItem().toString()
                    ));
                    // Do not forget to sync changes to the disk after adding new records!
                    this.childDatabase.sync();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid date and time.");
            }
        }
    }
    private void logGrowth() {
        JPanel datePanel = new JPanel();

        // Create JDateChooser for date selection
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd HH:mm");
        datePanel.add(new JLabel("Select Date and Time:"));
        datePanel.add(dateChooser);

        // Prompt user for date
        int dateOption = JOptionPane.showConfirmDialog(this, datePanel, "Select Date and Time", JOptionPane.OK_CANCEL_OPTION);
        if (dateOption == JOptionPane.OK_OPTION) {
            if (dateChooser.getDate() != null) {
                // Extract the selected date and time from the date chooser
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateChooser.getDate());

                JTextField heightField = new JTextField(10);
                JTextField weightField = new JTextField(10);
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);

                panel.add(new JLabel("Height (cm):"), gbc);
                gbc.gridx = 1;
                panel.add(heightField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(new JLabel("Weight (kg):"), gbc);
                gbc.gridx = 1;
                panel.add(weightField, gbc);

                int option = JOptionPane.showConfirmDialog(this, panel, "Log Growth", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    // Validate height and weight
                    try {
                        double height = Double.parseDouble(heightField.getText());
                        double weight = Double.parseDouble(weightField.getText());

                        if (height <= 0 || weight <= 0) {
                            throw new NumberFormatException("Height and weight must be positive numbers.");
                        }

                        growthModel.addRow(new Object[]{
                                growthModel.getRowCount() + 1,
                                "Growth Record",
                                dateTime,
                                height,
                                weight
                        });
                        // Add it to the records in the child object.
                        this.childDatabase.getChild(childName).addGrowthData(new GrowthData(
                                LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                weight,
                                height
                        ));
                        // Do not forget to sync changes to the disk after adding new records!
                        this.childDatabase.sync();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter valid positive numbers for height and weight.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid date and time.");
            }
        }
    }

    // Method to generate a report.
    private void generateReport(String type) {
        // If the default/placeholder was specified, display an error message and abort.
        if (type.equals("Select Report")) {
            JOptionPane.showMessageDialog(this, "Please select the type of report to generate from the dropdown.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Generate a report with the given type. It returns a filename, so save that.
        String filename = ReportGenerator.generate(this.childName, type, this.childDatabase);
        // If the filename was null, then the generation failed. Deal with it.
        if (filename == null) {
            JOptionPane.showMessageDialog(this, "The report generation for '" + type + "' failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Refresh the table with newly saved report.
        this.loadReports();
        // Display a message indicating that it was successful.
        JOptionPane.showMessageDialog(this, "The report generation for '" + type + "' was successful (" + filename + ").", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to retrieve the reports saved on the disk and load them into the table.
    private void loadReports() {
        // Delete all existing rows, except the heading row.
        // We have to use a reverse order, due how rows are indexed.
        for (int i = reportModel.getRowCount() - 1; i >= 0; i -= 1) {
            reportModel.removeRow(i);
        }
        // Load all existing reports.
        for (String filename : ReportGenerator.findexisting()) {
            // The report type can be extracted from the filename, before any numbers appear.
            // Format: Name_Type_Date_Time.
            String[] filenameparts = filename.split("_");
            // Ignore any report with an invalid filename, or that isn't for this child.
            if (filenameparts.length != 4 || !filenameparts[0].equals(this.childName)) {
                continue;
            }
            // Add the table entry.
            reportModel.addRow(new Object[]{
                    reportModel.getRowCount() + 1,
                    "Report",
                    filenameparts[1],
                    filename
            });
        }
    }
}