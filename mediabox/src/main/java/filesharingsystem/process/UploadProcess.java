package filesharingsystem.process;

public interface UploadProcess extends Runnable {
    /**
     * Stops the seeding process.
     *
     */
    void stop();
}
