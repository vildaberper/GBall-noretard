package GBall.network;

import java.io.Serializable;

public class ServerClient<T> {

	public final Connection connection;
	private T data = null;

	public ServerClient(Connection connection) {
		this.connection = connection;
	}

	public void send(Serializable o) {
		connection.send(o);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ServerClient<?> && ((ServerClient<?>) o).connection.location.equals(connection.location);
	}

}
