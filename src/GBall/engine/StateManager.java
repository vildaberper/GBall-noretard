package GBall.engine;

import GBall.GameState;
import GBall.engine.event.Event;

public class StateManager {
	
	public interface StateManagerListener {
		
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

	private final StateManagerListener listener;
	
	
	private Snapshot current;
	private Snapshot first;
	private Snapshot last;
	
	public StateManager(StateManagerListener listener) {
		
		this.listener = listener;
	}
	
	public void step(long time) {
		
		while (current != null && current.event.timestamp <= time) {
			listener.onEvent(current);
			current = current.next;
		}
		
		removeOld(time);
	}
	
	private void removeOld(long time) {
		
		while ((time - first.event.timestamp) > Const.OUTDATED_THRESHOLD) {
			
			Snapshot temp = first.next;
			first.next = null;
			first.previous = null;
			first = temp;
			first.previous = null;
		}
	}

	public void add(Event event, GameState state) {
		
		Snapshot snapshot = new Snapshot(state, event);

		if (first == null) {

			first = last = current = snapshot;
		}

		if (snapshot.event.timestamp > last.event.timestamp) {

			last.next = snapshot;
			last = snapshot;
			return;
		}

		if (snapshot.event.timestamp < current.event.timestamp) {
			current = insert(snapshot, current);
			listener.onTimewarp(current);
			return;
		}

		insert(snapshot, last);

	}

	private Snapshot insert(Snapshot snapshot, Snapshot startAt) {

		Snapshot it = startAt;
		while (it.event.timestamp > snapshot.event.timestamp) {

			if (it.previous == null) {
				first = snapshot;
				snapshot.next = it;
				it.previous = snapshot;
				return snapshot;
			}

			it = it.previous;
		}

		snapshot.next = it.next;
		snapshot.previous = it;
		it.next = snapshot;

		return it;
	}

}
