package GBall.engine.event;

public class OffsetEvent extends Event {
	private static final long serialVersionUID = -751182465233186810L;

	public final long offset;

	public OffsetEvent(long offset) {
		super(0);
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "OffsetEvent - " + frame + " " + offset;
	}

}
