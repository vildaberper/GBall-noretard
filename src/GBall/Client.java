package GBall;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import GBall.Controller.ControllerListener;
import GBall.Game.GameListener;
import GBall.engine.Const;
import GBall.engine.GameWindow;
import GBall.engine.Time;
import GBall.engine.Vector2.Direction;
import GBall.engine.event.ControllerEvent;
import GBall.engine.event.Event;
import GBall.engine.event.OffsetEvent;
import GBall.network.ClientConnection;
import GBall.network.ClientConnection.ClientConnectionListener;
import GBall.network.Location;

import static GBall.engine.Util.*;

public class Client implements ClientConnectionListener, ControllerListener, GameListener {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Client c = new Client();
		c.run();
	}

	private ClientConnection clientConnection;

	private final Location server = new Location("tfbs.no-ip.org", 25565);
	// private final Location server = new Location("localhost", 25565);

	private long id = -1;

	private boolean doneHandshake = false;

	// Incoming events before handshake is done (order is not guaranteed)
	private Queue<Event> queuedEvents = new LinkedList<Event>();

	private long startTime = -1;

	private final Game game;
	private final GameWindow gw;

	public Client() throws UnknownHostException {
		game = new Game(this);
		gw = new GameWindow(game);
	}

	public void run() throws SocketException {
		clientConnection = new ClientConnection(server, this);

		Controller c = new Controller(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, this);

		game.reset();
		gw.addKeyListener(c);

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

			}

			@Override
			public void keyTyped(KeyEvent e) {

			}

		});

		while (true) {
			if (!doneHandshake) {
				gw.repaint();
				sleep(Const.FRAME_INCREMENT);
				continue;
			}

			long timeToSleep;
			synchronized (game) {
				game.tick();
				gw.repaint();

				timeToSleep = startTime + game.getFrame() * Const.FRAME_INCREMENT - Time.getTime();
			}

			if (timeToSleep > 100)
				System.out.println("sleeping for " + timeToSleep + "ms");

			if (timeToSleep > 0)
				sleep(timeToSleep);
		}
	}

	@Override
	public void onReceive(Object o) {
		if (o instanceof Handshake) {
			Handshake h = (Handshake) o;

			startTime = h.startTime;
			id = h.id;
			Time.setOffset(startTime + h.stateEvent.frame * Const.FRAME_INCREMENT - Time.getTime());
			synchronized (game) {
				game.setFrame(h.stateEvent.frame - 1);
			}
			game.pushEvent(h.stateEvent);

			System.out.println("id=" + id);
			System.out.println("startTime=" + startTime);
			System.out.println("initialOffset=" + Time.getOffset());

			Event e;
			while ((e = queuedEvents.poll()) != null)
				game.pushEvent(e);

			doneHandshake = true;
		} else if (o instanceof Event) {
			if (o instanceof OffsetEvent) {
				long deltaOffset = ((OffsetEvent) o).offset - Const.LOCAL_DELAY;
				Time.setOffset(Time.getOffset() + minmax(deltaOffset / 2L, 50L));
			} else {
				Event e = (Event) o;

				if (!doneHandshake) {
					queuedEvents.add(e);
					return;
				}

				long diff = startTime + (e.frame - Const.LOCAL_DELAY) * Const.FRAME_INCREMENT - Time.getTime();
				if (diff > 0)
					Time.setOffset(Time.getOffset() + minmax(diff, 5L));

				game.pushEvent(e);
			}
		}
	}

	private void localEvent(Direction d, boolean press) {
		if (!doneHandshake || id == -1)
			return;

		ControllerEvent event;
		synchronized (game) {
			event = new ControllerEvent(game.getFrame() + Const.LOCAL_DELAY, id, d, press);
		}
		clientConnection.send(event);
		game.pushEvent(event);
	}

	@Override
	public void onDirection(Direction d, boolean press) {
		localEvent(d, press);
	}

	@Override
	public void onGoal(boolean red) {
		// DO NOTHING GOALS ARE HANDLED BY SERVER
	}

	@Override
	public void onTimewarp(long offset, long entityId) {
		Time.setOffset(Time.getOffset() - minmax((offset - Const.LOCAL_DELAY / 2L) / 2L, 10L));
	}

	@Override
	public void onInvalidInput() {
		System.out.println("!!! invalid input !!!");
	}

	@Override
	public void onDisconnect() {
		System.out.println("Disconnected");
	}

	@Override
	public void onExit() {
		clientConnection.close();
	}

}
