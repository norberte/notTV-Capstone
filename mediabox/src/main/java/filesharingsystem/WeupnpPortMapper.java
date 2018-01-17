package filesharingsystem;

import java.io.IOException;
import java.net.InetAddress;

import javax.xml.parsers.ParserConfigurationException;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class WeupnpPortMapper implements PortMapper {
    private static final Logger log = LoggerFactory.getLogger(WeupnpPortMapper.class);
    private final int port;
    private GatewayDevice gate;

    public WeupnpPortMapper(int port) throws PortMapException {
	// Discover gateways.
	log.info("Starting weupnp and discovering gateways...");
	GatewayDiscover discover = new GatewayDiscover();
	try {
	    discover.discover();
	    // Get the discovered gateway.
	    gate = discover.getValidGateway();
	    if (gate == null) {
		log.warn("Unable to find a valid gateway.");
		throw new PortMapException("No valid gateway found.");
	    }
	} catch (IOException | SAXException | ParserConfigurationException e) {
	    log.error("Error discovering a valid gateway.", e);
	    throw new PortMapException("Error discovering a valid gateway.", e);
	}
	this.port = port;
    }
    
    @Override
    public void setup() throws PortMapException {
	// http://bitletorg.github.io/weupnp/
	try {
	    log.info("Found gateway device. \n{} ({})", gate.getModelName(), gate.getModelDescription());
	    InetAddress localAddress = gate.getLocalAddress();
	    log.info("Using local address: {}", localAddress);

	    // set up port mapping
	    log.info("Attempting to map port {}", port);
	    PortMappingEntry portMapping = new PortMappingEntry();
	    // Check if mapping exists already.
	    if(!gate.getSpecificPortMappingEntry(port, "TCP", portMapping)) {
		log.info("Sending mapping request");
		// true if portmappig is successful
		if(!gate.addPortMapping(port, port, localAddress.getHostAddress(), "TCP", "notTV Torrent Port Forward")) {
		    log.warn("Unable to add a port mapping.");
		}
	    }
	} catch (IOException | SAXException  e) {
	    log.error("Error setting up weupnp", e);
	    throw new PortMapException("Error setting up weupnp", e);
	}
    }

    @Override
    public void shutdown() {
	try {
	    gate.deletePortMapping(port, "TCP");
	} catch (IOException | SAXException e) {
	    log.error("Error deleting port mapping.", e);
	}
    }
}
