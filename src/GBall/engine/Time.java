package GBall.engine;

public final class Time {

	private static long offset = 0;

	public static long getTime() {
		return Util.millis() + offset;
	}

	public static long getOffset() {
		return offset;
	}

	public static void setOffset(long offset) {
		Time.offset = offset;
	}

}
