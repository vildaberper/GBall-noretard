package GBall.engine;

import java.io.Serializable;
import java.util.HashMap;

public class WorldState implements Serializable {
	private static final long serialVersionUID = -8005336361056943857L;

	private static HashMap<Long, Entity> clone(HashMap<Long, Entity> map) {
		HashMap<Long, Entity> nmap = new HashMap<Long, Entity>();
		map.entrySet().forEach(e -> {
			nmap.put(e.getKey(), e.getValue().clone());
		});
		return nmap;
	}

	public long frame;
	public HashMap<Long, Entity> entities;

	public WorldState(long frame, HashMap<Long, Entity> entities) {
		this.frame = frame;
		this.entities = clone(entities);
	}

	@Override
	public WorldState clone() {
		return new WorldState(frame, entities);
	}

}
