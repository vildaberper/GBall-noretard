package GBall.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPSocket {

	private final DatagramSocket socket;
	private Thread receiveThread;

	public UDPSocket() throws SocketException {
		socket = new DatagramSocket();
	}

	public UDPSocket(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	public void send(Location target, Packet packet) {
		try {
			socket.send(packet.toDatagramPacket(target));
		} catch (IOException e) {
			e.printStackTrace();
			socket.close();
		}
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
						e.printStackTrace();
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

}
