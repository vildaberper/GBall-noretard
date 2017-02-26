package GBall.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import GBall.engine.Util;

public class TCPSocket {

	public final Location location;

	private final java.net.Socket socket;
	private final ObjectOutputStream oos;
	private final ObjectInputStream ois;
	private Thread receiveThread, sendThread;

	private Queue<Packet> sendBuffer = new ConcurrentLinkedQueue<Packet>();

	private AtomicBoolean sending = new AtomicBoolean();

	public TCPSocket(Location location) throws IOException {
		this.location = location;
		socket = new java.net.Socket(location.ip, location.port);
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	public TCPSocket(java.net.Socket socket) throws IOException {
		location = new Location(socket.getInetAddress(), socket.getPort());
		this.socket = socket;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	public synchronized void send(Packet packet) {
		sendBuffer.add(packet);
		sendThread.interrupt();
	}

	public void open(final SocketListener listener) {
		sending.set(true);
		sendThread = new Thread() {
			@Override
			public void run() {
				Packet toSend = null;
				while (sending.get()) {
					while ((toSend = sendBuffer.poll()) != null) {
						try {
							oos.writeObject(toSend);
						} catch (IOException e) {
							e.printStackTrace();
							close();
							return;
						}
					}
					Util.sleep(Long.MAX_VALUE);
				}
			}
		};
		sendThread.start();

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
		sending.set(false);
		sendThread.interrupt();
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