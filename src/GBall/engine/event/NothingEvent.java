package GBall.engine.event;

public class NothingEvent extends Event {
	private static final long serialVersionUID = -1196866376720772080L;

	public NothingEvent(long framestamp) {
		super(framestamp);
	}

	@Override
	public String toString() {
		return "NothingEvent - " + framestamp;
	}

}
