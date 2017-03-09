package GBall.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Socket {

	public interface SocketListener {

		public void onReceive(Location source, Packet packet);

	}

	private final DatagramSocket socket;
	private Thread receiveThread = null;

	public Socket() throws SocketException {
		socket = new DatagramSocket();
	}

	public Socket(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	public void open(final SocketListener listener) {
		receiveThread = new Thread() {
			@Override
			public void run() {
				byte[] buffer = new byte[32768];

				while (!Thread.interrupted()) {
					DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
					try {
						socket.receive(datagramPacket);
					} catch (IOException e) {
						close();
						return;
					}
					listener.onReceive(new Location(datagramPacket.getAddress(), datagramPacket.getPort()),
							new Packet(datagramPacket));
				}
			}
		};
		receiveThread.start();
	}

	public void close() {
		if (receiveThread != null)
			receiveThread.interrupt();
		socket.close();
	}

	public boolean isOpen() {
		return !socket.isClosed();
	}

	public void send(Location target, Packet packet) {
		try {
			socket.send(packet.toDatagramPacket(target));
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

}
