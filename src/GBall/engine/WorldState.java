package GBall.engine;

import java.io.Serializable;
import java.util.HashMap;

public class WorldState implements Serializable {
	private static final long serialVersionUID = -8005336361056943857L;

	public long frame;
	public HashMap<Long, Entity> entities;

	public WorldState(long frame, HashMap<Long, Entity> entities) {
		this.frame = frame;
		this.entities = Util.clone(entities);
	}

	@Override
	public WorldState clone() {
		HashMap<Long, Entity> nentities = new HashMap<Long, Entity>();

		entities.entrySet().forEach(e -> nentities.put(e.getKey(), e.getValue().clone()));

		return new WorldState(frame, Util.clone(entities));
	}

}
