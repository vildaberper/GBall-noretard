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

		public Snapshot(GameState state, Event event) {
			this.state = state;
			this.event = event;
		}

	}

	private final StateListener listener;

	private Snapshot current;
	private Snapshot first;
	private Snapshot last;

	public StateManager(StateListener listener) {
		this.listener = listener;
	}

	public void step(long time) {
		while (current != null && current.event.timestamp <= time) {
			listener.onEvent(current);
			current = current.next;
		}

		removeOld(time);
	}

	private void decouple(Snapshot s) {
		if (s != null) {
			if (s.previous != null)
				s.previous.next = s.next != null ? s.next : null;
			if (s.next != null)
				s.next.previous = s.previous != null ? s.previous : null;
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

	private void removeOld(long time) {
		while (first != null && (time - first.event.timestamp) > Const.OUTDATED_THRESHOLD) {
			Snapshot temp = first.next;
			decouple(first);
			first = temp;
		}
	}

	public void add(Event event, GameState state) {
		Snapshot snapshot = new Snapshot(state, event);

		if (first == null) {
			first = last = current = snapshot;
			return;
		}

		if (current == null) {
			couple(last, current = snapshot);
			last = current;
			return;
		}

		Snapshot tmp = last;
		while (snapshot.event.timestamp < tmp.event.timestamp) {
			if ((tmp = tmp.previous) == null) {
				System.out.println("shit fucked up");
				return;
			}
		}
		if (tmp.next == null) {
			couple(tmp, last = snapshot);
		} else
			couple(tmp, snapshot, tmp.next);

		if (current.event.timestamp > snapshot.event.timestamp)
			listener.onTimewarp(current = tmp);
	}

}
