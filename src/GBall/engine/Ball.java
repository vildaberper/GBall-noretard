package GBall.engine;

public class Ball extends Entity {
	private static final long serialVersionUID = 6843930351309156932L;

	private Ball(Ball b) {
		super(b.id);
		position = b.position.clone();
		velocity = b.velocity.clone();
		direction = b.direction.clone();
		acceleration = b.acceleration;
		lastFrame = b.lastFrame;
	}

	public Ball(long id) {
		super(id);
	}

	@Override
	public void render(GameWindow gw, long frame) {
		gw.setColor(Const.BALL_COLOR);
		gw.drawCircle(position, radius());
	}

	@Override
	public double friction() {
		return Const.BALL_FRICTION;
	}

	@Override
	public int radius() {
		return Const.BALL_RADIUS;
	}

	@Override
	public double maxSpeed() {
		return Const.BALL_MAX_SPEED;
	}

	@Override
	public Entity clone() {
		return new Ball(this);
	}

}
