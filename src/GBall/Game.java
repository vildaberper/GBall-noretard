package GBall;

import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import GBall.engine.Ball;
import GBall.engine.Const;
import GBall.engine.Entity;
import GBall.engine.GameWindow;
import GBall.engine.Ship;
import GBall.engine.StateManager;
import GBall.engine.Time;
import GBall.engine.StateManager.Frame;
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
import GBall.engine.event.RemoveEntityEvent;
import GBall.engine.event.ResetGoalsEvent;
import GBall.engine.event.StateEvent;

public class Game implements WorldListener, GameWindowListener, StateListener {

	public interface GameListener {
		void onGoal(boolean red);

		void onTimewarp(long offset, long entityId);

		void onInvalidInput();

		void onExit();

	}

	public boolean debug = false;

	private final World world;
	private final StateManager stateManager;

	private long frame = 0;

	private int scoreRed = 0, scoreGreen = 0;
	private final GameListener listener;

	private HashSet<Event> eventQueueFrame = new HashSet<Event>();

	private Queue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();

	public Game(GameListener listener) {
		this.listener = listener;

		world = new World(this);
		stateManager = new StateManager(this);

		world.addEntity(new Ball(0L));
	}

	public GameState getState() {
		return new GameState(world.getState().clone(), scoreRed, scoreGreen, frame);
	}

	public void setState(GameState state) {
		world.setState(state.worldState.clone());
		scoreRed = state.scoreRed;
		scoreGreen = state.scoreGreen;
		frame = state.frame;
	}

	public long getFrame() {
		return frame;
	}

	public void setFrame(long frame) {
		this.frame = frame;
	}

	public void saveState() {
		pushEvent(new StateEvent(getState()));
	}

	public Ship nextShip() {
		if (world.getEntity(1L) == null) {
			Ship s1 = new Ship(1L, Color.RED);
			s1 = new Ship(1L, Color.RED);
			s1.direction.set(1, 0);
			s1.position.x = Const.START_TEAM1_SHIP1_X;
			s1.position.y = Const.START_TEAM1_SHIP1_Y;
			s1.velocity.zero();
			s1.acceleration = 0.0;
			world.addEntity(s1);
			return s1;
		} else if (world.getEntity(3L) == null) {
			Ship s3 = new Ship(3L, Color.GREEN);
			s3 = new Ship(3L, Color.GREEN);
			s3.direction.set(-1, 0);
			s3.position.x = Const.START_TEAM2_SHIP1_X;
			s3.position.y = Const.START_TEAM2_SHIP1_Y;
			s3.velocity.zero();
			s3.acceleration = 0.0;
			world.addEntity(s3);
			return s3;
		} else if (world.getEntity(2L) == null) {
			Ship s2 = new Ship(2L, Color.RED);
			s2 = new Ship(2L, Color.RED);
			s2.direction.set(1, 0);
			s2.position.x = Const.START_TEAM1_SHIP2_X;
			s2.position.y = Const.START_TEAM1_SHIP2_Y;
			s2.velocity.zero();
			s2.acceleration = 0.0;
			world.addEntity(s2);
			return s2;
		} else if (world.getEntity(4L) == null) {
			Ship s4 = new Ship(4L, Color.GREEN);
			s4 = new Ship(4L, Color.GREEN);
			s4.direction.set(-1, 0);
			s4.position.x = Const.START_TEAM2_SHIP2_X;
			s4.position.y = Const.START_TEAM2_SHIP2_Y;
			s4.velocity.zero();
			s4.acceleration = 0.0;
			world.addEntity(s4);
			return s4;
		}
		return null;
	}

	public void addEntity(Entity... e) {
		world.addEntity(e);
	}

	public void removeEntity(Long... id) {
		world.removeEntity(id);
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
		world.update(frame);
	}

	public void tick() {
		{
			Event event = null;
			while ((event = eventQueue.poll()) != null)
				stateManager.push(event);
		}
		update();
		stateManager.clean();
	}

	public void reset() {
		Entity tmp;

		if ((tmp = world.getEntity(1L)) != null) {
			Ship s1 = (Ship) tmp;
			s1.direction.set(1, 0);
			s1.position.x = Const.START_TEAM1_SHIP1_X;
			s1.position.y = Const.START_TEAM1_SHIP1_Y;
			s1.velocity.zero();
			s1.acceleration = 0.0;
		}

		if ((tmp = world.getEntity(2L)) != null) {
			Ship s2 = (Ship) tmp;
			s2.direction.set(1, 0);
			s2.position.x = Const.START_TEAM1_SHIP2_X;
			s2.position.y = Const.START_TEAM1_SHIP2_Y;
			s2.velocity.zero();
			s2.acceleration = 0.0;
		}

		if ((tmp = world.getEntity(3L)) != null) {
			Ship s3 = (Ship) tmp;
			s3.direction.set(-1, 0);
			s3.position.x = Const.START_TEAM2_SHIP1_X;
			s3.position.y = Const.START_TEAM2_SHIP1_Y;
			s3.velocity.zero();
			s3.acceleration = 0.0;
		}

		if ((tmp = world.getEntity(4L)) != null) {
			Ship s4 = (Ship) tmp;
			s4.direction.set(-1, 0);
			s4.position.x = Const.START_TEAM2_SHIP2_X;
			s4.position.y = Const.START_TEAM2_SHIP2_Y;
			s4.velocity.zero();
			s4.acceleration = 0.0;
		}

		Ball b = (Ball) world.getEntity(0);
		b.position.x = Const.BALL_X;
		b.position.y = Const.BALL_Y;
		b.velocity.zero();
		b.acceleration = 0.0;
	}

	public void pushEvent(Event event) {
		eventQueue.add(event);
	}

	@Override
	public void onWallCollide(Entity e, Direction d, double dist) {
		// if (!(e instanceof Ball))
		// Would fire several goal events in the event of exception etc.
		e.velocity.invertInDirection(d);

		switch (d) {
		case LEFT: {
			if (e instanceof Ball)
				listener.onGoal(false);
			e.position.x += e.radius() - dist;
			break;
		}
		case RIGHT: {
			if (e instanceof Ball)
				listener.onGoal(true);
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
		gw.setColor(Const.TEAM1_COLOR);
		gw.fillRect(Const.TEAM1_GOAL_POSITION, 0, Const.GOAL_WIDTH, Const.DISPLAY_HEIGHT);
		gw.setColor(Const.TEAM2_COLOR);
		gw.fillRect(Const.TEAM2_GOAL_POSITION, 0, Const.GOAL_WIDTH, Const.DISPLAY_HEIGHT);
		world.render(gw);

		gw.setFont_(Const.SCORE_FONT);
		gw.setColor(Const.TEAM1_COLOR);
		gw.drawString(Integer.toString(scoreRed), Const.TEAM1_SCORE_TEXT_POSITION);
		gw.setColor(Const.TEAM2_COLOR);
		gw.drawString(Integer.toString(scoreGreen), Const.TEAM2_SCORE_TEXT_POSITION);

		if (debug) {
			gw.setColor(Color.WHITE);
			gw.setFont_(new Font("Arial", Font.BOLD, 12));
			String[] ss = stateManager.toString().split("\n");
			Vector2 p = new Vector2(10, 50);

			for (String s : ss) {
				gw.drawString(s, p);
				p.y += 13;
			}
			gw.drawString(Long.toString(getFrame()), new Vector2(500, 50));
			gw.drawString(Long.toString(Time.getOffset()), new Vector2(500, 70));
		}

	}

	@Override
	public void onTimewarp(GameState state, Event source) {
		listener.onTimewarp((frame - source.frame) * Const.FRAME_INCREMENT, source.getEntityId());

		System.out.println("timewarp to frame " + state.frame + " from " + frame);
		System.out.println("source " + source.toString());

		long currentFrame = getFrame();
		setState(state);
		--frame;
		while (frame < currentFrame)
			update();

	}

	@Override
	public void onFrame(Frame frame) {
		frame.state = getState();
		frame.events.forEach(e -> onEvent(e));

		int queuedSize;
		while ((queuedSize = eventQueueFrame.size()) > 0) {
			HashSet<Event> tmp = eventQueueFrame;
			eventQueueFrame = new HashSet<Event>();

			tmp.forEach(e -> onEvent(e));

			if (queuedSize <= eventQueueFrame.size()) {
				eventQueueFrame.clear();
				listener.onInvalidInput();
				break;
			}
		}
	}

	public void onEvent(Event event) {
		System.out.println("  event(" + getFrame() + "):" + event.toString());

		if (event instanceof ControllerEvent) {
			ControllerEvent ce = (ControllerEvent) event;
			Ship s = getShip(ce.entityId);

			if (s == null) {
				System.out.println("!!! ship fucked up !!!");
				return;
			}

			if (!(ce.press ^ s.isPressed(ce.direction)))
				eventQueueFrame.add(event);
			else
				s.onDirection(ce.direction, ce.press);
		} else if (event instanceof GoalEvent) {
			if (eventQueueFrame.size() > 0) {
				eventQueueFrame.add(event);
				return;
			}

			GoalEvent ge = (GoalEvent) event;

			if (ge.red)
				++scoreRed;
			else
				++scoreGreen;

			reset();
		} else if (event instanceof AddEntityEvent) {
			AddEntityEvent aee = (AddEntityEvent) event;

			world.addEntity(aee.entity.clone());
		} else if (event instanceof RemoveEntityEvent) {
			RemoveEntityEvent ree = (RemoveEntityEvent) event;

			world.removeEntity(ree.entityId);
		} else if (event instanceof ResetGoalsEvent) {
			scoreRed = scoreGreen = 0;
		} else if (event instanceof StateEvent) {
			if(bajs){
				if (eventQueueFrame.size() > 0) {
					eventQueueFrame.add(event);
					return;
				}
	
				StateEvent se = (StateEvent) event;
	
				setState(se.state);
				bajs = false;
			}
		}
	}
	private boolean bajs = true;

	@Override
	public void onClose() {
		listener.onExit();
	}

}
