package GBall.engine.event;

public class RemoveEntityEvent extends Event {
	private static final long serialVersionUID = -2252862911877983853L;

	public final long entityId;

	public RemoveEntityEvent(long framestamp, long entityId) {
		super(framestamp);
		this.entityId = entityId;
	}

	@Override
	public String toString() {
		return "RemoveEntityEvent - " + frame + " " + entityId;
	}

}
