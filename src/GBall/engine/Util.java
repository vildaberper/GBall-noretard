package GBall.engine;

public class Util {

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

}
