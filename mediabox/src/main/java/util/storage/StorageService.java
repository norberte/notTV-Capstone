package util.storage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    default File get(String id) {
        return get(Paths.get(id));
    }

    default File get(String first, String... more) {
        return get(Paths.get(first, more));
    }
    
    /**
     * Gets the file with the given id (as a path)
     * with specified parents.
     * @param parents
     * @param id
     * @return
     */
    File get(Path pathId);

    /**
     * Checks whether a file exists with the given filename.
     *
     * @param filename
     * @return
     */
    boolean has(String filename);
}
