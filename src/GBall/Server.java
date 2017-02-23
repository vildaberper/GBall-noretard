package GBall;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import GBall.Game.GameListener;
import GBall.engine.Const;
import GBall.engine.GameWindow;
import GBall.engine.Ship;
import GBall.engine.event.ControllerEvent;
import GBall.engine.event.Event;
import GBall.engine.event.GoalEvent;
import GBall.network.Location;
import GBall.network.Packet;
import GBall.network.Socket;
import GBall.network.Socket.SocketListener;

import static GBall.engine.Util.*;

public class Server implements SocketListener, GameListener {

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
		game = new Game(this);
		gw = new GameWindow(game);
	}

	public void run() {
		socket.open(this);

		game.reset();

		while (true) {
			game.tick();
			gw.repaint();
			sleep(Const.FRAME_INCREMENT);
		}
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		Client client;
		if (!clients.containsKey(source)) {
			client = new Client();
			if ((client.id = game.addShip()) != -1) {
				clients.put(source, client);
				socket.send(source, new Packet(client.id));
				clients.entrySet().forEach(e -> socket.send(e.getKey(), new Packet(game.getState())));
			} else
				return;
		} else
			client = clients.get(source);

		Event event = (Event) packet.getObject();

		clients.entrySet().forEach(e -> {
			if (!e.getValue().equals(client))
				socket.send(e.getKey(), new Packet(event));
		});

		game.pushEvent(event);

		System.out.println("got event");
	}

	@Override
	public void onGoal(boolean red) {
		
		clients.entrySet().forEach(e -> {
				socket.send(e.getKey(), new Packet(event));
		});
		
		game.pushEvent(event);
	}

}
