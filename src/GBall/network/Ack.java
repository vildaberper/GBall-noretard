package GBall.network;

import java.io.Serializable;

public class Ack implements Serializable {

	private static final long serialVersionUID = -5633967981911948689L;	

	public long id;
	
	public Ack(long id) {
		this.id = id;
	}
}
