package GBall.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Packet {

	public static byte[] serialize(Object o) {
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

	public Packet(Object o) {
		this(serialize(o));
	}

	public Object getObject() {
		return desieralize(getData());
	}

	public byte[] getData() {
		return data;
	}

}
