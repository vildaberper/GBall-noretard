package GBall.engine;

import java.util.HashMap;
import java.util.function.Consumer;

import GBall.engine.Vector2.Direction;

import static GBall.engine.Util.*;

public class World {

	public interface WorldListener {

		public void onWallCollide(Entity e, Direction d, double dist);

		public void onEntityCollide(Entity e1, Entity e2);

	}

	private final WorldListener listener;

	private WorldState state;

	public World(WorldListener listener) {
		this.listener = listener;
		state = new WorldState(0L, new HashMap<Long, Entity>());
	}

	public WorldState getState() {
		return state;
	}

	public void setState(WorldState state) {
		this.state = state;
	}

	public int size() {
		return state.entities.size();
	}

	public void forEachEntity(Consumer<? super Entity> func) {
		state.entities.entrySet().forEach(e -> func.accept(e.getValue()));
	}

	public void addEntity(Entity... e) {
		for (Entity e_ : e)
			state.entities.put(e_.id, e_);
	}

	public Entity getEntity(long id) {
		return state.entities.get(id);
	}

	public void clear() {
		state.entities.clear();
	}

	private void checkWallCollision_helper(Entity e, Direction d, double dist) {
		if (e.velocity.isMovingInDirection(d))
			listener.onWallCollide(e, d, dist);
	}

	private void checkWallCollision(Entity e) {
		double d;

		if ((d = interv(e.position.x, Const.WINDOW_BORDER_WIDTH)) < e.radius())
			checkWallCollision_helper(e, Direction.LEFT, d);

		if ((d = interv(e.position.x, Const.DISPLAY_WIDTH - Const.WINDOW_BORDER_WIDTH)) < e.radius())
			checkWallCollision_helper(e, Direction.RIGHT, d);

		if ((d = interv(e.position.y, Const.WINDOW_TOP_HEIGHT)) < e.radius())
			checkWallCollision_helper(e, Direction.UP, d);

		if ((d = interv(e.position.y, Const.DISPLAY_HEIGHT - Const.WINDOW_BOTTOM_HEIGHT)) < e.radius())
			checkWallCollision_helper(e, Direction.DOWN, d);
	}

	private void checkEntityCollision(Entity e1) {
		forEachEntity(e2 -> {
			if (e1.equals(e2))
				return;

			if (e1.position.distance(e2.position) < e1.radius() + e2.radius())
				listener.onEntityCollide(e1, e2);
		});
	}

	public void update(long frame) {
		while (state.frame < frame) {
			forEachEntity(e -> e.tick(Const.DT, frame));
			forEachEntity(e -> checkWallCollision(e));
			forEachEntity(e -> checkEntityCollision(e));
			++state.frame;
		}
	}

	public void render(GameWindow gw) {
		forEachEntity(e -> e.render(gw, state.frame));
	}

}
