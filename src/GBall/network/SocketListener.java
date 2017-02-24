package GBall.network;

public interface SocketListener {

	public void onReceive(Location source, Packet packet);

}
