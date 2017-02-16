package GBall.engine;

public class Ball extends Entity {
	private static final long serialVersionUID = 6843930351309156932L;

	@Override
	public void render(GameWindow gw, long time) {
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

}
