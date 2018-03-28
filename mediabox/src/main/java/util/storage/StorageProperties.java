package util.storage;

import java.nio.file.Paths;

public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location;

    public StorageProperties(String dirName) {
        this.location = Paths.get(System.getProperty("user.home"), dirName).toAbsolutePath().toString();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
