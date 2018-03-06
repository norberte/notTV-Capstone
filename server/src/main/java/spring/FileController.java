package spring;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.storage.StorageFileNotFoundException;
import spring.storage.StorageService;

@CrossOrigin
@RestController
public class FileController {
    private final HashMap<String, StorageService> storage;

    public FileController(@Qualifier("TorrentStorage") StorageService torrentStorage, @Qualifier("ImageStorage") StorageService thumbnailStorage) {
        storage = new HashMap<>();
    	storage.put("torrent", torrentStorage);
        storage.put("thumbnail", thumbnailStorage);
    }

    /**
     * Lists all files of the given type (e.g. torrent) on the server.
     *
     * @param model
     * @return
     *
     * @throws IOException
     */
    @GetMapping("/list/{type}")
    @ResponseBody
    public ResponseEntity<List<String>> listUploadedFiles(Model model, @PathVariable String type) throws IOException {
	// Return json/xml/whatever list.
        if(!storage.containsKey(type))
            return ResponseEntity.badRequest().body(Collections.emptyList());

        return ResponseEntity.ok().body(
            storage.get(type).loadAll().map( // map the file names to a /get/filename url
                path -> MvcUriComponentsBuilder.fromMethodName(
                    FileController.class,
                    "serveFile",
                    type,
                    path.getFileName().toString()
                ).build().toString()
            ).collect(Collectors.toList())
        );
    }
    
    /**
     * Fetches a file from the server.
     *
     * @param filename
     * @param type - type of File (e.g., torrent)
     * @return
     */
    @GetMapping("/get/{type}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String type, @PathVariable String filename) {
        if(!storage.containsKey(type))
            return ResponseEntity.badRequest().body(null);

        try {
            Resource file = storage.get(type).loadAsResource(filename);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + file.getFilename()
            );
            responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/file");
            return ResponseEntity.ok().headers(responseHeaders).body(file);
        } catch(StorageFileNotFoundException e) {
            // if no file, return not found.
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Uploads a file to the server.
     *
     * @param file
     * @param redirectAttributes
     */
    @PostMapping("/upload/{type}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Void> handleFileUpload(@PathVariable String type, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if(!storage.containsKey(type))
            return ResponseEntity.badRequest().body(null);
        storage.get(type).store(file);
        return ResponseEntity.ok().body(null);
    }
}
