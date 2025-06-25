package application.Functionality;

import javax.swing.*;

public class LoginAction {
    // Private constructor - objects of this class should not be instantiated.
    private LoginAction() {};
    // Static method that handles the login logic.
    // Extracted into its own class because apparently its simpler this way.
    public static int doLogin(boolean register, String username, String fullname, String password, String passwordrepeat, Account account, boolean verify) {
        if (register) {
            // Verify all the fields contain data.
            if (username.isEmpty() || fullname.isEmpty() || password.isEmpty() || passwordrepeat.isEmpty()) {
                return 2;
            }
            // Verify that the passwords match.
            if (!password.equals(passwordrepeat)) {
                return 3;
            }
            // Ensure the account does not already exist.
            if (account != null) {
                return 4;
            }
            // We've passed the checks - return code 1 to create the new account.
            return 1;
        } else {
            // Ensure the fields are not empty.
            if (username.isEmpty() || password.isEmpty()) {
                return 5;
            }
            // Verify the inputted credentials are correct.
            if (!verify) {
                // Show an appropriate error message for invalid credentials.
                return 6;
            }
            // We've passed the checks - return code 0 to login to the account.
            return 0;
        }
    }
}