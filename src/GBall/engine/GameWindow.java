package GBall.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GameWindow extends Frame implements WindowListener {
	private static final long serialVersionUID = -6791313761602059823L;

	public interface GameWindowListener {

		public void render(GameWindow gw);

	}

	// private Image background;
	private Image offScreenImage;
	private Graphics offScreenGraphicsCtx; // Used for double buffering

	// private final static int YOFFSET = 34;
	// private final static int XOFFSET = 4;

	private final GameWindowListener listener;

	public GameWindow(GameWindowListener listener) {
		this.listener = listener;
		addWindowListener(this);

		setSize(Const.DISPLAY_WIDTH, Const.DISPLAY_HEIGHT);
		setTitle(Const.APP_NAME);
		setVisible(true);
	}

	public void setColor(Color color) {
		offScreenGraphicsCtx.setColor(color);
	}

	public void setFont_(Font font) {
		offScreenGraphicsCtx.setFont(font);
	}

	public void drawString(String string, Vector2 position) {
		offScreenGraphicsCtx.drawString(string, (int) position.x, (int) position.y);
	}

	public void drawCircle(Vector2 position, int radius) {
		offScreenGraphicsCtx.drawOval((int) position.x - radius, (int) position.y - radius, radius * 2, radius * 2);
	}

	public void drawLine(Vector2 position, double angle, int length) {
		offScreenGraphicsCtx.drawLine((int) position.x, (int) position.y, (int) (position.x + Math.cos(angle) * length),
				(int) (position.y + Math.sin(angle) * length));
	}

	@Override
	public void update(Graphics g) {
		if (offScreenGraphicsCtx == null) {
			offScreenImage = createImage(getSize().width, getSize().height);
			offScreenGraphicsCtx = offScreenImage.getGraphics();
		}

		setColor(Const.BG_COLOR);
		offScreenGraphicsCtx.fillRect(0, 0, getSize().width, getSize().height);

		listener.render(this);

		// Draw the scene onto the screen
		if (offScreenImage != null) {
			g.drawImage(offScreenImage, 0, 0, this);
		}
	}

	@Override
	public void paint(Graphics g) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
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
