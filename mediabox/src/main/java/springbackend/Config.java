package springbackend;

public class Config {
    public String serverUrl;
    public String trackerUrl;
    public String secureServerUrl;


    /**
     * @return the serverUrl
     */
    public String getServerUrl() {
  return serverUrl;
    }

    /**
     * @param secure_serverUrl the serverUrl to set
     */
    public void setSecureServerUrl(String secureServerUrl) {
  this.secureServerUrl = secureServerUrl;
    }

    /**
     * @return the secure_serverUrl
     */
    public String getSecureServerUrl() {
	return secureServerUrl;
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
