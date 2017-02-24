package GBall.engine;

import java.util.HashMap;

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

	public static HashMap<Long, Entity> clone(HashMap<Long, Entity> map) {
		HashMap<Long, Entity> nmap = new HashMap<Long, Entity>();
		map.entrySet().forEach(e -> {
			nmap.put(e.getKey(), e.getValue().clone());
		});
		return nmap;
	}

}
