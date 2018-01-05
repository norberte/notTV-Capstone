package filesharingsystem;

public interface PortMapper {
    /**
     * Sets up the port mapping to map
     * from WAN to LAN with the given port.
     * @param port - port to listen to.
     * @throws PortMapException - if an error occurs during setup.
     */
    void setup() throws PortMapException;

    /**
     * Shuts down the port mapping,
     * deleting all port forwarding done.
     */
    void shutdown();
}
