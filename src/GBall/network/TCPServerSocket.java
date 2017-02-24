package GBall.network;

import java.io.IOException;

public class TCPServerSocket {

	public interface TCPServerSocketListener {

		public void onConnect(TCPSocket socket);

	}

	private final java.net.ServerSocket socket;
	private Thread receiveThread;

	public TCPServerSocket(int port) throws IOException {
		socket = new java.net.ServerSocket(port);
	}

	public void open(final TCPServerSocketListener listener) {
		receiveThread = new Thread() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						java.net.Socket clientSocket = socket.accept();
						listener.onConnect(new TCPSocket(clientSocket));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		receiveThread.start();
	}

	public void close() {
		receiveThread.interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}