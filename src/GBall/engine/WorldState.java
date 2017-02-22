package GBall.engine;

import java.io.Serializable;
import java.util.HashMap;

public class WorldState implements Serializable {
	private static final long serialVersionUID = -8005336361056943857L;

	public long lastTick;
	public double dt = 0.0;
	public HashMap<Long, Entity> entities;

	public WorldState(long lastTick, double dt, HashMap<Long, Entity> entities) {
		this.lastTick = lastTick;
		this.dt = dt;
		this.entities = entities;
	}

	@Override
	public WorldState clone() {
		HashMap<Long, Entity> nentities = new HashMap<Long, Entity>();

		entities.entrySet().forEach(e -> nentities.put(e.getKey(), e.getValue().clone()));

		return new WorldState(lastTick, dt, nentities);
	}

}
