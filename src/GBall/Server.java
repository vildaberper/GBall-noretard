package GBall;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import GBall.Game.GameListener;
import GBall.engine.Const;
import GBall.engine.GameWindow;
import GBall.engine.Ship;
import GBall.engine.Time;
import GBall.engine.event.AddEntityEvent;
import GBall.engine.event.Event;
import GBall.engine.event.GoalEvent;
import GBall.engine.event.OffsetEvent;
import GBall.engine.event.StateEvent;
import GBall.network.Location;
import GBall.network.Packet;
import GBall.network.SocketListener;
import GBall.network.TCPServerSocket;
import GBall.network.TCPServerSocket.TCPServerSocketListener;
import GBall.network.TCPSocket;

import static GBall.engine.Util.*;

public class Server implements SocketListener, GameListener, TCPServerSocketListener {

	public static void main(String[] args) throws IOException {
		Server s = new Server();
		s.run();
	}

	private class Client {

		public long id = -1;

		public final TCPSocket socket;

		public Client(TCPSocket socket) {
			this.socket = socket;
		}

	}

	private Map<Location, Client> clients = new ConcurrentHashMap<Location, Client>();

	private final TCPServerSocket socket;

	private long startTime;

	private final Game game;
	private final GameWindow gw;

	public Server() throws IOException {
		socket = new TCPServerSocket(25565);
		game = new Game(this);
		gw = new GameWindow(game);
	}

	private Client getClient(long id) {
		for (Client c : clients.values())
			if (c.id == id)
				return c;
		return null;
	}

	public void run() {
		socket.open(this);

		game.reset();
		game.saveState();

		startTime = Time.getTime();
		System.out.println("startTime=" + startTime);
		while (true) {
			/*
			 * if (game.getFrame() % Const.PERIODIC_STATES == 0L)
			 * broadcast(game.getState());
			 */
			synchronized (game) {
				game.tick();
				gw.repaint();
			}

			long timeToSleep = startTime + game.getFrame() * Const.FRAME_INCREMENT - Time.getTime();

			if (timeToSleep > 0)
				sleep(timeToSleep);
		}
	}

	private void broadcast(Serializable s, Client... except) {
		clients.entrySet().forEach(e -> {
			for (Client c : except)
				if (e.getValue().equals(c))
					return;

			e.getValue().socket.send(new Packet(s));
		});
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		Client client = clients.get(source);

		Event event = (Event) packet.getObject();

		/*
		 * TODO validate ControllerEvent and entity id.
		 */

		broadcast(event, client);
		game.pushEvent(event);
	}

	@Override
	public void onGoal(boolean red) {
		GoalEvent event = new GoalEvent(game.getFrame(), red);

		broadcast(event);
		game.pushEvent(event);
	}

	@Override
	public void onConnect(TCPSocket socket) {
		Client client = new Client(socket);

		clients.put(socket.location, client);
		socket.open(this);

		AddEntityEvent aee;
		StateEvent gse;
		Ship ship;
		synchronized (game) {
			if ((ship = game.nextShip()) == null) {
				socket.close();
				clients.remove(socket.location);
				return;
			}
			gse = new StateEvent(game.getState());
			aee = new AddEntityEvent(game.getFrame() + 2, ship.clone());
		}
		client.id = ship.id;
		socket.send(new Packet(startTime));
		socket.send(new Packet(client.id));
		socket.send(new Packet(gse.framestamp));
		socket.send(new Packet(gse));
		broadcast(aee);
		game.pushEvent(aee);

	}

	@Override
	public void onTimewarp(long offset, long entityId) {
		Client c = getClient(entityId);

		++offset;
		if (c != null)
			c.socket.send(new Packet(new OffsetEvent(0, offset < 0 ? 0 : offset)));
	}

	@Override
	public void onInvalidInput() {
		StateEvent gse = new StateEvent(game.getState());

		System.out.println("!!! invalid input !!!");
		broadcast(gse);
		game.pushEvent(gse);
	}

}
