package spring;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.storage.StorageService;

public class ThumbnailUploadController {
    private final StorageService thumbnailStorage;
    
    public ThumbnailUploadController(@Qualifier("ImageStorage") StorageService thumbnailStorage) {
        this.thumbnailStorage = thumbnailStorage;
    }

    /**
     * Lists all thumbnails on the server.
     *
     * @param model
     * @return
     *
     * @throws IOException
     */
    @GetMapping("/list-thumbnails")
    @ResponseBody
    public List<String> listUploadedFiles(Model model) throws IOException {
    // Return json/xml/whatever list.
    return thumbnailStorage.loadAll().map(
        path -> MvcUriComponentsBuilder.fromMethodName(ThumbnailUploadController.class,
        "serveFile", path.getFileName().toString()).build().toString())
        .collect(Collectors.toList());
    }

    
    /**
     * Fetches a thumbnail from the server.
     *
     * @param filename
     * @return
     */
    @GetMapping("/get-thumbnail/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
    Resource file = thumbnailStorage.loadAsResource(filename);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + file.getFilename()
    );
    responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/thumbnail"); // double check this line
    return ResponseEntity.ok().headers(responseHeaders).body(file);
    }

    /**
     * Uploads a thumbnail to the server.
     *
     * @param file
     * @param redirectAttributes
     */
    @PostMapping("/upload-thumbnail")
    @ResponseStatus(value = HttpStatus.OK)
    public void storeThumbnailOnServer(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        thumbnailStorage.store(file);
    }
}
