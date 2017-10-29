package models;

import java.text.NumberFormat;
import java.util.Random;

import javaclient3.BlobfinderInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;
import javaclient3.structures.ranger.PlayerRangerData;

public class Comportamiento {

	// Variables
	private String servidor;
	private int puerto;

	// Comportamientos activos
	private boolean wander = false;
	private boolean wallFollower = false;
	private boolean blobfinder = false;
	
	public static void main(String[] args) {
		String servidor = args[0];
		int puerto = Integer.parseInt(args[1]);
		boolean wander = Boolean.parseBoolean(args[2]);
		boolean wallFollower = Boolean.parseBoolean(args[3]);
		boolean blobfinder = Boolean.parseBoolean(args[4]);
		
		new Comportamiento(servidor, puerto, wander, wallFollower, blobfinder);		
		
		
	}

	// Constructor
	public Comportamiento(String servidor, int puerto, boolean wander, boolean wallFollower, boolean blobfinder) {

		// Instanciando las variables

		this.servidor = servidor;
		this.puerto = puerto;

		this.wander = wander;
		this.wallFollower = wallFollower;
		this.blobfinder = blobfinder;

		bucle();

	}

	//Métodos
	
	// Bucle
	public void bucle() {

		

		if (wallFollower) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					new CWallFollower(puerto, servidor, wander, blobfinder);

				}
			}).start();
		}else {
			if (wander) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						new CWander(puerto, servidor);
					}
				}).start();
			}
			
			if (blobfinder) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						new CBlobfinder(puerto, servidor);
					}
				}).start();
			}
			
		}

		

	}
	


	// Getters && Setters

	public String getServidor() {
		return servidor;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public boolean isWander() {
		return wander;
	}

	public void setWander(boolean wander) {
		this.wander = wander;
	}

	public boolean isWallFollower() {
		return wallFollower;
	}

	public void setWallFollower(boolean wallFollower) {
		this.wallFollower = wallFollower;
	}

	public boolean isBlobfinder() {
		return blobfinder;
	}

	public void setBlobfinder(boolean blobfinder) {
		this.blobfinder = blobfinder;
	}

}

/******************************************************************************************************************************************************
 * - COMPORTAMIENTO WANDER
 ******************************************************************************************************************************************************/

class CWander {

	private int puerto = 6665;
	private String servidor = "localhost";

	public CWander(int puerto, String servidor) {

		this.puerto = puerto;
		this.servidor = servidor;

		PlayerClient robot = new PlayerClient(servidor, puerto);
		RangerInterface ranger = robot.requestInterfaceRanger(0, PlayerConstants.PLAYER_OPEN_MODE);

		Position2DInterface motor = robot.requestInterfacePosition2D(0, PlayerConstants.PLAYER_OPEN_MODE);

		// Encender el ranger y enceder el motor
		ranger.setRangerPower(1);
		motor.setMotorPower(1);

		while (true) {
			float giro, velocidad;

			// Leer todos los datos del robot
			robot.readAll();

			if (ranger.isDataReady()) {
				PlayerRangerData rangerData = ranger.getData();
				double[] ranges = rangerData.getRanges();

				if (ranges.length == 0) {
					continue;
				}

				Random aleatorio = new Random();

				int numeroRandom = (20 + aleatorio.nextInt((70 + 1) - 20));

				if (ranges[0] + ranges[1] < ranges[6] + ranges[7])
					giro = numeroRandom * -1.0f * (float) Math.PI / 180.0f;
				else
					giro = numeroRandom * 1.0f * (float) Math.PI / 180.0f;

				if (ranges[3] < 0.5f)
					velocidad = 0.0f;

				else {
					velocidad = 1.5f;
				}

				motor.setSpeed(velocidad, giro);

			}
		}

	}

}

/******************************************************************************************************************************************************
 * - COMPORTAMIENTO WALLFOLLOWER
 ******************************************************************************************************************************************************/

class CWallFollower {

	private int puerto = 6665;
	private String servidor = "localhost";

	// define minimum/maximum allowed values for the SONAR sensors
	static double SONAR_MIN_VALUE = 0.2;
	static double SONAR_MAX_VALUE = 5.0;

	// define the wall threshold
	static double MIN_WALL_THRESHOLD = 0.3;
	static double MAX_WALL_THRESHOLD = 0.4;

	// define the default translational and rotational speeds
	static double xSpeed, yawSpeed;
	static double DEF_X_SPEED = 0.2;
	static double DEF_YAW_SPEED = 0.15;

	// array to hold the SONAR sensor values
	static double[] sonarValues;
	static double frontSide, leftSide;

	public CWallFollower(int puerto, String servidor, boolean wander, boolean blobfinder) {
		this.puerto = puerto;
		this.servidor = servidor;

		PlayerClient robot = null;
		Position2DInterface posi = null;
		RangerInterface rngi = null;

		try {
			// Connect to the Player server and request access to Position and Sonar
			robot = new PlayerClient(servidor, puerto);
			posi = robot.requestInterfacePosition2D(0, PlayerConstants.PLAYER_OPEN_MODE);
			rngi = robot.requestInterfaceRanger(0, PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("WallFollowerExample: > Error connecting to Player: ");
			System.err.println(" [ " + e.toString() + " ]");
			System.exit(1);
		}

		robot.runThreaded(-1, -1);

		// Go ahead and find a wall and align to it on the robot's left side
		getWall(posi, rngi);
		
		
		if (wander) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					new CWander(puerto, servidor);
				}
			}).start();
		}


		if (blobfinder) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					new CBlobfinder(puerto, servidor);
				}
			}).start();
		}
		

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
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}

		}

	}

	static void getWall(Position2DInterface posi, RangerInterface rngi) {
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

	static void getSonars(RangerInterface rngi) {
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

/******************************************************************************************************************************************************
 * - COMPORTAMIENTO BLOBFINDER
 ******************************************************************************************************************************************************/
class CBlobfinder {
    
	private int puerto = 6665;
	private String servidor = "localhost";
	
    // define the threshold (any value under this is considered an obstacle)
    static double SONAR_THRESHOLD = 0.5;
    // define the wheel diameter (~example for a Pioneer 3 robot)
    static double WHEEL_DIAMETER  = 24.0;
    
    // define the default rotational speed in rad/s
    static double DEF_YAW_SPEED   = 0.30;
    static double DEF_X_SPEED     = 0.50;
    
    // array to hold the SONAR sensor values
    static double[] sonarValues;
    // translational/rotational speed
    static double xspeed, yawspeed;
    static double leftSide, rightSide;
    
    // the number of blobs found
    static int blobCount;
    
    
    public CBlobfinder(int puerto, String servidor) {
    	this.puerto = puerto;
    	this.servidor = servidor;
    	
    	PlayerClient        robot = null;
        Position2DInterface posi  = null;
        RangerInterface     rngi  = null;
        BlobfinderInterface blfi  = null;
        
        try {
            // Connect to the Player server
            robot  = new PlayerClient (servidor, puerto);
            posi = robot.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
            rngi = robot.requestInterfaceRanger     (0, PlayerConstants.PLAYER_OPEN_MODE);
            blfi = robot.requestInterfaceBlobfinder (0, PlayerConstants.PLAYER_OPEN_MODE);
        } catch (PlayerException e) {
            System.err.println ("BlobfinderExample: > Error connecting to Player: ");
            System.err.println ("    [ " + e.toString() + " ]");
            System.exit (1);
        }
        
        robot.runThreaded (-1, -1);
        
        while (true) {
        	 try { Thread.sleep (200); } catch (Exception e) { }
             
            while (!rngi.isDataReady ());
            // get all SONAR values
            sonarValues = rngi.getData ().getRanges ();
            
            // read and average the sonar values on the left and right side
            leftSide  = (sonarValues [1] + sonarValues [2]) / 2; // + sonarValues [3]) / 3;
            rightSide = (sonarValues [5] + sonarValues [6]) / 2; // + sonarValues [4]) / 3;
            
            leftSide = leftSide / 10;
            rightSide = rightSide / 10;
            
            // calculate the translational and rotational velocities
            xspeed = (leftSide + rightSide) / 2;
            yawspeed = ((leftSide - rightSide) * (180 / Math.PI) / WHEEL_DIAMETER);
            
           
            // if the path is clear on the left OR on the right, use {x,yaw}speed
            if (((sonarValues [1] > SONAR_THRESHOLD) && 
                 (sonarValues [2] > SONAR_THRESHOLD) && 
                 (sonarValues [3] > SONAR_THRESHOLD))  ||
                ((sonarValues [4] > SONAR_THRESHOLD) && 
                 (sonarValues [5] > SONAR_THRESHOLD) && 
                 (sonarValues [6] > SONAR_THRESHOLD) 
                ))
                posi.setSpeed (xspeed, yawspeed);
            else
                // if we have obstacles in front (both left and right), rotate
                if (sonarValues [0] < sonarValues [7])
                    posi.setSpeed (0, -DEF_YAW_SPEED);    
                else
                    posi.setSpeed (0, DEF_YAW_SPEED);
            
            // get the number of blobs detected
            while (!blfi.isDataReady ());
            blobCount = blfi.getData ().getBlobs_count ();
            
            if (blobCount > 0)
                for (int i = 0; i < blobCount; i++) {
                    PlayerBlobfinderBlob unblob = blfi.getData ().getBlobs ()[i];
                    
                    int x = unblob.getX (); 
                    int y = unblob.getY ();
                    
                    int left  = unblob.getLeft  (); 
                    int right = unblob.getRight ();
                    
                    int top    = unblob.getTop    (); 
                    int bottom = unblob.getBottom ();
                    
                    int area   = unblob.getArea   ();
                    
                }
        }
    	
    	
    }
    
}

