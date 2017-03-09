package GBall;

import java.io.Serializable;

import GBall.engine.event.StateEvent;

public class Handshake implements Serializable {
	private static final long serialVersionUID = 4386608413694294176L;

	public final long startTime;
	public final long id;
	public final StateEvent stateEvent;

	public Handshake(long startTime, long id, StateEvent stateEvent) {
		this.startTime = startTime;
		this.id = id;
		this.stateEvent = stateEvent;
	}

}
