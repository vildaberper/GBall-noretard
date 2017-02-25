package GBall;

import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;

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
import GBall.engine.event.AddEntityEvent;
import GBall.engine.event.ControllerEvent;
import GBall.engine.event.Event;
import GBall.engine.event.GoalEvent;
import GBall.engine.event.NothingEvent;

public class Game implements WorldListener, GameWindowListener, StateListener {

	public interface GameListener {
		void onGoal(boolean red);

		void onTimewarp(long offset, long entityId);
	}

	private final World world;
	private final StateManager stateManager;

	public Ship s1, s2, s3, s4;
	private Ball b;

	private long frame = 0;

	private int scoreRed = 0, scoreGreen = 0;
	private final GameListener listener;

	private HashSet<Snapshot> queuedEvents = new HashSet<Snapshot>();

	public Game(GameListener listener) {
		this.listener = listener;

		world = new World(this);
		stateManager = new StateManager(this);

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
		world.setState(state.worldState.clone());
		scoreRed = state.scoreRed;
		scoreGreen = state.scoreGreen;
		frame = state.frame;

		b = (Ball) world.getEntity(0L);

		Ship tmp;
		s1 = (tmp = getShip(1L)) == null ? new Ship(1L, Color.RED) : tmp;
		s2 = (tmp = getShip(2L)) == null ? new Ship(2L, Color.RED) : tmp;
		s3 = (tmp = getShip(3L)) == null ? new Ship(3L, Color.GREEN) : tmp;
		s4 = (tmp = getShip(4L)) == null ? new Ship(4L, Color.GREEN) : tmp;
	}

	public long getFrame() {
		return frame;
	}

	public void saveState() {
		stateManager.add(new NothingEvent(frame), getState());
	}

	public Ship nextShip() {
		switch (world.size()) {
		case 1:
			return s1;
		case 2:
			return s2;
		case 3:
			return s3;
		case 4:
			return s4;
		}
		return null;
	}

	public void addEntity(Entity... e) {
		world.addEntity(e);
	}

	public Ship getShip(long id) {
		Entity e = world.getEntity(id);

		if (e != null && e instanceof Ship)
			return (Ship) e;

		return null;
	}

	private void update() {
		++frame;
		stateManager.step(frame);

		int queuedSize;
		while ((queuedSize = queuedEvents.size()) > 0) {
			HashSet<Snapshot> tmp = queuedEvents;
			queuedEvents = new HashSet<Snapshot>();

			for (Snapshot s : tmp)
				onEvent(s);

			if (queuedSize <= queuedEvents.size()) {
				System.out.println("!!! invalid inputs !!!");
				break;
			}
		}

		world.update(frame);
	}

	public void tick() {
		update();
		stateManager.clean();
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
		stateManager.add(event, null);
	}

	@Override
	public void onWallCollide(Entity e, Direction d, double dist) {
		e.velocity.invertInDirection(d);

		switch (d) {
		case LEFT: {
			if (e instanceof Ball)
				listener.onGoal(false);
			else
				e.position.x += e.radius() - dist;
			break;
		}
		case RIGHT: {
			if (e instanceof Ball)
				listener.onGoal(true);
			else
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
		gw.setColor(Color.WHITE);
		gw.setFont_(new Font("Arial", Font.BOLD, 12));
		String[] ss = stateManager.toString().split("\n");
		Vector2 p = new Vector2(10, 50);

		for (String s : ss) {
			gw.drawString(s, p);
			p.y += 13;
		}

		gw.drawString(Long.toString(getFrame()), new Vector2(500, 50));

	}

	@Override
	public void onTimewarp(Snapshot snapshot) {
		long currentFrame = frame;

		listener.onTimewarp((frame - snapshot.state.frame) * Const.FRAME_INCREMENT, snapshot.event.getEntityId());

		System.out.println("timewarp to frame " + snapshot.state.frame + " from " + frame);
		System.out.println("  ->" + snapshot.event.toString());

		setState(snapshot.state);
		--frame;
		while (frame <= currentFrame)
			update();
	}

	@Override
	public void onEvent(Snapshot snapshot) {
		snapshot.state = getState();

		System.out.println("  event(" + getFrame() + "):" + snapshot.event.toString());

		if (snapshot.event instanceof ControllerEvent) {
			ControllerEvent ce = (ControllerEvent) snapshot.event;
			Ship s = getShip(ce.entityId);

			if (s == null) {
				System.out.println("!!! ship fucked up !!!");
				return;
			}

			if (!(ce.press ^ s.isPressed(ce.direction)))
				queuedEvents.add(snapshot);
			else {
				if (ce.press)
					getShip(ce.entityId).onPress(ce.direction);
				else
					getShip(ce.entityId).onRelease(ce.direction);
			}
		} else if (snapshot.event instanceof GoalEvent) {
			GoalEvent ge = (GoalEvent) snapshot.event;

			if (ge.red)
				++scoreRed;
			else
				++scoreGreen;

			reset();
		} else if (snapshot.event instanceof AddEntityEvent) {
			AddEntityEvent aee = (AddEntityEvent) snapshot.event;

			world.addEntity(aee.entity.clone());
		}
	}

}
