package GBall.network;

import java.io.Serializable;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import GBall.network.Connection.ConnectionListener;
import GBall.network.Socket.SocketListener;

public class Server<T> implements SocketListener, ConnectionListener {

	public interface ServerListener<T> {

		public void onConnect(ServerClient<T> client);

		public void onDisconnect(ServerClient<T> client);

		public void onReceive(ServerClient<T> client, Object o);

	}

	private final Socket socket;

	private final ServerListener<T> listener;

	private final Map<Location, ServerClient<T>> clients = new ConcurrentHashMap<Location, ServerClient<T>>();

	public Server(int port, ServerListener<T> listener) throws SocketException {
		this.socket = new Socket(port);
		this.listener = listener;

		socket.open(this);
	}

	public int size() {
		return clients.size();
	}

	public boolean isEmpty() {
		return clients.isEmpty();
	}

	public ServerClient<T> getClient(T data) {
		if (data != null)
			for (ServerClient<T> client : clients.values())
				if (data.equals(client.getData()))
					return client;
		return null;
	}

	public void forEachClient(Consumer<? super ServerClient<T>> func) {
		clients.entrySet().forEach(e -> func.accept(e.getValue()));
	}

	public void broadcast(Serializable o, Object... except) {
		forEachClient(e -> {
			for (Object sc : except)
				if (e.equals(sc))
					return;

			e.send(o);
		});
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		ServerClient<T> client;

		if (!clients.containsKey(source)) {
			clients.put(source, client = new ServerClient<T>(new Connection(socket, source, this)));
			client.connection.open();
			listener.onConnect(client);
		} else
			client = clients.get(source);

		client.connection.onReceive(source, packet);
	}

	@Override
	public void onReceive(Connection source, Object o) {
		listener.onReceive(clients.get(source.location), o);
	}

	@Override
	public void onDisconnect(Connection source) {
		ServerClient<T> client = clients.get(source.location);

		clients.remove(source.location);
		listener.onDisconnect(client);
	}

}
