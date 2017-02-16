package GBall.engine;

import java.io.Serializable;

public abstract class Entity implements Serializable {
	private static final long serialVersionUID = -9029024866598246630L;

	public Vector2 position = new Vector2(), velocity = new Vector2(), direction = new Vector2(-1.0, 0.0);
	
	public double acceleration = 0.0;

	public boolean dead = false;

	public long lastTick = 0;

	public void tick(double dt, long time) {
		if(acceleration > 0.0)
			velocity.add(direction.clone().scale(acceleration * dt));
		else
			velocity.scale(friction(), dt);

		if (velocity.length() > maxSpeed())
			velocity.normalize().scale(maxSpeed());
		
		position.add(velocity.clone().scale(dt));
		
		lastTick = time;
	}

	public abstract void render(GameWindow gw, long time);

	public abstract double friction();

	public abstract int radius();

	public abstract double maxSpeed();

}
