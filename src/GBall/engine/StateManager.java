package GBall.engine;

import GBall.GameState;
import GBall.engine.event.Event;
import GBall.engine.event.StateEvent;

public class StateManager {

	public interface StateListener {

		public void onTimewarp(Snapshot snapshot, long offset);

		public void onEvent(Snapshot snapshot);

	}

	public class Snapshot {

		public GameState state;
		public final Event event;

		private Snapshot next = null;
		private Snapshot previous = null;

		private Snapshot(GameState state, Event event) {
			this.state = state;
			this.event = event;
		}

	}

	private final StateListener listener;

	private Snapshot current;
	private Snapshot first;
	private Snapshot last;

	private long frame = 0;

	public StateManager(StateListener listener) {
		this.listener = listener;
	}

	public void step(long frame) {
		while (current != null && current.event.framestamp <= frame) {
			listener.onEvent(current);
			current = current.next;
		}
		this.frame = frame;
	}

	private void decouple(Snapshot s) {
		if (s != null) {
			if (s.previous != null)
				s.previous.next = (s.next != null ? s.next : null);
			if (s.next != null)
				s.next.previous = (s.previous != null ? s.previous : null);
			s.previous = s.next = null;
		}
	}

	private void couple(Snapshot s1, Snapshot s2) {
		if (s1 != null)
			s1.next = s2;
		if (s2 != null)
			s2.previous = s1;
	}

	private void couple(Snapshot s1, Snapshot s2, Snapshot s3) {
		couple(s1, s2);
		couple(s2, s3);
	}

	private Snapshot backToState(Snapshot s) {
		while (s != null && s.state == null)
			s = s.previous;
		return s;
	}

	private Snapshot back(Snapshot s) {
		long frame = s.event.framestamp;

		while (s.previous != null && s.previous.event.framestamp >= frame)
			s = s.previous;
		return s;
	}

	private Snapshot forward(Snapshot s) {
		long frame = s.event.framestamp;

		while (s.next != null && s.next.event.framestamp <= frame)
			s = s.next;
		return s;
	}

	// rightmost snapshot with a smaller or equal previous framestamp
	private Snapshot find(long frame) {
		Snapshot s = last;

		while (s.previous != null && s.previous.event.framestamp > frame)
			s = s.previous;
		return s;
	}

	public void clean() {
		while (first != null && first != last && (frame - first.event.framestamp) > Const.OUTDATED_THRESHOLD) {
			Snapshot temp = first.next;
			decouple(first);
			first = temp;
		}
	}

	public void add(Event event, GameState state) {
		Snapshot s = new Snapshot(state, event);

		if (first == null) {
			first = last = current = s;
			return;
		}

		Snapshot fs = find(event.framestamp);
		if (fs == null)
			System.out.println("shit fucked up");
		else if (fs.equals(last)) {
			couple(fs, last = s);
			if (current == null)
				current = s;
		} else
			couple(fs, s, fs.next);

		if (s.event.framestamp <= frame || s.event instanceof StateEvent) {
			if (s.event instanceof StateEvent) {
				decouple(s);
				fs = back(fs);

				if (fs.previous == null)
					couple(last = s, fs);
				else
					couple(fs, s, fs.next);

				listener.onTimewarp(current = fs, 0);
			} else
				listener.onTimewarp(current = back(backToState(s)), frame - s.event.framestamp);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		Snapshot s = first;
		while (s != null) {
			if (s.equals(current))
				sb.append('>');
			sb.append(s.event.toString());
			if (s.state == null)
				sb.append(" - null state");
			sb.append('\n');
			s = s.next;
		}

		return sb.toString();
	}

}
