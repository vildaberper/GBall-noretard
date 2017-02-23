package GBall;

import java.io.Serializable;

import GBall.engine.WorldState;

public class GameState implements Serializable {
	private static final long serialVersionUID = -1639803764407549028L;

	public final WorldState worldState;
	public final int scoreRed, scoreGreen;
	public final long frame;

	public GameState(WorldState worldState, int scoreRed, int scoreGreen, long frame) {
		this.worldState = worldState;
		this.scoreRed = scoreRed;
		this.scoreGreen = scoreGreen;
		this.frame = frame;
	}

}
