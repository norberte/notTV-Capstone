package filesharingsystem;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;

public class ClingPortMapper implements PortMapper {
    private UpnpService upnpService;
    private PortMapping[] map;
    public ClingPortMapper(int port) throws UnknownHostException {
	// Create TCP and UDP port mappings.
	map = new PortMapping[] {
	    new PortMapping(
		port,
		InetAddress.getLocalHost().getHostAddress(),
		PortMapping.Protocol.TCP,
		"TCP PORT Forwarding"
	    ),
	    new PortMapping(
		port,
		InetAddress.getLocalHost().getHostAddress(),
		PortMapping.Protocol.UDP,
		"UDP PORT Forwarding"
	    )
	};
	upnpService = new UpnpServiceImpl();
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
