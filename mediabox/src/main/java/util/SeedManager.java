package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import filesharingsystem.process.UploadProcess;

import util.storage.StorageService;

/**
 * Manages the seeding threads, including the startup and shutdown. Doesn't use a threadpool, because the seeding process is long lived.
 * @author
 */
public class SeedManager {
    private static class Pair<T, S> implements Serializable {
        private static final long serialVersionUID = -3540758862913946498L;
        private T t;
        private S s;

        Pair(T t, S s) {
            this.t = t;
            this.s = s;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(SeedManager.class);
    private static final String MEMORY = "SeedManager-Memory";
    private final StorageService torrentStorage;
    private final BeanFactory beanFactory;
    private final Map<String, Pair<Thread, UploadProcess>> currentSeeds;

    @SuppressWarnings("unchecked") // can't check generic type.
    @Autowired
    public SeedManager(@Qualifier("TorrentStorage") StorageService torrentStorage, BeanFactory beanFactory) {
        this.torrentStorage = torrentStorage;
        this.beanFactory = beanFactory;
        this.currentSeeds = new HashMap<>();
        // Start seeding saved seeds:
        File memory = torrentStorage.get(MEMORY);
        if (memory.exists())
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(memory))) {
                for (Pair<String, File> p : (Pair<String, File>[]) in.readObject())
                    addProcess(p.t, p.s);
            } catch (IOException | ClassNotFoundException | URISyntaxException e) {
                log.error("Unable to load saved seeds.", e);
            }

    }

    /**
     * Starts the upload/seeding process in a new thread Blocks the thread until the torrent file is generated.
     * @param name
     * @param up
     * @throws URISyntaxException
     */
    public File addProcess(String name, File file) throws URISyntaxException {
        UploadProcess up = beanFactory.getBean(UploadProcess.class, name, file);
        Thread t = new Thread(up);
        t.start();
        currentSeeds.put(up.getName(), new Pair<>(t, up));

        // Wait for torrent to be generated before returning.
        while (up.getTorrent() == null)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error("Inturrupted while sleeping.", e);
            }
        return up.getTorrent();
    }

    /**
     * Stops the seeding/uploading process given by name
     *
     * @param name
     */
    public void stopProcess(String name) {
        log.info("Stopping process {}...", name);
        if (currentSeeds.containsKey(name))
            currentSeeds.remove(name).t.interrupt();
    }

    /**
     * Persists the current seeds so they can be started up again.
     *
     */
    @PreDestroy
    public void saveSeeds() {
        log.info("Saving seeds...");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(torrentStorage.get(MEMORY)))) {
            // Only write the name and File of the UploadProcess, because it's all we need to resume seeding.
            // Otherwise we need to make UploadProcess and all it's attributes Serializable.
            out.writeObject(currentSeeds.values().stream().map(p -> new Pair<>(p.s.getName(), p.s.getTorrent())).toArray(Pair[]::new));
        } catch (IOException e) {
            log.error("Error persisting the files currently being seeded.", e);
        }
    }
}
