package filesharingsystem.process;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
 * Horrible hack to avoid modifying the library.
 * I'm so sorry..
 * @author
 */
public class WANClient {
    private static final Logger log = LoggerFactory.getLogger(WANClient.class);
    
    public static Client newWANClient(InetAddress bindAddress, InetAddress announceAddress, SharedTorrent torrent) throws UnknownHostException, IOException {
	try {
	    // Can't actually instantiate Unsafe because of it's private constructor.
	    // Unsafe has an instance of itself contained in 'theUnsafe'.
	    Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
	    unsafeField.setAccessible(true);
	    Unsafe unsafe = (Unsafe) unsafeField.get(null);
	    Client c = (Client)unsafe.allocateInstance(Client.class);

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
	    String id =  (String)f.get("BITTORRENT_ID_PREFIX") + UUID.randomUUID().toString().split("-")[4];

	    // Initialize the incoming connection handler and register ourselves to
	    // it.
	    Constructor<ConnectionHandler> constructor = ConnectionHandler.class.getDeclaredConstructor(SharedTorrent.class, String.class, InetAddress.class);
	    constructor.setAccessible(true);
	    ConnectionHandler service = constructor.newInstance(torrent, id, bindAddress);
	    set(c, "service", service);
	    service.register(c);

	    Peer self = new Peer(
		announceAddress.getHostAddress(),
		service.getSocketAddress().getPort(),
		ByteBuffer.wrap(id.getBytes(Torrent.BYTE_ENCODING))
	    );
	    set(c, "self", self);

	    // Initialize the announce request thread, and register ourselves to it
	    // as well.
	    Announce announce = new Announce(torrent, self); 
	    set(c, "announce", announce);
	    announce.register(c);

	    log.info(
		"BitTorrent client [{}] for {} started and listening at {}:{}...",
		new Object[] {
		    self.getShortHexPeerId(), torrent.getName(), self.getIp(), self.getPort()
		}
	    );

	    set(c, "peers", new ConcurrentHashMap<String, SharingPeer>());
	    set(c, "connected", new ConcurrentHashMap<String, SharingPeer>());
	    set(c, "random", new Random(System.currentTimeMillis()));

	    return c;
	} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
	    // TODO Auto-generated catch block
	    log.error("Not surprisingly, this didn't work. Can't instantiate WANClient.", e);
	} 
	return null;
    }

    private static void set(Client c, String field, Object value)
	throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	Field f = c.getClass().getDeclaredField(field);
	f.setAccessible(true);
	f.set(c, value);
    }
}
