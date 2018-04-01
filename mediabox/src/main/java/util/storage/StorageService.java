package util.storage;

import java.io.File;

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

    void setRoot(String s);

    /**
     * Gets the file with the given id. Creates a new file if it doesn't exist.
     * @param filename
     * @return
     */
    File get(String filename);

    /**
     * Checks whether a file exists with the given filename.
     *
     * @param filename
     * @return
     */
    boolean has(String filename);

    /**
     * Gets the base directory.
     *
     * @return
     */
    File getBaseDir();
}
