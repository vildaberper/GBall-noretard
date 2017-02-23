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

	private boolean[] lastState = new boolean[4];

	private final ControllerListener listener;

	public Controller(int left, int right, int up, int down, ControllerListener listener) {
		this.left = left;
		this.right = right;
		this.up = up;
		this.down = down;
		this.listener = listener;
		for (int i = 0; i < 4; ++i)
			lastState[i] = false;
	}

	private Direction d(int keyCode) {
		if (keyCode == left) {
			return Direction.LEFT;
		} else if (keyCode == right) {
			return Direction.RIGHT;
		} else if (keyCode == up) {
			return Direction.UP;
		} else if (keyCode == down) {
			return Direction.DOWN;
		}
		return null;
	}

	private int i(Direction d) {
		switch (d) {
		case LEFT:
			return 0;
		case RIGHT:
			return 1;
		case UP:
			return 2;
		case DOWN:
			return 3;
		}
		return -1;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Direction d = d(e.getKeyCode());
		int i;
		if (d != null && !lastState[i = i(d)]) {
			lastState[i] = true;
			listener.onPress(d);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Direction d = d(e.getKeyCode());
		int i;
		if (d != null && lastState[i = i(d)]) {
			lastState[i] = false;
			listener.onRelease(d);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
