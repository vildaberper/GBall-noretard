package GBall.engine;

import java.io.Serializable;

public abstract class Entity implements Serializable {
	private static final long serialVersionUID = -9029024866598246630L;

	public final long id;

	public Vector2 position = new Vector2(), velocity = new Vector2(), direction = new Vector2(-1.0, 0.0);

	public double acceleration = 0.0;

	public long lastFrame = 0;

	protected Entity(long id) {
		this.id = id;
	}

	public void tick(double dt, long frame) {
		if (acceleration > 0.0)
			velocity.add(direction.clone().scale(acceleration * dt));
		else
			velocity.scale(friction(), dt);

		if (velocity.length() > maxSpeed())
			velocity.normalize().scale(maxSpeed());

		position.add(velocity.clone().scale(dt));

		lastFrame = frame;
	}

	public abstract void render(GameWindow gw, long time);

	public abstract double friction();

	public abstract int radius();

	public abstract double maxSpeed();
	
	@Override
	public abstract Entity clone();

}
