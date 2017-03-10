package GBall.engine;

public final class Time {

	// Singleton just because Singletons are the best xP ^__^ g
	// g

	// (_)_)::::::::::::::::::D ~ ~ choo choo ~ ~ rocket penis ~ ~

	// Q(o.oQ) fight me irl fgt

	private static Time instance = null;

	private static Time getInstance() {
		return instance == null ? instance = new Time() : instance;
	}

	public static long getTime() {
		return getInstance().getTime_();
	}

	public static long getOffset() {
		return getInstance().getOffset_();
	}

	public static void setOffset(long offset) {
		getInstance().setOffset_(offset);
	}

	private long offset = 0;

	private Time() {

	}

	private long getTime_() {
		return Util.millis() + offset;
	}

	private long getOffset_() {
		return offset;
	}

	private void setOffset_(long offset) {
		this.offset = offset;
	}

}
