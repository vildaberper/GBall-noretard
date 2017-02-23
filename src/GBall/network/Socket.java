package GBall.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Socket {

	public interface SocketListener {

		public void onReceive(Location source, Packet packet);

	}

	public final Location location;

	private final java.net.Socket socket;
	private final ObjectOutputStream oos;
	private final ObjectInputStream ois;
	private Thread receiveThread;

	public Socket(Location location) throws IOException {
		this.location = location;
		socket = new java.net.Socket(location.ip, location.port);
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	public Socket(java.net.Socket socket) throws IOException {
		location = new Location(socket.getInetAddress(), socket.getPort());
		this.socket = socket;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	public void send(Packet packet) {
		try {
			oos.writeObject(packet);
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	public void open(final SocketListener listener) {
		receiveThread = new Thread() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						listener.onReceive(location, (Packet) ois.readObject());
					} catch (IOException e) {
						close();
						return;
					} catch (ClassNotFoundException e) {
						System.out.println("Received invalid packet!");
					}
				}
			}
		};
		receiveThread.start();
	}

	public void close() {
		if (receiveThread != null)
			receiveThread.interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isOpen() {
		return !socket.isClosed();
	}

}
