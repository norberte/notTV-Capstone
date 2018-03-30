package filesharingsystem.process;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.Client.ClientState;
import com.turn.ttorrent.client.ConnectionHandler;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.client.announce.Announce;
import com.turn.ttorrent.client.peer.SharingPeer;
import com.turn.ttorrent.common.Peer;
import com.turn.ttorrent.common.Torrent;

import sun.misc.Unsafe;

/**
 * Horrible hack to avoid modifying the library. I'm so sorry..
 * @author
 */
public class WANClient {
    static class Pair {
        public final Client            client;
        public final InetSocketAddress address;

        public Pair(Client client, InetSocketAddress address) {
            this.client = client;
            this.address = address;
        }
    };

    public static class ClientInitializationException extends Exception {
        private static final long serialVersionUID = 3182947996648682383L;

        ClientInitializationException(String message) {
            super(message);
        }

        ClientInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(WANClient.class);
    private static final Unsafe unsafe;

    static {
        unsafe = getUnsafe();
    }

    private static Unsafe getUnsafe() {
        // Can't actually instantiate Unsafe because of it's private constructor.
        // Unsafe has an instance of itself contained in 'theUnsafe'.
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            log.error("Unable to get Unsafe Instance.", e);
        }
        return null;
    }

    public static Pair newWANClient(InetAddress bindAddress, InetAddress announceAddress, SharedTorrent torrent) throws IOException, ClientInitializationException {
        if (unsafe == null) {
            log.warn("Unsafe isn't initialized, cannot create client.");
            throw new ClientInitializationException("Unsafe isn't initialized, so Client can't be created. Check the logs.");
        }
        try {
            Client c = (Client) unsafe.allocateInstance(Client.class);

            // Tediously initialize c exactly like in Client, except
            // for bindAddress and announceAddress.

            // I am officially the devil.
            Field obs = c.getClass().getSuperclass().getDeclaredField("obs");
            obs.setAccessible(true);
            obs.set(c, new Vector<Object>());
            // Instantiate Client to allow a different address for binding and announcing:
            // See https://github.com/mpetazzoni/ttorrent/blob/master/core/src/main/java/com/turn/ttorrent/client/Client.java
            set(c, "torrent", torrent);
            set(c, "state", ClientState.WAITING);

            Field f = c.getClass().getDeclaredField("BITTORRENT_ID_PREFIX");
            f.setAccessible(true);
            String id = (String) f.get("BITTORRENT_ID_PREFIX") + UUID.randomUUID().toString().split("-")[4];

            // Initialize the incoming connection handler and register ourselves to
            // it.
            Constructor<ConnectionHandler> constructor = ConnectionHandler.class.getDeclaredConstructor(SharedTorrent.class, String.class, InetAddress.class);
            constructor.setAccessible(true);
            ConnectionHandler service = constructor.newInstance(torrent, id, bindAddress);
            set(c, "service", service);
            service.register(c);

            Peer self = new Peer(announceAddress.getHostAddress(), service.getSocketAddress().getPort(), ByteBuffer.wrap(id.getBytes(Torrent.BYTE_ENCODING)));
            set(c, "self", self);

            // Initialize the announce request thread, and register ourselves to it
            // as well.
            Announce announce = new Announce(torrent, self);
            set(c, "announce", announce);
            announce.register(c);

            log.info("BitTorrent client [{}] for {} started and listening at {}:{}...", new Object[] { self.getShortHexPeerId(), torrent.getName(), self.getIp(), self.getPort() });

            set(c, "peers", new ConcurrentHashMap<String, SharingPeer>());
            set(c, "connected", new ConcurrentHashMap<String, SharingPeer>());
            set(c, "random", new Random(System.currentTimeMillis()));

            return new Pair(c, service.getSocketAddress());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            log.error("Not surprisingly, this didn't work. Can't instantiate WANClient.");
            throw new ClientInitializationException("Unable to create client.", e);
        }
    }

    private static void set(Client c, String field, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = c.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(c, value);
    }
}
