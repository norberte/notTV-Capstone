package filesharingsystem;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClingPortMapper implements PortMapper {
    private static final Logger log = LoggerFactory.getLogger(ClingPortMapper.class);
    private UpnpService upnpService;
    private PortMapping[] map;
    public ClingPortMapper(int port) throws UnknownHostException {
	log.info("Initializing port mapper on port {}...", port);
	String host = InetAddress.getLocalHost().getHostAddress();
	log.info("Host Address: {}", host);
	// Create TCP and UDP port mappings.
	map = new PortMapping[] {
	    new PortMapping(port, host, PortMapping.Protocol.TCP, "TCP PORT Forwarding"),
	    new PortMapping(port, host, PortMapping.Protocol.UDP, "UDP PORT Forwarding"),
	};

	// customize for use with tomcat.
	// http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#section.BasicAPI.UpnpService.Configuration
	// http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#section.ConfiguringTransports
	upnpService = new UpnpServiceImpl(new ClingUpnpServiceConfiguration());
    }
    
    @Override
    public void setup() throws PortMapException {
	// Register listeners for both port mappings.
	RegistryListener registryListener = new PortMappingListener(map);
	upnpService.getRegistry().addListener(registryListener);
	upnpService.getControlPoint().search();
    }

    @Override
    public void shutdown() {
	upnpService.shutdown();
    }
    
}
