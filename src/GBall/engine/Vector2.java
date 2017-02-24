package GBall.engine;

import java.io.Serializable;

public class Vector2 implements Serializable {
	private static final long serialVersionUID = -6117108900769577833L;

	public static enum Direction {
		LEFT, RIGHT, UP, DOWN;

		public static String toString(Direction d) {
			switch (d) {
			default:
			case LEFT:
				return "LEFT";
			case RIGHT:
				return "RIGHT";
			case UP:
				return "UP";
			case DOWN:
				return "DOWN";
			}
		}
	}

	public double x, y;

	public Vector2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2(double x, double y) {
		set(x, y);
	}

	public Vector2() {
		this(0.0, 0.0);
	}

	public boolean isMovingInDirection(Direction d) {
		switch (d) {
		default:
		case LEFT:
			return x < 0.0;
		case RIGHT:
			return x > 0.0;
		case UP:
			return y < 0.0;
		case DOWN:
			return y > 0.0;
		}
	}

	public void invertInDirection(Direction d) {
		switch (d) {
		case LEFT:
		case RIGHT:
			x = -x;
			break;
		case UP:
		case DOWN:
			y = -y;
			break;
		}
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public Vector2 scale(double d) {
		x *= d;
		y *= d;
		return this;
	}

	public Vector2 scale(double d, double dt) {
		return scale(1.0 / (1.0 + d * dt));
	}

	public Vector2 normalize() {
		double length = length();

		if (length > 0.0)
			scale(1.0 / length);
		return this;
	}

	public Vector2 invert() {
		x = -x;
		y = -y;
		return this;
	}

	public Vector2 zero() {
		x = y = 0.0;
		return this;
	}

	public Vector2 add(double dx, double dy) {
		x += dx;
		y += dy;
		return this;
	}

	public Vector2 add(Vector2 dv) {
		return add(dv.x, dv.y);
	}

	public double angle() {
		return Math.atan2(y, x);
	}

	public Vector2 rotate(double da) {
		double length = length();

		if (length > 0.0) {
			da += angle();
			x = Math.cos(da) * length;
			y = Math.sin(da) * length;
		}
		return this;
	}

	public double dot(Vector2 v) {
		return x * v.x + y * v.y;
	}

	public double distance(Vector2 v) {
		return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2));
	}

	@Override
	public Vector2 clone() {
		return new Vector2(x, y);
	}

}
