package springbackend;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private String serverUrl;
    private URL trackerUrl;
    private int trackerPort;
    private InetAddress publicIp;
    
	public Config(String serverUrl, String trackerUrl, int trackerPort) throws MalformedURLException {
        try (Scanner s = new Scanner(new URL(serverUrl + "/info/public-ip").openStream(), "UTF-8")) {
            publicIp = InetAddress.getByName(s.next());
            log.info("{}", publicIp);
        } catch(IOException e) {
            log.error("Error getting public ip", e);
        }
        this.serverUrl = serverUrl;
        this.trackerUrl = new URL(trackerUrl);
        this.trackerPort = trackerPort;
    }

    /**
     * @return the serverUrl
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * @return the trackerUrl
     */
    public URL getTrackerUrl() {
        return trackerUrl;
    }

    /**
     * @return the trackerPort
     */
    public int getTrackerPort() {
        return trackerPort;
    }

    /**
     * @return the publicIp
     */
    public InetAddress getPublicIp() {
        return publicIp;
    }
}
