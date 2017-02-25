package GBall.engine;

import java.awt.Color;

import GBall.Controller.ControllerListener;
import GBall.engine.Vector2.Direction;

public class Ship extends Entity implements ControllerListener {
	private static final long serialVersionUID = 5331656060833494804L;

	public boolean left = false, right = false, up = false, down = false;

	public int rotation = 0;
	public boolean braking = false;

	public final Color color;

	private Ship(Ship s) {
		super(s.id);
		position = s.position.clone();
		velocity = s.velocity.clone();
		direction = s.direction.clone();
		acceleration = s.acceleration;
		lastFrame = s.lastFrame;
		left = s.left;
		right = s.right;
		up = s.up;
		down = s.down;
		rotation = s.rotation;
		braking = s.braking;
		color = s.color;
	}

	public Ship(long id, Color color) {
		super(id);
		this.color = color;
	}

	@Override
	public void tick(double dt, long frame) {
		acceleration = up ? Const.SHIP_MAX_ACCELERATION : 0;
		braking = down;
		rotation = (left ? -1 : 0) + (right ? 1 : 0);

		if (rotation != 0) {
			direction.rotate(rotation * Const.SHIP_ROTATION * dt);
			velocity.scale(Const.SHIP_TURN_BRAKE_SCALE, dt);
		}
		if (braking) {
			velocity.scale(Const.SHIP_BRAKE_SCALE, dt);
			acceleration = 0.0;
		}

		super.tick(dt, frame);
	}

	@Override
	public void render(GameWindow gw, long frame) {
		gw.setColor(color);
		gw.drawCircle(position, radius());
		gw.drawLine(position, direction.angle(), radius());
	}

	@Override
	public double friction() {
		return Const.SHIP_FRICTION;
	}

	@Override
	public int radius() {
		return Const.SHIP_RADIUS;
	}

	@Override
	public double maxSpeed() {
		return Const.BALL_MAX_SPEED;
	}

	public boolean isPressed(Direction d) {
		switch (d) {
		default:
		case UP: {
			return up;
		}
		case DOWN: {
			return down;
		}
		case LEFT: {
			return left;
		}
		case RIGHT: {
			return right;
		}
		}
	}

	private void control(Direction d, boolean press) {
		switch (d) {
		case UP: {
			up = press;
			break;
		}
		case DOWN: {
			down = press;
			break;
		}
		case LEFT: {
			left = press;
			break;
		}
		case RIGHT: {
			right = press;
			break;
		}
		}
	}

	@Override
	public void onPress(Direction d) {
		control(d, true);
	}

	@Override
	public void onRelease(Direction d) {
		control(d, false);
	}

	@Override
	public Entity clone() {
		return new Ship(this);
	}

}
