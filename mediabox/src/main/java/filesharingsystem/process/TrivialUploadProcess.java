package filesharingsystem.process;

import java.io.File;

public class TrivialUploadProcess implements UploadProcess {
    @Override
    public void upload(String name, File parent, File... files) {
    }
    
    @Override
    public void upload(String name,File f) {
    }

    @Override
    public void stop() {
    }
}
