package GBall.engine.event;

import GBall.engine.Entity;

public class AddEntityEvent extends Event {
	private static final long serialVersionUID = 7450487098977678837L;

	public final Entity entity;

	public AddEntityEvent(long framestamp, Entity entity) {
		super(framestamp);
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "AddEntityEvent - " + frame + " " + entity.id;
	}

}
