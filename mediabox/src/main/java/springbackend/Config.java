package springbackend;

public class Config {
    public String serverUrl;
    public String trackerUrl;

    /**
     * @return the serverUrl
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * @param serverUrl the serverUrl to set
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * @return the trackerUrl
     */
    public String getTrackerUrl() {
        return trackerUrl;
    }

    /**
     * @param trackerUrl the trackerUrl to set
     */
    public void setTrackerUrl(String trackerUrl) {
        this.trackerUrl = trackerUrl;
    }
}
