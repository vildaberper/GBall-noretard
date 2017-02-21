package GBall.engine;

import GBall.engine.World.WorldState;
import GBall.engine.event.Event;

public class StateManager {
		
	public class Snapshot{
		
		private final WorldState state;
		private final Event event;
		
		public Snapshot next;
		public Snapshot previous;
		
		public Snapshot(WorldState state, Event event) {
			
			this.state = state;
			this.event = event;
		}
	}
	
	private Snapshot current;
	private Snapshot first;
	private Snapshot last;
	
	private void timewarp(Snapshot snapshot) {
		
		return;
	}
	
	public void add(Snapshot snapshot) {
		
		if (first == null) {
			
			first = last = current = snapshot;			
		}		
		
		if (snapshot.event.timestamp > last.event.timestamp) {
			
			last.next = snapshot;
			last = snapshot;
			return;
		}
		
		if (snapshot.event.timestamp < current.event.timestamp) {	
			timewarp(insert(snapshot, current));
			return;
		}
		
		insert(snapshot, last);		
		
	}
	
	private Snapshot insert(Snapshot snapshot, Snapshot startAt) {
		
		Snapshot it = startAt;
		while (it.event.timestamp > snapshot.event.timestamp)				
			it = it.previous;
		
		snapshot.next = it.next;
		snapshot.previous = it;
		it.next = snapshot;
		
		return it;
	}

}
