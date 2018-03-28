package springbackend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import filesharingsystem.PortMapException;
import filesharingsystem.PortMapper;
import filesharingsystem.process.DownloadProcess;

import util.SeedManager;
import util.StatTask;
import util.storage.StorageService;

@SpringBootApplication
@ImportResource("classpath:app-config.xml")
public class Application implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    @Autowired
    PortMapper portMapper;
    @Autowired
    SeedManager seedManager;
    @Autowired
    Config config;
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    @Qualifier("TorrentStorage")
    private StorageService torrentStorage;
    @Autowired
    @Qualifier("VideoStorage")
    private StorageService videoStorage;
    @Autowired
    @Qualifier("ImageStorage")
    private StorageService thumbnailStorage;

    // ARGS:
    // 0 - Output folder.
    // 1 - bandwidth.
    // 2 - upload/download
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //config.id = Integer.parseInt(args[0]);
    }

    @Override
    public void run(String... args) throws Exception{
	portMap();
        config.outDir = Paths.get(
            System.getProperty("user.home"),
            "simulation_data",
            args[0]
        ).toFile();
        config.bandwidth = Integer.parseInt(args[1]);
        String videoId = "sim";
        
        if(args[2].equalsIgnoreCase("upload")) {
            // Start seeding process.
            seedManager.addProcess(videoId, videoStorage.get(videoId+".mp4"));
        } else {
            // get torrent file.
            String torrent = String.format("%s.torrent", videoId);
            File torrentFile = torrentStorage.get(torrent);
            try {
                Request.Get(
                    String.format("%s/get/torrent/%s", this.config.getServerUrl(), torrent)
                ).execute().saveContent(torrentFile);

                // download file.
                beanFactory.getBean(DownloadProcess.class, torrentFile).download();
                new Thread(new StatTask(config.outDir)).start();
            } catch (IOException e) {
                log.error("Error getting torrent file from server.", e);
            }
        }
    }

    public void portMap() {
	// configure port forwarding.
	try {
	    portMapper.setup();
	} catch (PortMapException e) {
	    log.warn("Unable to setup the port forwarding.", e);
	    // TODO: send notification to UI to inform user
	    // that they need to enable upnp.
	    // Bonus: check portforwarding somehow to allow manual port forwarding.
	}
    }
}
