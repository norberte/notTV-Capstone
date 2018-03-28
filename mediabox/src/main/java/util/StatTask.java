package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import filesharingsystem.process.DownloadProcess;

public class StatTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(StatTask.class);
    private static final String[] CMD = {
        "/bin/bash",
        "-c",
        "top -bn1 | grep \"Cpu(s)\" | sed \"s/.*, *\\([0-9.]*\\)%* id.*/\\1/\" | awk '{print 100 - $1}'"
    };
    private static int INTERVAL = 100;
    private PrintWriter pw;
    private File f;
    private long startTime = -1l;
    private long oldSize = -1l;
    private DownloadProcess dp;

    public StatTask(File f) throws FileNotFoundException {
        this(f, null);
    }
    
    public StatTask(File f, DownloadProcess dp) throws FileNotFoundException {
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
            oldSize = f.length();
            Thread.sleep(INTERVAL);
            while(dp == null || dp.isFinished()) {
                // collect updated data.
                long endTime = System.currentTimeMillis();
                long currSize = f.length();

                // print as csv/
                StringBuilder out = new StringBuilder();
                out.append(startTime)
                    .append(',')
                    .append(endTime)
                    .append(',')
                    .append((currSize - oldSize) / (endTime - startTime))
                    .append(',')
                    .append(getCPU())
                    .append(',');
                pw.append(out);
                pw.flush();
                // update data.
                startTime = endTime;
                oldSize = currSize;
                Thread.sleep(INTERVAL);
            }
        } catch (InterruptedException e) {
            pw.flush();
            pw.close();
            return;
        }
        pw.flush();
        pw.close();
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
            return Double.parseDouble(s.next().trim());
        } catch (IOException e) {
            log.error("Error getting CPU.", e);
        } finally {
            if(s != null)
                s.close();
        }
        return Double.NaN;
    }
}
