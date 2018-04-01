package springbackend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

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

    private static ConfigurableApplicationContext context;
    // ARGS:
    // 0 - Output folder.
    // 1 - id
    // 2 - bandwidth.
    // 3 - upload/download
    // 4 - collect stats (optional, missing = true)
    public static void main(String[] args) {
        context = SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        config.outDir = Paths.get(
            System.getProperty("user.home"),
            "simulation_data",
            args[0]
        ).toFile();
        config.bandwidth = Integer.parseInt(args[2]);
        torrentStorage.setRoot("torrent"+args[1]);
        videoStorage.setRoot("video"+args[1]);
        
        String videoId = "sim";
        boolean collectStats = args.length > 4 ?
            Boolean.parseBoolean(args[4]) : true;

        if(args[3].equalsIgnoreCase("upload")) {
            // Start seeding process.
            seedManager.addProcess(videoId, videoStorage.get(videoId+".mp4"));
            collectStats(collectStats, Optional.empty());
        } else {
            // get torrent file.
            String torrent = String.format("%s.torrent", videoId);
            File torrentFile = torrentStorage.get(torrent);
            try {
                Request.Get(
                    String.format("%s/get/torrent/%s", this.config.getServerUrl(), torrent)
                ).execute().saveContent(torrentFile);

                // download file.
                DownloadProcess dp = beanFactory.getBean(DownloadProcess.class, torrentFile);
                collectStats(collectStats, Optional.of(dp));
                dp.download();
            } catch (IOException e) {
                log.error("Error getting torrent file from server.", e);
            }
        }
    }

    private void collectStats(boolean collectStats, Optional<DownloadProcess> dp)
        throws FileNotFoundException, InterruptedException {
        if(collectStats)
            new Thread(new StatTask(config.outDir, dp)).start();
        else {
            // wait for it to finish
            while(!(dp.isPresent() && dp.get().isFinished())) {
                Thread.sleep(100);
            }
            Application.exit();
        }
    }

    public static void exit() {
        int exitCode = SpringApplication.exit(context, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                // no errors
                return 0;
            }
        });
        System.exit(exitCode);
    }
}
