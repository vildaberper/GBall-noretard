package GBall;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import GBall.engine.Const;
import GBall.engine.GameWindow;
import GBall.engine.Time;
import GBall.network.Location;
import GBall.network.Packet;
import GBall.network.Socket;
import GBall.network.Socket.SocketListener;

import static GBall.engine.Util.*;

public class Client implements SocketListener {

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

		Controller c = new Controller(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, game.s1);
		gw.addKeyListener(c);

		game.reset();

		long derp = 0;
		while (true) {
			long herp = Time.getTime();
			game.tick();
			gw.repaint();
			sleep(1.0 / Const.TARGET_FPS);
			if (herp - derp > 10) {
				socket.send(new Packet(game.getState()));
				derp = herp;
			}
		}
	}

	@Override
	public void onReceive(Location source, Packet packet) {

	}

}
