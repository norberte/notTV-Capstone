package filesharingsystem.process;

import java.io.File;
import java.net.URISyntaxException;


public interface UploadProcessFactory {
    UploadProcess getProcess(String name, File file) throws URISyntaxException;
}
