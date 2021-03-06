package GBall.engine.event;

import GBall.engine.Vector2.Direction;

public class ControllerEvent extends Event {
	private static final long serialVersionUID = -8753934043164132239L;

	public final long entityId;

	public final Direction direction;

	public final boolean press;

	public ControllerEvent(long framestamp, long entityId, Direction direction, boolean press) {
		super(framestamp);
		this.entityId = entityId;
		this.direction = direction;
		this.press = press;
	}

	@Override
	public String toString() {
		return "ControllerEvent - " + frame + " " + entityId + " " + Direction.toString(direction) + " " + press;
	}
	
	@Override
	public long getEntityId() {
		return entityId;
	}

}
