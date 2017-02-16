package GBall.engine;

import java.awt.Color;
import java.awt.Font;

public final class Const {
	// World-related constants
	public final static double TARGET_FPS = 50;
	public final static double FRAME_INCREMENT = 1000 / TARGET_FPS;
	public final static String APP_NAME = "Geometry Ball Tournament 2014";
	public final static int DISPLAY_WIDTH = 1024;
	public final static int DISPLAY_HEIGHT = 758;
	public final static int WINDOW_TOP_HEIGHT = 30;
	public final static int WINDOW_BORDER_WIDTH = 5;
	public final static int WINDOW_BOTTOM_HEIGHT = 5;
	public final static Color BG_COLOR = Color.BLACK;
	public final static int FONT_SIZE = 24;

	public final static boolean SHOW_FPS = true;
	public final static Color FPS_TEXT_COLOR = Color.WHITE;
	public final static Vector2 FPS_TEXT_POSITION = new Vector2(10.0, 50.0);

	public final static Vector2 TEAM1_SCORE_TEXT_POSITION = new Vector2((double) (DISPLAY_WIDTH / 2) - 120, 52.0);
	public final static Vector2 TEAM2_SCORE_TEXT_POSITION = new Vector2((double) (DISPLAY_WIDTH / 2) + 120, 52.0);
	public final static Font SCORE_FONT = new Font("Times New Roman", Font.BOLD, FONT_SIZE);

	public final static Color TEAM1_COLOR = Color.RED;
	public final static Color TEAM2_COLOR = Color.GREEN;

	public final static double START_TEAM1_SHIP1_X = 200.0;
	public final static double START_TEAM1_SHIP1_Y = 100.0;
	public final static double START_TEAM1_SHIP2_X = START_TEAM1_SHIP1_X;
	public final static double START_TEAM1_SHIP2_Y = DISPLAY_HEIGHT - START_TEAM1_SHIP1_Y;
	public final static double START_TEAM2_SHIP1_X = DISPLAY_WIDTH - START_TEAM1_SHIP1_X;
	public final static double START_TEAM2_SHIP1_Y = START_TEAM1_SHIP1_Y;
	public final static double START_TEAM2_SHIP2_X = START_TEAM2_SHIP1_X;
	public final static double START_TEAM2_SHIP2_Y = DISPLAY_HEIGHT - START_TEAM2_SHIP1_Y;

	public final static double BALL_X = DISPLAY_WIDTH / 2;
	public final static double BALL_Y = DISPLAY_HEIGHT / 2;

	// Ship-related constants
	public final static int SHIP_RADIUS = 22;
	public final static double SHIP_MAX_ACCELERATION = 400.0;
	public final static double SHIP_MAX_SPEED = 370.0;
	public final static double SHIP_BRAKE_SCALE = 0.978; // Scale speed by this factor
													// (per frame) when braking
	public final static double SHIP_TURN_BRAKE_SCALE = 0.99; // Scale speed by this
														// factor (per frame)
														// when turning
	public final static double SHIP_FRICTION = 0.99; // Scale speed by this factor (per
												// frame) when not accelerating
	public final static double SHIP_ROTATION = 0.067 * 50.0; // Rotate ship by this many
												// radians (per frame) when
												// turning

	// Ball-related constants
	public final static int BALL_RADIUS = 18;
	public final static double BALL_MAX_ACCELERATION = 400.0;
	public final static double BALL_MAX_SPEED = 370.0;
	public final static double BALL_FRICTION = 0.992;
	public final static Color BALL_COLOR = Color.WHITE;
}
