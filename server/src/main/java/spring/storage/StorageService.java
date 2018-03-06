package spring.storage;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Stores the given file.
     * 
     * @param file
     */
    void store(MultipartFile file);

    /**      
     * Stores the given file with the given name
     *                       
     * @param file           
     */    
    void store(String name, MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
