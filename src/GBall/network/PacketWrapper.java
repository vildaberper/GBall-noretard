package GBall.network;

import java.io.Serializable;

public class PacketWrapper implements Serializable {

	private static final long serialVersionUID = -3237711528920135230L;
	
	public long id;
	public byte[] data;
	
	public PacketWrapper(long id, byte[] data) {
		this.id = id;
		this.data = data;
	}
}
