package GBall;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import GBall.engine.Ball;
import GBall.engine.Const;
import GBall.engine.Entity;
import GBall.engine.GameWindow;
import GBall.engine.Ship;
import GBall.engine.Vector2;
import GBall.engine.Vector2.Direction;
import GBall.engine.World;
import GBall.engine.GameWindow.GameWindowListener;
import GBall.engine.World.WorldListener;

public class Game implements WorldListener, GameWindowListener {

	private final GameWindow gw;

	private final World world;

	private final Ship s1, s2, s3, s4;
	private final Ball b;

	private boolean running = true;

	private int scoreRed = 0, scoreGreen = 0;

	public Game() {
		gw = new GameWindow(this);

		s1 = new Ship(Color.RED);
		s2 = new Ship(Color.RED);
		s3 = new Ship(Color.GREEN);
		s4 = new Ship(Color.GREEN);
		b = new Ball();

		world = new World(this);
		world.addEntity(s1, s2, s3, s4, b);
	}

	private boolean left, right, up, down;

	public void run() {
		gw.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				switch (arg0.getKeyCode()) {
				case KeyEvent.VK_LEFT: {
					left = true;
					break;
				}
				case KeyEvent.VK_RIGHT: {
					right = true;
					break;
				}
				case KeyEvent.VK_UP: {
					up = true;
					break;
				}
				case KeyEvent.VK_DOWN: {
					down = true;
					break;
				}
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				switch (arg0.getKeyCode()) {
				case KeyEvent.VK_LEFT: {
					left = false;
					break;
				}
				case KeyEvent.VK_RIGHT: {
					right = false;
					break;
				}
				case KeyEvent.VK_UP: {
					up = false;
					break;
				}
				case KeyEvent.VK_DOWN: {
					down = false;
					break;
				}
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {

			}

		});

		reset();

		while (running) {
			s1.acceleration = up ? Const.SHIP_MAX_ACCELERATION : 0;
			s1.braking = down;
			s1.rotation = (left ? -1 : 0) + (right ? 1 : 0);

			world.update();
			gw.repaint();
		}
	}

	public void reset() {
		s1.direction.set(1, 0);
		s2.direction.set(1, 0);

		s3.direction.set(-1, 0);
		s4.direction.set(-1, 0);

		world.forEachEntity(e -> {
			e.velocity.zero();
			e.acceleration = 0.0;
		});

		s1.position.x = Const.START_TEAM1_SHIP1_X;
		s1.position.y = Const.START_TEAM1_SHIP1_Y;

		s2.position.x = Const.START_TEAM1_SHIP2_X;
		s2.position.y = Const.START_TEAM1_SHIP2_Y;

		s3.position.x = Const.START_TEAM2_SHIP1_X;
		s3.position.y = Const.START_TEAM2_SHIP1_Y;

		s4.position.x = Const.START_TEAM2_SHIP2_X;
		s4.position.y = Const.START_TEAM2_SHIP2_Y;

		b.position.x = Const.BALL_X;
		b.position.y = Const.BALL_Y;
	}

	@Override
	public void onWallCollide(Entity e, Direction d) {
		System.out.println("Wall collision");

		e.velocity.invertInDirection(d);

		if (e instanceof Ball) {
			if (d.equals(Direction.LEFT)) {
				++scoreGreen;
				reset();
			} else if (d.equals(Direction.RIGHT)) {
				++scoreRed;
				reset();
			}
		}
	}

	@Override
	public void onEntityCollide(Entity e1, Entity e2) {
		System.out.println("Entity collision");

		Vector2 v = e1.position.clone().add(e2.position.clone().invert());
		Vector2 vn = v.clone().normalize();
		double length = v.length();

		v.normalize().scale((e1.radius() + e2.radius() - length) / 2.0);
		e1.position.add(v);
		e2.position.add(v.invert());

		vn.scale(e1.velocity.dot(vn) - e2.velocity.dot(vn));
		e2.velocity.add(vn);
		e1.velocity.add(vn.invert());
	}

	@Override
	public void render(GameWindow gw) {
		world.render(gw);

		gw.setFont_(Const.SCORE_FONT);
		gw.setColor(Const.TEAM1_COLOR);
		gw.drawString(Integer.toString(scoreRed), Const.TEAM1_SCORE_TEXT_POSITION);
		gw.setColor(Const.TEAM2_COLOR);
		gw.drawString(Integer.toString(scoreGreen), Const.TEAM2_SCORE_TEXT_POSITION);
	}

}
