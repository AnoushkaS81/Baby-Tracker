package application.Functionality;

// This class will contain methods for handling the app's data storage.
// It only contains static methods, so objects must not be created.
public class AppStorage {
    // Make the constructor private and empty so objects cannot be created.
    private AppStorage() {}
    // This method returns the data directory according to the OS.
    public static String getDirectory() {
        // Determine the OS name and convert to lower-case, for checking.
        String os = System.getProperty("os.name").toLowerCase();
        // Determine the home directory - data directory is based off this.
        String homedir = System.getProperty("user.home");
        // Set the data directory according to the OS we are running on.
        if (os.contains("win")) {
            // Data directory for Windows.
            return homedir + "/AppData/Roaming/ce201application/";
        } else if (os.contains("mac")) {
            // Directory data for macOS.
            return homedir + "/Library/Application Support/ce201application/";
        } else {
            // Data directory for Linux, BSD, and other Unix.
            return homedir + "/.local/share/ce201application/";
        }
    }
}
