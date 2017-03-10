package GBall.engine;

public class Util {

	public static long millis() {
		return System.currentTimeMillis();
	}

	public static boolean sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	public static double interv(double d1, double d2) {
		return d1 > d2 ? d1 - d2 : d2 - d1;
	}

	public static long minmax(long l, long minmax) {
		if (minmax < 0)
			minmax = -minmax;

		return l > minmax ? minmax : (l < -minmax ? -minmax : l);
	}

}
