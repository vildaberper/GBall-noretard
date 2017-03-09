package GBall.engine.event;

public class ResetGoalsEvent extends Event {
	private static final long serialVersionUID = -8471740470287227585L;

	public ResetGoalsEvent(long framestamp) {
		super(framestamp);
	}

	@Override
	public String toString() {
		return "ResetEvent - " + frame;
	}

}
