package GBall.engine;

import GBall.GameState;
import GBall.engine.event.Event;

public class StateManager {

	public interface StateListener {

		public void onTimewarp(Snapshot snapshot);

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

	public void clean() {
		while (first != null && first != last && (frame - first.event.framestamp) > Const.OUTDATED_THRESHOLD) {
			Snapshot temp = first.next;
			decouple(first);
			first = temp;
		}
	}

	public void add(Event event, GameState state) {
		if (event.framestamp <= frame) {
			System.out.println("i should timewarp");
		}

		Snapshot snapshot = new Snapshot(state, event);

		if (first == null) {
			first = last = current = snapshot;
			return;
		}

		if (last.event.framestamp < event.framestamp) {
			couple(last, snapshot);
			last = snapshot;
			if (event.framestamp <= frame)
				listener.onTimewarp(current = last.previous);
			else if (current == null)
				current = last;
			return;
		}

		Snapshot tmp = last;
		while (tmp.event.framestamp > snapshot.event.framestamp)
			if ((tmp = tmp.previous) == null) {
				System.out.println("shit fucked up");
				return;
			}

		couple(tmp, snapshot, tmp.next);

		if (snapshot.event.framestamp <= frame) {
			System.out.println("snapshot:" + snapshot.event.toString());
			System.out.println("tmp:" + tmp.event.toString());
			listener.onTimewarp(current = tmp);
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
