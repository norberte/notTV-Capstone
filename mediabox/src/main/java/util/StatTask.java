package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.peer.SharingPeer;

import filesharingsystem.process.DownloadProcess;

import springbackend.Application;

public class StatTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(StatTask.class);
    private static final String[] CMD = {
        "/bin/bash",
        "-c",
        "mpstat 1 1 | grep Average | grep -Eo '([0-9]|\\.)+$'"
    };
    // private static final String[] CMD = {
    //     "/bin/bash",
    //     "-c",
    //     "top -bn1 | grep \"Cpu(s)\" | sed \"s/.*, *\\([0-9.]*\\)%* id.*/\\1/\" | awk '{print 100 - $1}'"
    // };
    private static int INTERVAL = 1000;
    private PrintWriter pw;
    private File f;
    private long startTime = -1l;
    private Optional<DownloadProcess> dp;

    public StatTask(File f, Optional<DownloadProcess> dp) throws FileNotFoundException {
        // this.si = new SystemInfo();
        this.f = f;
        this.dp = dp;
        pw = new PrintWriter(f);
    }
    
    @Override
    public void run() {
        log.info("Starting Stat collection...");
        StringBuilder header = new StringBuilder();
        // print header
        header.append("START,")
            .append("END,")
            .append("DOWNLOAD_RATE,")
            .append("CPU_USAGE,")
            .append('\n');
        pw.append(header);
        pw.flush();
        try {
            // init data.
            startTime = System.currentTimeMillis();
            Thread.sleep(INTERVAL);
            while(!(dp.isPresent() && dp.get().isFinished())) {
                // collect updated data.
                long endTime = System.currentTimeMillis();

                // print as csv/
                StringBuilder out = new StringBuilder();
                out.append(startTime)
                    .append(',')
                    .append(endTime)
                    .append(',')
                    .append(getDownloadSpeed())
                    .append(',')
                    .append(getCPU())
                    .append(',')
                    .append('\n');
                pw.append(out);
                pw.flush();
                // update data.
                startTime = endTime;
                Thread.sleep(INTERVAL);
            }
        } catch (InterruptedException e) {
        } finally {
            pw.flush();
            pw.close();
            Application.exit();
        }
    }

    /**
     * Gets the download speed.
     * https://github.com/mpetazzoni/ttorrent/blob/master/core/src/main/java/com/turn/ttorrent/client/Client.java
     * Line ~443
     * @param client
     * @return
     */
    public double getDownloadSpeed() {
        float dl = 0;
        if(dp.isPresent()) {
            try {
                Field conn = Client.class.getDeclaredField("connected");
                conn.setAccessible(true);
                @SuppressWarnings("unchecked")
                ConcurrentMap<String, SharingPeer> connected = (ConcurrentMap<String, SharingPeer>) conn.get(dp.get().getClient());
                for (SharingPeer peer : connected.values()) {
                    dl += peer.getDLRate().get();
                }
                
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                log.error("Error getting dl speed", e);
            }
        }
        return dl/1024.0;
    }

    private double getCPU() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true); // This is the important part
        builder.command(CMD);
        builder.directory(this.f.getParentFile());
        Scanner s = null;
        try {
            Process p = builder.start();
            s = new Scanner(p.getInputStream()).useDelimiter("\\A");
            // https://superuser.com/questions/455005/how-do-i-see-the-cpu-load-of-a-server
            return 100.0 - Double.parseDouble(s.next().trim()); // gets idle%
        } catch (IOException e) {
            log.error("Error getting CPU.", e);
        } finally {
            if(s != null)
                s.close(); // called before return.
        }
        return Double.NaN;
    }
}
