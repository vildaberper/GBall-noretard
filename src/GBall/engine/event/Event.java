package GBall.engine.event;

import java.io.Serializable;

public abstract class Event implements Serializable {
	private static final long serialVersionUID = 6091575079684803289L;

	public final long frame;

	protected Event(long framestamp) {
		this.frame = framestamp;
	}

	public long getEntityId() {
		return -1;
	}

	@Override
	public abstract String toString();

}
