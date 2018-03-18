package springbackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import filesharingsystem.PortMapException;
import filesharingsystem.PortMapper;

import util.SeedManager;

@SpringBootApplication
@ImportResource("classpath:app-config.xml")
public class Application implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    @Autowired
    PortMapper portMapper;
    @Autowired
    SeedManager seedManager;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //config.id = Integer.parseInt(args[0]);
    }

    @Override
    public void run(String... args) {
	portMap();
    }

    public void portMap() {
	// configure port forwarding.
	try {
	    portMapper.setup();
	} catch (PortMapException e) {
	    log.warn("Unable to setup the port forwarding.", e);
	    // TODO: send notification to UI to inform user
	    // that they need to enable upnp.
	    // Bonus: check portforwarding somehow to allow manual port forwarding.
	}
    }
}
