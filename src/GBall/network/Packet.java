package GBall.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;

public class Packet implements Serializable {
	private static final long serialVersionUID = 5361078578409138060L;

	public static byte[] serialize(Serializable o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.flush();
			byte[] result = baos.toByteArray();
			oos.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static Object desieralize(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object result = ois.readObject();
			ois.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private final byte[] data;

	public Packet(byte[] data) {
		this.data = data;
	}

	public Packet(Serializable o) {
		this(serialize(o));
	}

	public Packet(DatagramPacket datagramPacket) {
		data = new byte[datagramPacket.getLength()];
		System.arraycopy(datagramPacket.getData(), datagramPacket.getOffset(), data, 0,
				datagramPacket.getLength());
	}

	public Object getObject() {
		return desieralize(getData());
	}

	public byte[] getData() {
		return data;
	}

	public DatagramPacket toDatagramPacket(Location target) {
		return new DatagramPacket(getData(), 0, getData().length, target.ip, target.port);
	}

	@Override
	public Packet clone() {
		return new Packet(getData().clone());
	}

}
