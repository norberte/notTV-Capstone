package springbackend;

public class Config {
    public String serverUrl;
    public String trackerUrl;
    //public int id;

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

    ///**
    // * @return the id
    // */
    ////return id;
    //}
    ///**
    // * @param id the id to set
    // */
    //public void setId(int id) {
    //this.id = id;
    //}
}
