package GBall;

import java.awt.Color;
import java.awt.event.KeyEvent;

import GBall.engine.Ball;
import GBall.engine.Const;
import GBall.engine.Entity;
import GBall.engine.GameWindow;
import GBall.engine.Ship;
import GBall.engine.Time;
import GBall.engine.Vector2;
import GBall.engine.Vector2.Direction;
import GBall.engine.World;
import GBall.engine.GameWindow.GameWindowListener;
import GBall.engine.World.WorldListener;

import static GBall.engine.Util.*;

public class Game implements WorldListener, GameWindowListener {

	private final GameWindow gw;

	private final World world;

	private final Ship s1, s2, s3, s4;
	private final Ball b;

	private boolean running = true;

	private int scoreRed = 0, scoreGreen = 0;

	public Game() {
		gw = new GameWindow(this);

		s1 = new Ship(0L, Color.RED);
		s2 = new Ship(1L, Color.RED);
		s3 = new Ship(2L, Color.GREEN);
		s4 = new Ship(3L, Color.GREEN);
		b = new Ball(4L);

		world = new World(this);
		world.addEntity(s1, s2, s3, s4, b);
	}

	public void run() {

		Controller c = new Controller(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, s1);
		gw.addKeyListener(c);

		reset();

		while (running) {
			world.update(Time.getTime());
			gw.repaint();

			sleep(1.0 / Const.TARGET_FPS);
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

		if (Const.SHOW_FPS) {
			gw.setColor(Const.FPS_TEXT_COLOR);
			gw.drawString(Integer.toString((int) world.fps()), Const.FPS_TEXT_POSITION);
		}
	}

}
