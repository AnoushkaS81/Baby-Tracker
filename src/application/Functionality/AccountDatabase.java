package application.Functionality;

// Import modules for file management, reading and writing.
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// This class is for the accounts database and handles everything account-related!
// The list of accounts will be an ArrayList of "Account" objects.
// This class is also responsible for managing the accounts data file.
public class AccountDatabase {
    // The ArrayList containing our accounts (each are Account objects).
    private ArrayList<Account> accounts;
    // The accounts file.
    private File accountsfile;
    // The extra fields (namely - email and phone) file.
    private File extrafieldsfile;
    // Constructor - primarily loads account data from the accounts file.
    public AccountDatabase() {
        // Set up the ArrayList of accounts.
        this.accounts = new ArrayList<Account>();
        // Initialise the accounts file.
        this.accountsfile = new File(AppStorage.getDirectory() + "accounts.dat");
        // Initialise the extras file.
        this.extrafieldsfile = new File(AppStorage.getDirectory() + "extrafields.dat");
        // Create both files if they don't yet exist.
        // We need to use try/catch because it can throw an IOException error.
        try {
            // Again this returns true or false, but we are ignoring it.
            this.accountsfile.createNewFile();
            this.extrafieldsfile.createNewFile();
        } catch (IOException e) {
            // Crash the program with a RuntimeException.
            throw new RuntimeException("Unable to initialise the accounts and extra fields files.", e);
        }
        // Now scan through the file and check if it has any valid user accounts.
        // Using try/catch yet again, for the same reason as before.
        try {
            // Set up a scanner to read through the lines of the file.
            Scanner accountsfileread = new Scanner(this.accountsfile);
            // Detect whether an account has been found.
            // Loop through while there are still lines in the file.
            // If the file is empty, this loop will be skipped entirely.
            while (accountsfileread.hasNextLine()) {
                // Read the line of the file into a string array.
                String[] line = accountsfileread.nextLine().split(",");
                // Ensure there are exactly 3 values in the line.
                if (line.length != 3) {
                    // This is an invalid entry, skip over it.
                    continue;
                }
                // Create a new account in the database using the read values.
                this.accounts.add(new Account(line[0], line[1], line[2]));
            }
            // Close the stream.
            accountsfileread.close();
            // Now do the same thing, for extras file.
            Scanner extrafieldsfileread = new Scanner(this.extrafieldsfile);
            while (extrafieldsfileread.hasNextLine()) {
                // We pass -1 as an additional argument to split, so it preserves empty fields.
                // This is because these fields are optional, and therefore may be empty.
                // No other fields elsewhere use this, so it's only needed here.
                String[] line = extrafieldsfileread.nextLine().split(",", -1);
                // Again, there are exactly 3 values - username, email, phone.
                if (line.length != 3) {
                    continue;
                }
                // Get the account object and set the extra information for it.
                Account account = this.getAccount(line[0]);
                account.setEmail(line[1]);
                account.setPhone(line[2]);
            }
            // Close the stream.
            extrafieldsfileread.close();
        } catch (FileNotFoundException e) {
            // We've already verified the existence of the files.
            // Therefore, this error should never occur in real-world situations.
            // Crash the program with a RuntimeException.
            throw new RuntimeException("An unknown error occurred while attempting to read the accounts and extra fields files.", e);
        }
        // Finally, run a sync, for redundancy.
        this.sync();
    }
    // Method to create a new account. Called by registration screen.
    // Note that it does not handle validation! Reg screen needs to do that.
    public void createAccount(String username, String fullname, String password) {
        // Add the new account object to the accounts list, ensuring the password is hashed.
        this.accounts.add(new Account(username, fullname, Hashing.hash(password)));
        // Sync changes to the disk.
        this.sync();
    }
    // Method to check whether at least one account exists.
    // i.e., used to check whether the program is being run for the first time.
    public boolean isEmpty() {
        return this.accounts.isEmpty();
    }
    // Verify account credentials match an account in the database.
    public boolean verifyAccount(String username, String password) {
        // Check inserted credentials against
        for (int i = 0; i < this.accounts.size(); i++) {
            // Check if username matches.
            if (this.accounts.get(i).getUsername().equals(username)) {
                // Username matches, check if password matches.
                if (this.accounts.get(i).getPasswordhash().equals(Hashing.hash(password))) {
                    // Password also matches, valid login.
                    return true;
                } else {
                    // Password does not match, invalid login.
                    return false;
                }
            };
        }
        // No matches; invalid login.
        return false;
    }
    // Get the account object associated with the username.
    // Can also be used to verify whether an account exists or not.
    public Account getAccount(String username) {
        // Find the account object.
        for (int i = 0; i < this.accounts.size(); i++) {
            // Check if the username matches.
            if (this.accounts.get(i).getUsername().equals(username)) {
                // Return the account.
                return this.accounts.get(i);
            }
        }
        // Couldn't find the account, return nothing.
        return null;
    }
    public void updateAccount(String username, String newFullname) { // Update the account in the database
        // Find the account and update it in memory
        Account account = getAccount(username);
        if (account != null) {
            account.setFullname(newFullname);
            // Sync changes to disk.
            this.sync();
        }
    }
    public void updatePassword(String username, String newPassword) {
        Account account = getAccount(username);
        if (account != null) {
            // Set the new password by hashing it using our hasing method.
            account.setPasswordhash(Hashing.hash(newPassword));
            // Sync changes to disk.
            this.sync();
        }
    }
    public void updateEmail(String username, String newEmail) {
        Account account = getAccount(username);
        if (account != null) {
            // Set the new field.
            account.setEmail(newEmail);
            // Sync changes to the disk.
            this.sync();
        }
    }
    public void updatePhone(String username, String newPhone) {
        Account account = getAccount(username);
        if (account != null) {
            // Set the new field.
            account.setPhone(newPhone);
            // Sync changes to the disk.
            this.sync();
        }
    }
    public void deleteAccount(String username) {
        Account account = getAccount(username);
        if (account != null) {
            // Remove the account from our list.
            this.accounts.remove(account);
            // Sync changes to disk (this will auto-remove the account's entries).
            this.sync();
            // TODO: We may need to recursively delete the directory containing child data.
            // TODO: Otherwise, it may spill over into a new account created with the same username.
        }
    }
    // Method to sync changes to the disk. Introduced to avoid repetition of code.
    // This should be run after any update to an account.
    public void sync() {
        // Update accounts (1/2).
        try {
            // Open the file in append = false mode, because we are re-writing all the data.
            FileWriter accountsfilewrite = new FileWriter(this.accountsfile, false);
            // Loop through every account in the database.
            for (int i = 0; i < this.accounts.size(); i++) {
                // Write the account data to the file, keeping in mind our CSV format, and newline.
                accountsfilewrite.write(this.accounts.get(i).getUsername() + "," + this.accounts.get(i).getFullname() + "," + this.accounts.get(i).getPasswordhash() + "\n");
            }
            // Close the file.
            accountsfilewrite.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to write data to the accounts file.", e);
        }
        // Update extra fields (2/2).
        try {
            // Again, open in append = false mode, to ensure we re-write all the data.
            FileWriter extrafieldsfilewrite = new FileWriter(this.extrafieldsfile, false);
            // Loop through every account in the database.
            for (int i = 0; i < this.accounts.size(); i++) {
                // Write the extras data to the file, in our CSV format, and again remembering newline.
                extrafieldsfilewrite.write(this.accounts.get(i).getUsername() + "," + this.accounts.get(i).getEmail() + "," + this.accounts.get(i).getPhone() + "\n");
            }
            // Close the file.
            extrafieldsfilewrite.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to write data to the extra fields file.", e);
        }
    }
}
