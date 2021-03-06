package filesharingsystem;

import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;

/**
 * http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#section.ConfiguringTransports
 *
 * @author
 */
public class ClingUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration {

    @Override
    protected Namespace createNamespace() {
        return new Namespace("/upnp"); // This will be the servlet context path
    }

    @Override
    public StreamClient<?> createStreamClient() {
        return new org.fourthline.cling.transport.impl.jetty.StreamClientImpl(
            new org.fourthline.cling.transport.impl.jetty.StreamClientConfigurationImpl(
                this.getSyncProtocolExecutorService()
            )
        );
    }

    @Override
    public StreamServer<?> createStreamServer(NetworkAddressFactory networkAddressFactory) {
        return new org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl(
            new org.fourthline.cling.transport.impl.AsyncServletStreamServerConfigurationImpl(
                org.fourthline.cling.transport.impl.jetty.JettyServletContainer.INSTANCE, 
                networkAddressFactory.getStreamListenPort()
            )
        );
    }
}
