package GBall;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import GBall.Controller.ControllerListener;
import GBall.engine.Const;
import GBall.engine.GameWindow;
import GBall.engine.Time;
import GBall.engine.Vector2.Direction;
import GBall.engine.event.ControllerEvent;
import GBall.network.Location;
import GBall.network.Packet;
import GBall.network.Socket;
import GBall.network.Socket.SocketListener;

import static GBall.engine.Util.*;

public class Client implements SocketListener, ControllerListener {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Client c = new Client();
		c.run();
	}

	private final Socket socket;

	private final Game game;
	private final GameWindow gw;

	public Client() throws UnknownHostException, IOException {
		socket = new Socket(new Location("localhost", 25000));
		game = new Game();
		gw = new GameWindow(game);
	}

	public void run() {
		socket.open(this);

		Controller c = new Controller(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, this);
		gw.addKeyListener(c);

		game.reset();

		while (true) {
			game.tick();
			gw.repaint();
			sleep(1.0 / Const.TARGET_FPS);
		}
	}

	@Override
	public void onReceive(Location source, Packet packet) {
		game.setState((GameState) packet.getObject());
	}

	private void sendEvent(Direction d, boolean press) {
		socket.send(new Packet(new ControllerEvent(0L, 0L, d, press)));
	}

	@Override
	public void onPress(Direction d) {
		sendEvent(d, true);
	}

	@Override
	public void onRelease(Direction d) {
		sendEvent(d, false);
	}

}
