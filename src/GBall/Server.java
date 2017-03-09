package GBall;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.SocketException;

import GBall.Game.GameListener;
import GBall.engine.Const;
import GBall.engine.GameWindow;
import GBall.engine.Ship;
import GBall.engine.Time;
import GBall.engine.event.AddEntityEvent;
import GBall.engine.event.ControllerEvent;
import GBall.engine.event.Event;
import GBall.engine.event.GoalEvent;
import GBall.engine.event.OffsetEvent;
import GBall.engine.event.RemoveEntityEvent;
import GBall.engine.event.ResetGoalsEvent;
import GBall.engine.event.StateEvent;
import GBall.network.ServerClient;
import GBall.network.ServerConnection;
import GBall.network.ServerConnection.ServerConnectionListener;

import static GBall.engine.Util.*;

public class Server implements GameListener, ServerConnectionListener<Server.Client> {

	public static void main(String[] args) throws IOException {
		Server s = new Server();
		s.run();
	}

	protected class Client {

		public long id = -1;

		public Client(long id) {
			this.id = id;
		}

	}

	private ServerConnection<Server.Client> serverConnection;

	private long startTime;

	private final Game game;
	private final GameWindow gw;

	public Server() {
		game = new Game(this);
		gw = new GameWindow(game, "- SERVER ");
	}

	private ServerClient<Server.Client> getClient(long id) {
		return serverConnection.getClient(new Client(id));
	}

	public void run() throws SocketException {
		serverConnection = new ServerConnection<Server.Client>(25565, this);

		game.reset();
		game.saveState();

		startTime = Time.getTime();
		System.out.println("startTime=" + startTime);

		gw.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_D) {
					synchronized (game) {
						game.debug = !game.debug;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});

		while (true) {
			synchronized (game) {
				game.tick();

				if (game.getFrame() % Const.PERIODIC_STATES == 0L)
					serverConnection.broadcast(new StateEvent(game.getState()));

				gw.repaint();
			}

			long timeToSleep = startTime + game.getFrame() * Const.FRAME_INCREMENT - Time.getTime();

			if (timeToSleep > 0)
				sleep(timeToSleep);
		}
	}

	@Override
	public void onConnect(ServerClient<Server.Client> client) {
		AddEntityEvent aee = null;
		StateEvent gse;
		Ship ship;
		long id = -1;

		synchronized (game) {
			if ((ship = game.nextShip()) != null) {
				aee = new AddEntityEvent(game.getFrame() + 1, ship.clone());
				id = ship.id;
			}
			gse = new StateEvent(game.getState());
		}
		client.setData(new Client(id));
		client.send(new Handshake(startTime, id, gse));
		if (aee != null) {
			serverConnection.broadcast(aee);
			game.pushEvent(aee);
		}
	}

	@Override
	public void onReceive(ServerClient<Server.Client> client, Object o) {
		if (!(o instanceof Event))
			return;

		Event event = (Event) o;

		if (!(event instanceof ControllerEvent && ((ControllerEvent) event).entityId == client.getData().id))
			return;

		serverConnection.broadcast(event, client);
		game.pushEvent(event);
	}

	@Override
	public void onGoal(boolean red) {
		GoalEvent event = new GoalEvent(game.getFrame(), red);

		serverConnection.broadcast(event);
		game.pushEvent(event);
	}

	@Override
	public void onTimewarp(long offset, long entityId) {
		ServerClient<Server.Client> client = getClient(entityId);

		++offset;
		if (client != null)
			client.send(new OffsetEvent(offset));
	}

	@Override
	public void onInvalidInput() {
		System.out.println("!!! invalid input !!!");
	}

	@Override
	public void onDisconnect(ServerClient<Client> client) {
		if (client.getData().id == -1)
			return;

		RemoveEntityEvent event = new RemoveEntityEvent(game.getFrame() + 1, client.getData().id);

		serverConnection.broadcast(event);
		game.pushEvent(event);

		if (serverConnection.isEmpty())
			game.pushEvent(new ResetGoalsEvent(game.getFrame() + 1));
	}

	@Override
	public void onExit() {
		serverConnection.forEachClient(e -> e.connection.close());
	}

}
