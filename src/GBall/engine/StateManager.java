package GBall.engine;

import java.util.LinkedList;

import GBall.GameState;
import GBall.engine.event.Event;
import GBall.engine.event.GoalEvent;
import GBall.engine.event.StateEvent;

import static GBall.engine.Const.*;

public class StateManager {

	public interface StateListener {

		public void onTimewarp(GameState state, Event source);

		public void onFrame(Frame frame);

	}

	public class Frame {

		public final long frame;
		public GameState state;
		public final LinkedList<Event> events;

		private Frame(long frame, GameState state) {
			this.frame = frame;
			this.state = state;
			events = new LinkedList<Event>();
		}

		private Frame(long frame) {
			this(frame, null);
		}

		private void push(Event event) {
			// Make sure StateEvent and GoalEvent are always last
			if (event instanceof StateEvent || event instanceof GoalEvent)
				events.add(event);
			else
				events.add(0, event);
		}

		@Override
		public String toString() {
			String s = "";
			for (Event event : events)
				s += "," + event;
			return "F:" + frame + " E:" + (s.length() == 0 ? "" : s.substring(1)) + " S:" + (state != null);
		}

	}

	private final LinkedList<Frame> left, right;

	private final StateListener listener;

	private long frame = 0;

	public StateManager(StateListener listener) {
		left = new LinkedList<Frame>();
		right = new LinkedList<Frame>();
		this.listener = listener;
	}

	public void step(long frame) {
		Frame f;
		if ((f = right.peek()) != null && f.frame == frame) {
			left.add(right.poll());
			listener.onFrame(f);
		}
		this.frame = frame;
	}

	public void clean() {
		while (left.size() > 1 && left.peek().frame < frame - OUTDATED_THRESHOLD)
			left.poll();
	}

	private Pair<Integer, Frame> find(long frame, LinkedList<Frame> fs) {
		for (int i = fs.size() - 1; i >= 0; --i) {
			Frame f = fs.get(i);

			if (f.frame == frame)
				return new Pair<Integer, Frame>(i, f);
			else if (f.frame < frame)
				return new Pair<Integer, Frame>(i + 1, null);
		}
		return new Pair<Integer, Frame>(0, null);
	}

	private void timewarp(int index, Event source) {
		int numFrames = left.size() - index;
		while (numFrames-- > 0)
			right.add(0, left.removeLast());

		listener.onTimewarp(right.peek().state, source);
	}

	public void push(Event event) {
		Frame f;
		Pair<Integer, Frame> p;

		if (event.frame > frame) {
			p = find(event.frame, right);
			if (p.getRight() == null)
				right.add(p.getLeft(), f = new Frame(event.frame));
			else
				f = p.getRight();

			f.push(event);
		} else {
			p = find(event.frame, left);
			int index = p.getLeft();

			if (p.getRight() == null) {
				if (p.getLeft() == 0) {
					if (left.size() == 0 && event instanceof StateEvent) {
						left.add(new Frame(event.frame, ((StateEvent) event).state));
					} else
						System.out.println("!!! shit fucked up !!!");
					return;
				} else {
					left.add(p.getLeft(), f = new Frame(event.frame));
					--index;
				}
			} else
				f = p.getRight();

			f.push(event);
			timewarp(index, event);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("LEFT\n");
		for (Frame f : left)
			sb.append("  " + f + "\n");

		sb.append("RIGHT\n");
		for (Frame f : right)
			sb.append("  " + f + "\n");

		return sb.toString();
	}

}
