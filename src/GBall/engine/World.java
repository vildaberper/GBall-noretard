package GBall.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import GBall.engine.Vector2.Direction;

public class World {

	public interface WorldListener {

		public void onWallCollide(Entity e, Direction d);

		public void onEntityCollide(Entity e1, Entity e2);

	}

	private final WorldListener listener;

	private long lastTick;

	private double dt = 0.0;

	private Map<Long, Entity> entities = new HashMap<Long, Entity>();

	public World(WorldListener listener) {
		this.listener = listener;
		lastTick = Time.getTime();
	}

	public void forEachEntity(Consumer<? super Entity> func) {
		entities.entrySet().forEach(e -> func.accept(e.getValue()));
	}

	public void addEntity(Entity... e) {
		for (Entity e_ : e)
			entities.put(e_.id, e_);
	}

	public void removeAll() {
		entities.clear();
	}

	public double fps() {
		return dt > 0.0 ? 1.0 / dt : 0.0;
	}

	// TODO Move
	private static double interv(double d1, double d2) {
		return d1 > d2 ? d1 - d2 : d2 - d1;
	}

	private void checkWallCollision_helper(Entity e, Direction d) {
		if (e.velocity.isMovingInDirection(d))
			listener.onWallCollide(e, d);
	}

	private void checkWallCollision(Entity e) {
		if (interv(e.position.x, Const.WINDOW_BORDER_WIDTH) < e.radius())
			checkWallCollision_helper(e, Direction.LEFT);

		if (interv(e.position.x, Const.DISPLAY_WIDTH - Const.WINDOW_BORDER_WIDTH) < e.radius())
			checkWallCollision_helper(e, Direction.RIGHT);

		if (interv(e.position.y, Const.WINDOW_TOP_HEIGHT) < e.radius())
			checkWallCollision_helper(e, Direction.UP);

		if (interv(e.position.y, Const.DISPLAY_HEIGHT - Const.WINDOW_BOTTOM_HEIGHT) < e.radius())
			checkWallCollision_helper(e, Direction.DOWN);
	}

	private void checkEntityCollision(Entity e1) {
		forEachEntity(e2 -> {
			if (e1.equals(e2))
				return;

			if (e1.position.distance(e2.position) < e1.radius() + e2.radius())
				listener.onEntityCollide(e1, e2);
		});
	}

	public void update() {
		long time = Time.getTime();

		if (time == lastTick)
			return;

		dt = (time - lastTick) / 1000.0;

		entities.entrySet().removeIf(e -> e.getValue().dead);

		forEachEntity(e -> e.tick(dt, time));
		forEachEntity(e -> checkWallCollision(e));
		forEachEntity(e -> checkEntityCollision(e));

		lastTick = time;
	}

	public void render(GameWindow gw) {
		long time = Time.getTime();

		forEachEntity(e -> e.render(gw, time));
	}

}
