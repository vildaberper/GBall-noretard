package GBall;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.UnknownHostException;

import GBall.Controller.ControllerListener;
import GBall.Game.GameListener;
import GBall.engine.Const;
import GBall.engine.GameWindow;
import GBall.engine.Time;
import GBall.engine.Vector2.Direction;
import GBall.engine.event.ControllerEvent;
import GBall.engine.event.Event;
import GBall.engine.event.OffsetEvent;
import GBall.network.Ack;
import GBall.network.Connection;
import GBall.network.Connection.ConnectionListener;
import GBall.network.Handshake;
import GBall.network.Location;
import GBall.network.Packet;
import GBall.network.SocketListener;
import GBall.network.UDPSocket;

import static GBall.engine.Util.*;

public class Client implements SocketListener, ControllerListener, GameListener, ConnectionListener {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Client c = new Client();
		c.run();
	}
	
	private final Connection connection;

	private final Location server = new Location("193.11.163.224", 25565);
	//private final Location server = new Location("localhost", 25565);

	private long id = -1;

	private boolean initialOffset = false;

	private long startTime = -1;

	private final Game game;
	private final GameWindow gw;

	public Client() throws UnknownHostException, IOException {
		connection = new Connection(server, new UDPSocket(), this);
		game = new Game(this);
		gw = new GameWindow(game);
	}

	public void run() {
		Controller c = new Controller(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, this);

		game.reset();
		connection.open(this);
		gw.addKeyListener(c);

		gw.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_D)
					game.debug = !game.debug;
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
			if (!initialOffset) {
				gw.repaint();
				sleep(Const.FRAME_INCREMENT);
				continue;
			}

			synchronized (game) {
				game.tick();
				gw.repaint();
			}

			long timeToSleep = startTime + game.getFrame() * Const.FRAME_INCREMENT - Time.getTime();

			if (timeToSleep > 0)
				sleep(timeToSleep);
		}
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		if (packet.getObject() instanceof Ack) {
			Ack ack = (Ack) packet.getObject();
			connection.addAck(ack.id);
			return;
		}
		
		connection.sendAck(packet.id);
		
		if (connection.handled(packet.id))
			return;
		
		connection.addHandled(packet.id);
		
		Object obj = packet.getObject();

		if (obj instanceof Handshake) {
			Handshake handshake = (Handshake) obj;
			
			startTime = handshake.startTime;
			System.out.println("startTime=" + handshake.startTime);
			
			id = handshake.clientId;
			System.out.println("id=" + id);
			
			Time.setOffset(startTime + handshake.frame * Const.FRAME_INCREMENT - Time.getTime());
			System.out.println("initialOffset=" + Time.getOffset() + " - packet id: " + packet.id);
			initialOffset = true;
			
			Event e = handshake.stateEvent;

			handleEvent(e);
			
		} else if (obj instanceof Event) {
			if (obj instanceof OffsetEvent) {
				long deltaOffset = ((OffsetEvent) obj).offset - Const.LOCAL_DELAY;
				Time.setOffset(Time.getOffset() + minmax(deltaOffset / 2L, 50L));
			} else {
				Event e = (Event) obj;

				handleEvent(e);
			}
		}		
	}
	
	private void handleEvent(Event e) {
		long diff = startTime + (e.frame - Const.LOCAL_DELAY) * Const.FRAME_INCREMENT - Time.getTime();
		if (diff > 0)
			Time.setOffset(Time.getOffset() + minmax(diff, 5L));

		game.pushEvent(e);
	}

	private void localEvent(Direction d, boolean press) {
		ControllerEvent event;
		synchronized (game) {
			event = new ControllerEvent(game.getFrame() + Const.LOCAL_DELAY, id, d, press);
		}
		connection.send(new Packet(event));
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
	public void onConnect(Connection c) {
		// TODO Auto-generated method stub
		
	}

}
