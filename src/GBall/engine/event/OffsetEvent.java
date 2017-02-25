package GBall.engine.event;

public class OffsetEvent extends Event {
	private static final long serialVersionUID = -751182465233186810L;

	public final long offset;

	public OffsetEvent(long framestamp, long offset) {
		super(framestamp);
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "OffsetEvent - " + framestamp + " " + offset;
	}

}
