package GBall.network;

import java.io.Serializable;

public class IDContainer implements Serializable {
	private static final long serialVersionUID = 1920803627868818586L;

	public final long id;
	public final Serializable o;

	public IDContainer(long id, Serializable o) {
		this.id = id;
		this.o = o;
	}

	public IDContainer(long id) {
		this(id, null);
	}

	public boolean isACK() {
		return o == null;
	}

	public Packet toPacket() {
		return new Packet(this);
	}

}
