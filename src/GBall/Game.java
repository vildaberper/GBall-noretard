package GBall;

import java.awt.Color;

import GBall.engine.Ball;
import GBall.engine.Const;
import GBall.engine.Entity;
import GBall.engine.GameWindow;
import GBall.engine.Ship;
import GBall.engine.StateManager;
import GBall.engine.StateManager.Snapshot;
import GBall.engine.StateManager.StateListener;
import GBall.engine.Vector2;
import GBall.engine.Vector2.Direction;
import GBall.engine.World;
import GBall.engine.GameWindow.GameWindowListener;
import GBall.engine.World.WorldListener;
import GBall.engine.event.ControllerEvent;
import GBall.engine.event.Event;
import GBall.engine.event.GoalEvent;

import static GBall.engine.Util.*;


public class Game implements WorldListener, GameWindowListener, StateListener {
	
	public interface GameListener{
		void onGoal(boolean red);
	}
	private final World world;
	private final StateManager stateManager;

	public Ship s1, s2, s3, s4;
	private Ball b;

	private long frame = 0;
	
	private int scoreRed = 0, scoreGreen = 0;
	private final GameListener listener;
	public Game(GameListener listener) {
		world = new World(this);
		stateManager = new StateManager(this);
		this.listener = listener;

		b = new Ball(0L);
		s1 = new Ship(1L, Color.RED);
		s2 = new Ship(2L, Color.RED);
		s3 = new Ship(3L, Color.GREEN);
		s4 = new Ship(4L, Color.GREEN);

		world.addEntity(b);
	}

	public GameState getState() {
		return new GameState(world.getState().clone(), scoreRed, scoreGreen, frame);
	}

	public void setState(GameState state) {
		world.setState(state.worldState);
		scoreRed = state.scoreRed;
		scoreGreen = state.scoreGreen;
		frame = state.frame;
		
		b = (Ball) world.getEntity(0L);
		
		s1 = (getShip(1L) != null) ? getShip(1L) : new Ship(1L, Color.RED);
		s2 = (getShip(2L) != null) ? getShip(2L) : new Ship(2L, Color.RED);
		s3 = (getShip(3L) != null) ? getShip(3L) : new Ship(3L, Color.GREEN);
		s4 = (getShip(4L) != null) ? getShip(4L) : new Ship(4L, Color.GREEN);
		
	}

	public long getFrame() {
		return frame;
	}

	public long addShip() {
		switch (world.size()) {
		case 1:
			world.addEntity(s1);
			return s1.id;
		case 2:
			world.addEntity(s2);
			return s2.id;
		case 3:
			world.addEntity(s3);
			return s3.id;
		case 4:
			world.addEntity(s4);
			return s4.id;
		}
		return -1;
	}

	public Ship getShip(long id) {
		Entity e = world.getEntity(id);

		if (e != null && e instanceof Ship)
			return (Ship) e;

		return null;
	}

	public void tick() {
		stateManager.step(frame);
		stateManager.clean();
		world.update(frame);
		++frame;
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

	public void pushEvent(Event event) {
		stateManager.add(event, getState());
	}

	@Override
	public void onWallCollide(Entity e, Direction d, double dist) {
		e.velocity.invertInDirection(d);

		switch (d) {
		case LEFT: {
			e.position.x += e.radius() - dist;
			break;
		}
		case RIGHT: {
			e.position.x -= e.radius() - dist;
			break;
		}
		case UP: {
			e.position.y += e.radius() - dist;
			break;
		}
		case DOWN: {
			e.position.y -= e.radius() - dist;
			break;
		}
		}

		if (e instanceof Ball) {
			if (d.equals(Direction.LEFT)) {
				listener.onGoal(false);
			} else if (d.equals(Direction.RIGHT)) {
				listener.onGoal(true);
			}
		}
	}

	@Override
	public void onEntityCollide(Entity e1, Entity e2) {
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
			// gw.drawString(Integer.toString((int) world.fps()),
			// Const.FPS_TEXT_POSITION);
		}
	}

	@Override
	public void onTimewarp(Snapshot snapshot) {
		long currentFrame = snapshot.event.framestamp;

		System.out.println("timewarp " + (frame - currentFrame));

		setState(snapshot.state);
		while (currentFrame < frame) {
			stateManager.step(currentFrame);
			world.update(currentFrame);
			++currentFrame;
		}
	}

	@Override
	public void onEvent(Snapshot snapshot) {
		snapshot.state = getState();
		
		if (snapshot.event instanceof ControllerEvent) {
			ControllerEvent ce = (ControllerEvent) snapshot.event;

			if (ce.entityId != -1) {
				if (ce.press) {
					System.out.println("press");
					getShip(ce.entityId).onPress(ce.direction);
				} else {
					System.out.println("release");
					getShip(ce.entityId).onRelease(ce.direction);
				}
			}
		}
		
		else if(snapshot.event instanceof GoalEvent){
			GoalEvent ge = (GoalEvent) snapshot.event;
			if(ge.red)
				++scoreRed;
			else
				++scoreGreen;
			
			reset();
			System.out.println("Reset, GOAL");
		}

		
	}

}
