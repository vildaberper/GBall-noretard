package GBall.engine;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import GBall.engine.Vector2.Direction;

public class World {

	public interface WorldListener {

		public void onWallCollide(Entity e, Direction d);

		public void onEntityCollide(Entity e1, Entity e2);

	}

	private final WorldListener listener;

	private long lastTick;

	private Set<Entity> entities = new HashSet<Entity>();

	public World(WorldListener listener) {
		this.listener = listener;
		lastTick = Time.getTime();
	}

	public void forEachEntity(Consumer<? super Entity> func) {
		entities.forEach(func);
	}

	public void addEntity(Entity... e) {
		for (Entity e_ : e)
			entities.add(e_);
	}

	public void removeAll() {
		entities.clear();
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
		entities.forEach(e2 -> {
			if (e1.equals(e2))
				return;

			if (e1.position.distance(e2.position) < e1.radius() + e2.radius())
				listener.onEntityCollide(e1, e2);
		});
	}

	public void update() {
		long time = Time.getTime();
		double dt = (time - lastTick) / 1000.0;

		entities.removeIf(e -> e.dead);

		entities.forEach(e -> e.tick(dt, time));
		entities.forEach(e -> checkWallCollision(e));
		entities.forEach(e -> checkEntityCollision(e));

		lastTick = time;
	}

	public void render(GameWindow gw) {
		long time = Time.getTime();

		entities.forEach(e -> e.render(gw, time));
	}

}
