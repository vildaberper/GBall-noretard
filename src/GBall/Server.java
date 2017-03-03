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
import GBall.network.Ack;
import GBall.network.Connection;
import GBall.network.Connection.ConnectionListener;
import GBall.network.Handshake;
import GBall.network.Location;
import GBall.network.Packet;
import GBall.network.SocketListener;
import GBall.network.UDPSocket;

import static GBall.engine.Util.*;

public class Server implements ConnectionListener, SocketListener, GameListener {

	public static void main(String[] args) throws IOException {
		Server s = new Server();
		s.run();
	}

	
	private Map<Location, Connection> connections = new ConcurrentHashMap<Location, Connection>();

	private final UDPSocket socket;

	private long startTime;

	private final Game game;
	private final GameWindow gw;

	public Server() throws IOException {
		socket = new UDPSocket(25565);
		game = new Game(this);
		gw = new GameWindow(game);
	}

	private Connection getConnection(long id) {
		for (Connection c : connections.values())
			if (c.clientId == id)
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
			synchronized (game) {
				game.tick();

				// if (game.getFrame() % Const.PERIODIC_STATES == 0L)
				// broadcast(new StateEvent(game.getState()));

				gw.repaint();
			}

			long timeToSleep = startTime + game.getFrame() * Const.FRAME_INCREMENT - Time.getTime();

			if (timeToSleep > 0)
				sleep(timeToSleep);
		}
	}
	
	private void broadcast(Serializable s, Connection... except) {
		connections.entrySet().forEach(e -> {
			for (Connection c : except)
				if (e.getValue().equals(c))
					return;
			
			e.getValue().send(new Packet(s));
		});
	}
	
	@Override
	public void onReceive(Location source, Packet packet) {
		Connection connection = connections.get(source);
		
		if (connection == null) {
			new Connection(source, socket, this).connect();
			return;
		}
		if (packet.getObject() instanceof Ack) {
			Ack ack = (Ack) packet.getObject();
			connection.addAck(ack.id);
			return;
		}
		connection.sendAck(packet.id);
		
		if (connection.handled(packet.id))
			return;
		
		connection.addHandled(packet.id);		
		
		if (packet.getObject() instanceof Long)
			return;
		
		Event event = (Event) packet.getObject();		
		/*
		 * TODO validate ControllerEvent and entity id.
		 */

		broadcast(event, connection);
		game.pushEvent(event);
	}	

	@Override
	public void onGoal(boolean red) {
		GoalEvent event = new GoalEvent(game.getFrame(), red);

		broadcast(event);
		game.pushEvent(event);
	}

	@Override
	public void onTimewarp(long offset, long entityId) {
		Connection c = getConnection(entityId);		

		++offset;
		if (c != null)
			c.send(new Packet(new OffsetEvent(0, offset)));
	}

	@Override
	public void onInvalidInput() {
		System.out.println("!!! invalid input !!!");
	}

	@Override
	public void onConnect(Connection c) {		
		connections.put(c.location, c);
		
		AddEntityEvent aee;
		StateEvent gse;
		Ship ship;
		synchronized (game) {
			if ((ship = game.nextShip()) == null) {
				c.close();
				connections.remove(c.location);
				return;
			}
			gse = new StateEvent(game.getState());
			aee = new AddEntityEvent(game.getFrame() + 1, ship.clone());
		}
		c.clientId = ship.id;
		game.pushEvent(aee);
		c.send(new Packet(new Handshake(startTime, c.clientId, gse.frame, gse)));
		
		broadcast(aee);		
	}

}
