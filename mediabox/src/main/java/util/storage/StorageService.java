package util.storage;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for a file system.
 * Note: Some implementations could have files over multiple partitions.
 *
 * @author
 */
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
}
