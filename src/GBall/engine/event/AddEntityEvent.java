package GBall.engine.event;

import GBall.engine.Entity;

public class AddEntityEvent extends Event{
	
	public final Entity entity;
	
	public AddEntityEvent(long framestamp, Entity entity){
		super(framestamp);
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "AddEntityEvent - " + framestamp + " EntityId - " + entity.id;
	}
	
}
