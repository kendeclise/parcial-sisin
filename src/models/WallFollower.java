package models;

import java.text.NumberFormat;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

public class WallFollower {

	private String servidor = "localhost";
	private int puerto = 6665;

	static NumberFormat fmt = NumberFormat.getInstance();

	private PlayerClient robot = null;
	private Position2DInterface posi = null;
	private RangerInterface rngi = null;

	// define minimum/maximum allowed values for the SONAR sensors
	static double SONAR_MIN_VALUE = 0.2;
	static double SONAR_MAX_VALUE = 5.0;

	// define the wall threshold
	static double MIN_WALL_THRESHOLD = 0.3;
	static double MAX_WALL_THRESHOLD = 1.0;

	// define the default translational and rotational speeds
	static double xSpeed, yawSpeed;
	static double DEF_X_SPEED = 0.2;
	static double DEF_YAW_SPEED = -35.0f * (float) Math.PI / 180.0f;

	// array to hold the SONAR sensor values
	static double[] sonarValues;
	static double frontSide, leftSide;

	public WallFollower(String servidor, int puerto, int speed) {

		this.servidor = servidor;
		this.puerto = puerto;

		DEF_X_SPEED = speed/3f;
		
		try {
			// Connect to the Player server and request access to Position and Sonar
			robot = new PlayerClient(servidor, puerto);
			posi = robot.requestInterfacePosition2D(0, PlayerConstants.PLAYER_OPEN_MODE);
			rngi = robot.requestInterfaceRanger(0, PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("WallFollowerExample: > Error connecting to Player: ");
			System.err.println(" [ " + e.toString() + " ]");
			// System.exit(1);
		}

		robot.runThreaded(-1, -1);

		// Go ahead and find a wall and align to it on the robot's left side
		getWall(posi, rngi);
		bucle();
	}

	public void bucle() {
		while (true) {
			// get all SONAR values and perform the necessary adjustments
			getSonars(rngi);

			// by default, just move in front
			xSpeed = DEF_X_SPEED;
			yawSpeed = 0;

			// if we're getting too close to the wall with the front side...
			if (frontSide < MAX_WALL_THRESHOLD) {
				// back up a little bit if we're bumping in front
				xSpeed = -0.10f;
				yawSpeed = -DEF_YAW_SPEED * 4;
			} else
			// if we're getting too close to the wall with the left side...
			if (leftSide < MIN_WALL_THRESHOLD) {
				// move slower at corners
				xSpeed = DEF_X_SPEED / 2;
				yawSpeed = -DEF_YAW_SPEED;
			} else
			// if we're getting too far away from the wall with the left side...
			if (leftSide > MAX_WALL_THRESHOLD) {
				// move slower at corners
				xSpeed = DEF_X_SPEED / 2;
				yawSpeed = DEF_YAW_SPEED;
			}

			// Move the robot
			posi.setSpeed(xSpeed, yawSpeed);
//			System.out.println(
//					"Left side : [" + leftSide + "], xSpeed : [" + xSpeed + "], yawSpeed : [" + yawSpeed + "]");
			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}

		}
	}

	public void getWall(Position2DInterface posi, RangerInterface rngi) {
		// get all SONAR values and perform the necessary adjustments
		getSonars(rngi);

		// if the robot is in open space, go ahead until it "sees" the wall
		while ((leftSide > MAX_WALL_THRESHOLD) && (frontSide > MAX_WALL_THRESHOLD)) {
			posi.setSpeed(DEF_X_SPEED, 0);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			getSonars(rngi);
		}

		double previousLeftSide = sonarValues[0];

		// rotate until we get a smaller value in sonar 0
		while (sonarValues[0] <= previousLeftSide) {
			previousLeftSide = sonarValues[0];

			// rotate more if we're almost bumping in front
			if (Math.min(leftSide, frontSide) == frontSide)
				yawSpeed = -DEF_YAW_SPEED * 3;
			else
				yawSpeed = -DEF_YAW_SPEED;

			// Move the robot
			posi.setSpeed(0, yawSpeed);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}

			getSonars(rngi);
		}
		posi.setSpeed(0, 0);
	}

	public void getSonars(RangerInterface rngi) {
		while (!rngi.isDataReady())
			;
		sonarValues = rngi.getData().getRanges();

		// ignore erroneous readings/keep interval [SONAR_MIN_VALUE; SONAR_MAX_VALUE]
		for (int i = 0; i < rngi.getData().getRanges_count(); i++)
			if (sonarValues[i] < SONAR_MIN_VALUE)
				sonarValues[i] = SONAR_MIN_VALUE;
			else if (sonarValues[i] > SONAR_MAX_VALUE)
				sonarValues[i] = SONAR_MAX_VALUE;

		leftSide = Math.min(Math.min(sonarValues[0], sonarValues[1]), sonarValues[2]);
		frontSide = Math.min(sonarValues[3], sonarValues[4]);
	}
}
