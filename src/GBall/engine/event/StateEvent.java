package GBall.engine.event;

import GBall.GameState;

public class StateEvent extends Event {
	private static final long serialVersionUID = 2416654417548168919L;

	public final GameState state;

	public StateEvent(GameState state) {
		super(state.frame);
		this.state = state;
	}

	@Override
	public String toString() {
		return "StateEvent - " + framestamp;
	}

}
