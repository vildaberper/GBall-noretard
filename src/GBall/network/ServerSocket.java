package GBall.network;

import java.io.IOException;

public class ServerSocket {

	public interface ServerSocketListener {

		public void onConnect(Socket socket);

	}

	private final java.net.ServerSocket socket;
	private Thread receiveThread;

	public ServerSocket(int port) throws IOException {
		socket = new java.net.ServerSocket(port);
	}

	public void open(final ServerSocketListener listener) {
		receiveThread = new Thread() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						java.net.Socket clientSocket = socket.accept();
						listener.onConnect(new Socket(clientSocket));
					} catch (IOException e) {
						e.printStackTrace();
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

}
