package GBall.engine;

import java.awt.Color;
import java.awt.Font;

public final class Const {
	// World-related constants
	public final static double TARGET_FPS = 50;
	public final static long FRAME_INCREMENT = (long) (1000.0 / TARGET_FPS);
	public final static double DT = FRAME_INCREMENT / 1000.0;

	public final static long LOCAL_DELAY = 100L / FRAME_INCREMENT;

	public final static long PERIODIC_STATES = 10000L / FRAME_INCREMENT;

	public final static String APP_NAME = "Rocket League f�r hemmafruar";
	public final static int DISPLAY_WIDTH = 1024;
	public final static int DISPLAY_HEIGHT = 758;
	public final static Color BG_COLOR = Color.BLACK;
	public final static int FONT_SIZE = 24;

	public final static Color TEXT_COLOR = Color.WHITE;
	public final static Vector2 FPS_TEXT_POSITION = new Vector2(10.0, 50.0);

	public final static Vector2 TEAM1_SCORE_TEXT_POSITION = new Vector2((double) (DISPLAY_WIDTH / 2) - 120, 22.0);
	public final static Vector2 TEAM2_SCORE_TEXT_POSITION = new Vector2((double) (DISPLAY_WIDTH / 2) + 120, 22.0);
	public final static Font SCORE_FONT = new Font("Times New Roman", Font.BOLD, FONT_SIZE);
	public final static Font DEBUG_FONT = new Font("Arial", Font.BOLD, 12);
	public final static Vector2 DEBUG_TEXT_POSITION = new Vector2(13.0, 13.0);

	public final static int GOAL_WIDTH = 10;
	public final static int TEAM1_GOAL_POSITION = 0;
	public final static int TEAM2_GOAL_POSITION = DISPLAY_WIDTH - GOAL_WIDTH;

	public final static Color TEAM1_COLOR = Color.RED;
	public final static Color TEAM2_COLOR = Color.GREEN;
	
	public final static double START_FROM_EDGE = 100.0;

	public final static double START_TEAM1_SHIP1_X = START_FROM_EDGE;
	public final static double START_TEAM1_SHIP1_Y = START_FROM_EDGE;
	public final static double START_TEAM1_SHIP2_X = START_FROM_EDGE;
	public final static double START_TEAM1_SHIP2_Y = DISPLAY_HEIGHT - START_FROM_EDGE;
	public final static double START_TEAM2_SHIP1_X = DISPLAY_WIDTH - START_FROM_EDGE;
	public final static double START_TEAM2_SHIP1_Y = START_FROM_EDGE;
	public final static double START_TEAM2_SHIP2_X = DISPLAY_WIDTH - START_FROM_EDGE;
	public final static double START_TEAM2_SHIP2_Y = DISPLAY_HEIGHT - START_FROM_EDGE;

	public final static double BALL_X = DISPLAY_WIDTH / 2;
	public final static double BALL_Y = DISPLAY_HEIGHT / 2;

	// Ship-related constants
	public final static int SHIP_RADIUS = 22;
	public final static double SHIP_MAX_ACCELERATION = 400.0;
	public final static double SHIP_MAX_SPEED = 370.0;
	public final static double SHIP_BRAKE_SCALE = 0.978;
	// Scale speed by this factor (per frame) when braking

	public final static double SHIP_TURN_BRAKE_SCALE = 0.99;
	// Scale speed by this factor (per frame) when turning

	public final static double SHIP_FRICTION = 0.99;
	// Scale speed by this factor (per frame) when not accelerating

	public final static double SHIP_ROTATION = 0.067 * 50.0;
	// Rotate ship by this many radians (per frame) when turning

	// Ball-related constants
	public final static int BALL_RADIUS = 18;
	public final static double BALL_MAX_ACCELERATION = 400.0;
	public final static double BALL_MAX_SPEED = 370.0;
	public final static double BALL_FRICTION = 0.992;
	public final static Color BALL_COLOR = Color.WHITE;

	// Events and states
	public final static long OUTDATED_THRESHOLD = 10000L / FRAME_INCREMENT;
}
