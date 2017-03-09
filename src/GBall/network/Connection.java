package GBall.network;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import GBall.engine.Single;
import GBall.engine.Util;
import GBall.network.Socket.SocketListener;

public class Connection implements SocketListener {

	public interface ConnectionListener {

		public void onDisconnect(Connection source);

		public void onReceive(Connection source, Object o);

	}

	private final Socket socket;
	private Thread sendThread = null;
	private Thread pingThread = null;
	private final AtomicBoolean open = new AtomicBoolean();

	private final Map<Long, Packet> sendBuffer = new ConcurrentHashMap<Long, Packet>();

	private long sendId = Long.MIN_VALUE;

	private final Set<Long> missed = new HashSet<Long>();
	private long receiveId = Long.MIN_VALUE;

	public final Location location;

	private final ConnectionListener listener;

	private Single<Long> lastReceived = new Single<Long>();

	private final Connection instance = this;

	public Connection(Socket socket, Location location, ConnectionListener listener) {
		this.socket = socket;
		this.location = location;
		this.listener = listener;
	}

	public void open() {
		lastReceived.setValue(Util.millis());

		open.set(true);
		sendThread = new Thread() {
			@Override
			public void run() {
				while (open.get())
					if (!sendBuffer.isEmpty()) {
						sendBuffer.entrySet().forEach(e -> send(e.getValue()));
						Util.sleep(50);
					} else
						Util.sleep(Long.MAX_VALUE);
			}
		};
		pingThread = new Thread() {
			@Override
			public void run() {
				while (open.get()) {
					sendPing();
					Util.sleep(50);
					if (!isConnected()) {
						close();
						listener.onDisconnect(instance);
					}
				}
			}
		};
		sendThread.start();
		pingThread.start();
	}

	public void close() {
		open.set(false);
		if (sendThread != null) {
			sendThread.interrupt();
			pingThread.interrupt();
		}
		sendDisconnect();
	}

	private boolean isConnected() {
		synchronized (lastReceived) {
			return lastReceived.getValue() != -1 && Util.millis() - lastReceived.getValue() < 1000L;
		}
	}

	public synchronized void send(Serializable o) {
		if (sendBuffer.size() > 99) {
			System.out.println("Connection overload: too many packets to send");
			close();
			return;
		}

		sendBuffer.put(sendId, new Payload(sendId, o).toPacket());
		++sendId;
		sendThread.interrupt();
	}

	private void send(Packet packet) {
		socket.send(location, packet);
	}

	private void sendACK(long id) {
		send(new Payload(id).toPacket());
	}

	private void sendPing() {
		send(new Packet(new Ping()));
	}

	private void sendDisconnect() {
		send(new Packet(new Disconnect()));
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		if (!open.get())
			return;

		Object o = packet.getObject();

		synchronized (lastReceived) {
			if (lastReceived.getValue() != -1)
				lastReceived.setValue(o instanceof Disconnect ? -1 : Util.millis());
		}

		if (!(o instanceof Payload))
			return;

		Payload payload = (Payload) o;

		if (payload.isACK()) {
			sendBuffer.remove(payload.id);
			return;
		}

		sendACK(payload.id);

		if (payload.id >= receiveId) {
			if (payload.id - receiveId + missed.size() > 100) {
				System.out.println("Connection overload: too many missed packets");
				close();
				return;
			}
			for (long l = receiveId; l < payload.id; ++l)
				missed.add(l);
			receiveId = payload.id + 1;
		} else if (missed.contains(payload.id))
			missed.remove(payload.id);
		else
			return;
		listener.onReceive(this, payload.o);
	}

}
