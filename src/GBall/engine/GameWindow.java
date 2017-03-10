package GBall.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow implements WindowListener {

	public interface GameWindowListener {

		public void render(GameWindow gw);

		public void onClose();

	}

	private class Panel extends JPanel {
		private static final long serialVersionUID = -1946087136085511129L;

		@Override
		public void paintComponent(Graphics g) {
			graphics = g;
			setColor(Const.BG_COLOR);
			fillRect(0, 0, Const.DISPLAY_WIDTH, Const.DISPLAY_HEIGHT);
			listener.render(instance);
		}
	}

	private final JFrame frame;
	private final Panel panel;
	private Graphics graphics;

	private final GameWindowListener listener;

	private final GameWindow instance = this;

	public GameWindow(GameWindowListener listener, String titleSuffix) {
		this.listener = listener;

		frame = new JFrame(Const.APP_NAME + (titleSuffix != null ? " " + titleSuffix : ""));
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new Panel();
		panel.setPreferredSize(new Dimension(Const.DISPLAY_WIDTH, Const.DISPLAY_HEIGHT));
		frame.add(panel);

		frame.pack();
		frame.setVisible(true);
	}

	public GameWindow(GameWindowListener listener) {
		this(listener, null);
	}

	public void setColor(Color color) {
		graphics.setColor(color);
	}

	public void setFont_(Font font) {
		graphics.setFont(font);
	}

	public void drawString(String string, Vector2 position) {
		graphics.drawString(string, (int) position.x, (int) position.y);
	}

	public void drawCircle(Vector2 position, int radius) {
		graphics.drawOval((int) position.x - radius, (int) position.y - radius, radius * 2, radius * 2);
	}

	public void drawLine(Vector2 position, double angle, int length) {
		graphics.drawLine((int) position.x, (int) position.y, (int) (position.x + Math.cos(angle) * length),
				(int) (position.y + Math.sin(angle) * length));
	}

	public void fillRect(int x, int y, int width, int height) {
		graphics.fillRect(x, y, width, height);
	}

	public void repaint() {
		panel.repaint();
	}

	public void addKeyListener(KeyListener keyListener) {
		frame.addKeyListener(keyListener);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		listener.onClose();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
