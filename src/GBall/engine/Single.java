package GBall.engine;

public class Single<T> {

	private T value;

	public Single(T value) {
		setValue(value);
	}

	public Single() {
		this(null);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

}
