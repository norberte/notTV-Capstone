package util.storage;

import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = Paths.get(System.getProperty("user.home"), "torrents").toAbsolutePath().toString();

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

}
