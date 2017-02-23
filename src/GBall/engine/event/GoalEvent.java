package GBall.engine.event;

public class GoalEvent extends Event {
	private static final long serialVersionUID = 7540003158312307219L;

	public final boolean red;

	public GoalEvent(long framestamp, boolean red) {
		super(framestamp);
		this.red = red;
	}

}
