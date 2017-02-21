package GBall;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import GBall.engine.Vector2.Direction;

public class Controller implements KeyListener {

	public interface ControllerListener {

		public void onPress(Direction d);

		public void onRelease(Direction d);

	}

	private final int left, right, up, down;

	private final ControllerListener listener;

	public Controller(int left, int right, int up, int down, ControllerListener listener) {
		this.left = left;
		this.right = right;
		this.up = up;
		this.down = down;
		this.listener = listener;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == left) {
			listener.onPress(Direction.LEFT);
		} else if (e.getKeyCode() == right) {
			listener.onPress(Direction.RIGHT);
		} else if (e.getKeyCode() == up) {
			listener.onPress(Direction.UP);
		} else if (e.getKeyCode() == down) {
			listener.onPress(Direction.DOWN);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == left) {
			listener.onRelease(Direction.LEFT);
		} else if (e.getKeyCode() == right) {
			listener.onRelease(Direction.RIGHT);
		} else if (e.getKeyCode() == up) {
			listener.onRelease(Direction.UP);
		} else if (e.getKeyCode() == down) {
			listener.onRelease(Direction.DOWN);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
