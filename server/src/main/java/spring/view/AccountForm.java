package spring.view;

public class AccountForm {
    String currentUsername;
    String newEmail;
    String autoDownload;
    String newPass;
    String confirmNewPass;
    
    
    public String getCurrentUsername() {
        return currentUsername;
    }
    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }
    
    public String getNewEmail() {
        return newEmail;
    }
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
    public String getAutoDownload() {
        return autoDownload;
    }
    public void setAutoDownload(String autoDownload) {
        this.autoDownload = autoDownload;
    }
    public String getNewPass() {
        return newPass;
    }
    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }
    public String getConfirmNewPass() {
        return confirmNewPass;
    }
    public void setConfirmNewPass(String confirmNewPass) {
        this.confirmNewPass = confirmNewPass;
    }
}
