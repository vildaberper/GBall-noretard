package GBall.engine;

import java.awt.Color;

public class Ship extends Entity {
	private static final long serialVersionUID = 5331656060833494804L;

	public int rotation = 0;
	public boolean braking = false;

	public final Color color;

	public Ship(long id, Color color) {
		super(id);
		this.color = color;
	}

	@Override
	public void tick(double dt, long time) {
		if (rotation != 0) {
			direction.rotate(rotation * Const.SHIP_ROTATION * dt);
			velocity.scale(Const.SHIP_TURN_BRAKE_SCALE, dt);
		}
		if (braking) {
			velocity.scale(Const.SHIP_BRAKE_SCALE, dt);
			acceleration = 0.0;
		}

		super.tick(dt, time);
	}

	@Override
	public void render(GameWindow gw, long time) {
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

}
