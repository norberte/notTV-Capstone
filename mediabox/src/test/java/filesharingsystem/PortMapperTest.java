package filesharingsystem;

import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PortMapperTest {
    private PortMapper mapper;
    
    @Before
    public void setup() {
	try {
	    mapper = new ClingPortMapper(1234);
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	}
    }
    
    @Test
    public void testSetup() {
	try {
	    mapper.setup();
	} catch (PortMapException e) {
	    Assert.fail();
	}
    }
}
