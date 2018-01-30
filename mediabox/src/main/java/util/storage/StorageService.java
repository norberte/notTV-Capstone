package util.storage;

import java.io.File;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Initializes the service.
     *
     */
    void init();

    /**
     * Stores the given File.
     *
     * @param file
     * @return
     */
    void store(MultipartFile file);

    /**
     * Gets the file with the given id.
     *
     * @param filename
     * @return
     */
    Path load(String filename);

    /**
     * Creates and returns a new File with the given name.
     *
     * @param name
     * @return
     */
    File newFile(String name);

    /**
     * Gets the base directory.
     *
     * @return
     */
    File getBaseDir();
}
