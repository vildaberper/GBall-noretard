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
import GBall.network.Socket;
import GBall.network.Socket.SocketListener;

import static GBall.engine.Util.*;

public class Server implements SocketListener {

	public static void main(String[] args) throws IOException {
		Server s = new Server();
		s.run();
	}

	private class Client {

		public long id = -1;

		public Client() {

		}

	}

	private Map<Location, Client> clients = new ConcurrentHashMap<Location, Client>();

	private final Socket socket;

	private final Game game;
	private final GameWindow gw;

	public Server() throws IOException {
		socket = new Socket(25565);
		game = new Game();
		gw = new GameWindow(game);
	}

	public void run() {
		socket.open(this);

		game.reset();

		while (true) {
			game.tick();

			clients.entrySet().forEach(e -> socket.send(e.getKey(), new Packet(game.getState())));

			gw.repaint();
			sleep(1.0 / Const.TARGET_FPS);
		}
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		Client client;
		if (!clients.containsKey(source)) {
			client = new Client();
			if ((client.id = game.addShip()) != -1)
				clients.put(source, client);
			else
				return;
		} else
			client = clients.get(source);

		Ship ship = game.getShip(client.id);
		ControllerEvent event = (ControllerEvent) packet.getObject();

		if (event.press)
			ship.onPress(event.direction);
		else
			ship.onRelease(event.direction);

		System.out.println("potatis");
	}

}
