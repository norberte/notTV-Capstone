package filesharingsystem;

import java.io.File;

public class TrivialUploadProcess implements UploadProcess {
    @Override
    public void upload(File parent, File... files) {
    }
    
    @Override
    public void upload(File f) {
    }

    @Override
    public void stop() {
    }
}
