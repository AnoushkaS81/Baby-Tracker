package application.Functionality;

// This class may be incomplete depending on what we need to add later.
// Feel free to adapt as necessary.
public class Account {
    // Account fields.
    private String username;
    private String passwordhash;
    private String fullname;
    // Additional/appendage fields.
    private String email;
    private String phone;
    // Constructor (used to register an account).
    public Account(String username, String fullname, String passwordhash) {
        // Set the account fields.
        // Note that password hashing is NOT managed by us.
        this.username = username;
        this.fullname = fullname;
        this.passwordhash = passwordhash;
        // Set up the additional/appendage fields. These are unset by default.
        this.email = "";
        this.phone = "";

    }
    // Basic getters.
    public String getUsername() {
        return this.username;
    }
    public String getFullname() {
        return this.fullname;
    }
    public String getPasswordhash() {
        return this.passwordhash;
    }
    // Additional getters.
    public String getEmail() {
        return this.email;
    }
    public String getPhone() {
        return this.phone;
    }
    // New setters, for editing details in settings.
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
