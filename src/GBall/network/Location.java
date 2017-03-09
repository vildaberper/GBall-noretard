package GBall.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Location {

	public final InetAddress ip;
	public final int port;

	public Location(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public Location(String hostName, int port) throws UnknownHostException {
		this(InetAddress.getByName(hostName), port);
	}

	@Override
	public String toString() {
		String s = ip.toString();
		int i = s.indexOf('/');

		return (i == -1 ? s : s.substring(i + 1)) + ":" + port;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Location && o.hashCode() == hashCode();
	}

}
