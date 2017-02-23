package GBall;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import GBall.engine.Const;
import GBall.engine.GameWindow;
import GBall.engine.Ship;
import GBall.engine.event.ControllerEvent;
import GBall.network.Location;
import GBall.network.Packet;
import GBall.network.ServerSocket;
import GBall.network.ServerSocket.ServerSocketListener;
import GBall.network.Socket;
import GBall.network.Socket.SocketListener;

import static GBall.engine.Util.*;

public class Server implements ServerSocketListener, SocketListener {

	public static void main(String[] args) throws IOException {
		Server s = new Server();
		s.run();
	}

	private class Client {

		public final Socket socket;

		public long id = -1;

		public Client(Socket socket) {
			this.socket = socket;
		}

	}

	private Map<Location, Client> clients = new ConcurrentHashMap<Location, Client>();

	private final ServerSocket socket;

	private final Game game;
	private final GameWindow gw;

	public Server() throws IOException {
		socket = new ServerSocket(25565);
		game = new Game();
		gw = new GameWindow(game);
	}

	public void run() {
		socket.open(this);

		game.reset();

		while (true) {
			game.tick();

			clients.entrySet().forEach(e -> e.getValue().socket.send(new Packet(game.getState())));

			gw.repaint();
			sleep(1.0 / Const.TARGET_FPS);
		}
	}

	@Override
	public void onConnect(Socket socket) {
		Client client = new Client(socket);

		if ((client.id = game.addShip()) != -1) {
			clients.put(socket.location, client);
			socket.open(this);
		} else
			socket.close();
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		Client client = clients.get(source);
		Ship ship = game.getShip(client.id);
		ControllerEvent event = (ControllerEvent) packet.getObject();

		if (event.press)
			ship.onPress(event.direction);
		else
			ship.onRelease(event.direction);

		System.out.println("potatis");
	}

}
