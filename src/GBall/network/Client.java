package GBall.network;

import java.io.Serializable;
import java.net.SocketException;

import GBall.network.Connection.ConnectionListener;

public class Client implements ConnectionListener {

	public interface ClientListener {

		public void onDisconnect();

		public void onReceive(Object o);

	}

	private final Socket socket;
	private final Connection connection;

	private final ClientListener listener;

	public Client(Location server, ClientListener listener) throws SocketException {
		this.socket = new Socket();
		this.listener = listener;

		socket.open(connection = new Connection(socket, server, this));
		connection.open();
	}

	public void close() {
		connection.close();
		socket.close();
	}

	@Override
	public void onReceive(Connection source, Object o) {
		listener.onReceive(o);
	}

	public void send(Serializable o) {
		connection.send(o);
	}

	@Override
	public void onDisconnect(Connection source) {
		listener.onDisconnect();
	}

}
