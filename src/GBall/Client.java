package GBall;

import java.awt.event.KeyEvent;
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
import GBall.network.Location;
import GBall.network.Packet;
import GBall.network.Socket;
import GBall.network.Socket.SocketListener;

import static GBall.engine.Util.*;

public class Client implements SocketListener, ControllerListener, GameListener {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Client c = new Client();
		c.run();
	}

	private final Socket socket;

	private final Location server = new Location("vildaberper.no-ip.org", 25565);
	//private final Location server = new Location("localhost", 25565);

	private long id = -1;

	private long startTime = -1;

	private final Game game;
	private final GameWindow gw;

	public Client() throws UnknownHostException, IOException {
		socket = new Socket();
		game = new Game(this);
		gw = new GameWindow(game);
	}

	public void run() {
		socket.open(this);

		Controller c = new Controller(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, this);
		gw.addKeyListener(c);

		game.reset();

		while (true) {
			if (startTime == -1) {
				gw.repaint();
				sleep(Const.FRAME_INCREMENT);
				continue;
			}

			game.tick();
			gw.repaint();

			long timeToSleep = (startTime + game.getFrame() * Const.FRAME_INCREMENT) - Time.getTime();

			if (timeToSleep > 0)
				sleep(timeToSleep);
		}
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		Object obj = packet.getObject();

		if (obj instanceof Long) {
			System.out.println("got id");
			id = (Long) obj;
		} else if (obj instanceof Event) {
			System.out.println("got event");
			game.pushEvent((Event) obj);
		} else if (obj instanceof GameState) {
			System.out.println("got state");
			game.setState((GameState) obj);
			startTime = Time.getTime() - game.getFrame() * Const.FRAME_INCREMENT;
		}
	}

	private void localEvent(Direction d, boolean press) {
		ControllerEvent event = new ControllerEvent(game.getFrame() + Const.LOCAL_DELAY, id, d, press);

		game.pushEvent(event);
		socket.send(server, new Packet(event));
	}

	@Override
	public void onPress(Direction d) {
		localEvent(d, true);
	}

	@Override
	public void onRelease(Direction d) {
		localEvent(d, false);
	}

	@Override
	public void onGoal(boolean red) {
		//DO NOTHING GOALS ARE HANDLED BY SERVER
		
	}

}
