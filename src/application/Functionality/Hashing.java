package application.Functionality;

// Import modules for password hashing and base64 encoding.
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

// This class will contain methods for hashing passwords.
// Like AppStorage, it only contains static methods, so no objects!
public class Hashing {
    // Private and empty constructor because we don't want objects to be created.
    private Hashing() {}
    // Static method to hash password using SHA512 and encode with base64.
    public static String hash(String password) {
        // Output string to return, declare it now for later.
        String pwhash;
        // Hash password using SHA512, which is one of the best algorithms.
        // We MUST use a try/catch block because md can throw an error.
        try {
            // Load the SHA512 algorithm.
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            // Calculate the hash using md, which requires string to be converted to byte format.
            // Then we are using base64 encoding because it's simpler than proper hex representation.
            // Then finally we have to convert to string because it's an array by default.
            // Essentially: convert to byte array > get hash > encode in base64 > back to string.
            pwhash = new String(Base64.getEncoder().encode(md.digest(password.getBytes())));
        } catch (NoSuchAlgorithmException error) {
            // Return empty string because we couldn't work out the hash.
            return null;
        }
        // Hash has been calculated, return it.
        return pwhash;
    }
}
