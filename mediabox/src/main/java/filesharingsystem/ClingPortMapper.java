package filesharingsystem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import springbackend.Config;

public class ClingPortMapper implements PortMapper {
    private static final Logger log = LoggerFactory.getLogger(ClingPortMapper.class);
    private String host;
    private UpnpService upnpService;
    private Config config;
    private Set<Integer> ports;
    private RegistryListener old;

    public ClingPortMapper() throws UnknownHostException {
        ports = new HashSet<>();
        host = InetAddress.getLocalHost().getHostAddress();
        // customize for use with tomcat.
        // http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#section.BasicAPI.UpnpService.Configuration
        // http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#section.ConfiguringTransports
        upnpService = new UpnpServiceImpl(new ClingUpnpServiceConfiguration());
    }
    
    public void add(int port) {
        upnpService.getRegistry().addListener(new PortMappingListener(new PortMapping[] {
            new PortMapping(port, host, PortMapping.Protocol.TCP, "TCP PORT Forwarding"),
            new PortMapping(port, host, PortMapping.Protocol.UDP, "UDP PORT Forwarding")
        }));
        upnpService.getControlPoint().search();
    }

    @PreDestroy
    @Override
    public void shutdown() {
        log.info("Removing port mappings...");
        upnpService.shutdown();
    }
}
