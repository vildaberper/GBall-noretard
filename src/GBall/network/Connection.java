package GBall.network;

import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection {
	
	public interface ConnectionListener {
		public void onConnect(Connection c);
	}	
	
	private Map<Long, Object> handled = new ConcurrentHashMap<Long, Object>();
	private Map<Long, Object> acked = new ConcurrentHashMap<Long, Object>();
	private Queue<Packet> packets = new ConcurrentLinkedQueue<Packet>();	
	
	public Location location;
	private UDPSocket socket;	
	public long clientId = -1;	
	private long packetId = 0;	
	private final ConnectionListener listener;
	private final Timer timer;
	
	public Connection(Location location, UDPSocket socket, ConnectionListener listener) {
		this.location = location;
		this.socket = socket;
		this.listener = listener;		
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Packet p = packets.poll();
				
				if (p == null) {
					return;
				}
				
				if (!acked.containsKey(p.id))
					resend(p);
			}			
		};
		
		timer = new Timer();
		timer.schedule(task, 0, 10);		
	}
	
	public void connect() {		
		listener.onConnect(this);
	}
	
	public void open(SocketListener socketListener)  {
		socket.open(socketListener);
	}
	
	public void send(Packet packet) {
		packet.id = ++packetId;
		socket.send(location, packet);
		packets.add(packet);
	}
	
	private void resend(Packet packet) {
		socket.send(location, packet);
		packets.add(packet);
	}
	
	public void sendAck(long id) {
		Packet packet = new Packet(new Ack(id));
		packet.id = ++packetId;
		socket.send(location, packet);
	}
	
	public void addAck(long id) {		
		System.out.println("Ack added for id: " + id);
		acked.put(id, 0);
	}
	
	public void addHandled(long id) {
		handled.put(id, 0);
	}
	
	public boolean handled(long id) {
		return handled.containsKey(id);
	}
	
	public void close() {
		timer.cancel();
		socket.close();
	}

}
