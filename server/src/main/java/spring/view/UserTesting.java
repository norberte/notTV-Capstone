package spring.view;

public class UserTesting {
    public String hashedPassword;
    public String email;
    
    public UserTesting(String hashedPassword, String email) {
        super();
        this.hashedPassword = hashedPassword;
        this.email = email;
    }
    
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
