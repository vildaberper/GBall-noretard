package GBall.network;

import java.io.Serializable;

import GBall.engine.event.StateEvent;

public class Handshake implements Serializable {

	private static final long serialVersionUID = -5739980111900226963L;
	
	public long startTime;
	public long clientId;
	public long frame;
	public StateEvent stateEvent;
	
	public Handshake(long startTime, long clientId, long frame, StateEvent stateEvent) {
		this.startTime = startTime;
		this.clientId = clientId;
		this.frame = frame;
		this.stateEvent = stateEvent;
	}

}
