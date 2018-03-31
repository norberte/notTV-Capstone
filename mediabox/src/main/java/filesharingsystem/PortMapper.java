package filesharingsystem;

import javax.annotation.PreDestroy;

public interface PortMapper {
    /**
     * Adds a new port mapping to the given port
     *
     * @param port
     */
    void add(int port);

    /**
     * Shuts down the port mapping, deleting all port forwarding done.
     */
    @PreDestroy
    void shutdown();
}
