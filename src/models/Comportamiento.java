package models;

import java.text.NumberFormat;
import java.util.Random;

import javaclient3.BlobfinderInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.ranger.PlayerRangerData;

public class Comportamiento {

	// Variables

	// Condiciones
	static Boolean wander = false;
	static Boolean wallFollower = false;
	static Boolean blobFinder = false;
	static Boolean encendido = false;

	// Usados por el player client
	private PlayerClient robot = null;
	private Position2DInterface posi = null;
	private RangerInterface rngi = null;

	// Especial para el comportamiento blobfinder
	private BlobfinderInterface blfi = null;

	// Parametros configurables

	// Servidor
	private String servidor = "localhost";
	private int puerto = 6665;

	// Parametro de alerta del SONAR (limita cuan cerca el robot puede estar de la
	// pared)
	private double SONAR_THRESHOLD = 0.5;

	static double SONAR_MIN_VALUE = 0.2;
	static double SONAR_MAX_VALUE = 5.0;
	static double MIN_WALL_THRESHOLD = 0.5;
	static double MAX_WALL_THRESHOLD = 0.8;

	// Diametro de la rueda(el mismo del robot pioneer 3)
	private double WHEEL_DIAMETER = 24.0;

	// Define la velocidad rotacional por defecto en radianes
	private double DEF_YAW_SPEED = 0.20;
	private double DEF_X_SPEED = 0.15;

	// Arreglo que almacena los valores del sensor SONAR
	static double[] sonarValues;

	// Velocidad translacional/rotacional
	private double xspeed, yawspeed;
	private double leftSide, rightSide, frontSide;

	// Numero de blobs(gotas) encontrados
	private int blobCount;

	// Adicionales
	static NumberFormat fmt = NumberFormat.getInstance();

	public Comportamiento(String server, int port, Boolean wander, Boolean wallFollower, Boolean blobFinder) {
		this.servidor = server;
		this.puerto = port;
		this.wander = wander;
		this.wallFollower = wallFollower;
		this.blobFinder = blobFinder;

		encendido = true;

		// DEF_X_SPEED = speed;

		try {

			robot = new PlayerClient(servidor, puerto);
			posi = robot.requestInterfacePosition2D(0, PlayerConstants.PLAYER_OPEN_MODE);
			rngi = robot.requestInterfaceRanger(0, PlayerConstants.PLAYER_OPEN_MODE);

			if (blobFinder) {
				blfi = robot.requestInterfaceBlobfinder(0, PlayerConstants.PLAYER_OPEN_MODE);
			}

			robot.runThreaded(-1, -1);
			rngi.setRangerPower(1);
			posi.setMotorPower(1);

			bucle();

		} catch (PlayerException e) {
			System.err.println("Proyecto Sisin: > Error al conectarse al servidor: ");
			System.err.println(" [ " + e.toString() + " ]");

		}

	}

	public void bucle() {
		while (true) {

			/*******************************************************/
			if (wallFollower) {

				if (encendido) {
					getWall(posi, rngi);
				}

				getSonars(rngi);

				// Por defecto se mueve de frente (angulo de giro 0)
				xspeed = DEF_X_SPEED;
				yawspeed = 0;

				// Si esta por debajo de la distancia maxima por el frente
				if (frontSide < MAX_WALL_THRESHOLD) {
					// retrocede un poco
					xspeed = -0.10f;
					yawspeed = -DEF_YAW_SPEED * 4;
				} else
				// Si esta por debajo de la distancia mínima por la izquierda
				if (leftSide < MIN_WALL_THRESHOLD) {
					// se mueve lento por las esquinas
					xspeed = DEF_X_SPEED / 2;
					yawspeed = -DEF_YAW_SPEED;
				} else
				// Si esta por encima de la distancia maxima por la izquierda
				if (leftSide > MAX_WALL_THRESHOLD) {
					// se mueve lento por las esquinas
					xspeed = DEF_X_SPEED / 2;
					yawspeed = DEF_YAW_SPEED;
				}
				
				try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
				
				if(wander) {
					Random aleatorio = new Random();
					int numeroRandom = (20 + aleatorio.nextInt((45 + 1) - 20));
					
					if (sonarValues[0] + sonarValues[1] + sonarValues[2] < sonarValues[6] + sonarValues[7] + sonarValues[8])
						yawspeed = numeroRandom*-1.0f * (float) Math.PI / 180.0f;
					else
						yawspeed = numeroRandom*1.0f * (float) Math.PI / 180.0f;

					if (sonarValues[3] + sonarValues[4] + sonarValues[5] < 0.5f)
						xspeed = 0.0f;

					else {
						xspeed = DEF_X_SPEED;
					}
				}
				

				// Mueve el robot
				posi.setSpeed(xspeed, yawspeed);
				// System.out.println(
				// "Left side : [" + leftSide + "], xSpeed : [" + xspeed + "], yawSpeed : [" +
				// yawspeed + "]");
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}

			}
			/*******************************************************/

			if (wander && !wallFollower) {

				robot.readAll();

				if (rngi.isDataReady()) {

					PlayerRangerData rangerData = rngi.getData();
					sonarValues = rangerData.getRanges();

					if (sonarValues.length == 0) {
						continue;
					}

					// Imprimir datos del sonar
					// System.out.printf("[");
					// for (double f: ranges){
					// System.out.printf("%.2f : ",f);
					// }
					// System.out.println("]");

					Random aleatorio = new Random();

					int numeroRandom = (20 + aleatorio.nextInt((45 + 1) - 20));

					if (sonarValues[0] + sonarValues[1] + sonarValues[2] < sonarValues[6] + sonarValues[7] + sonarValues[8])
						yawspeed = numeroRandom*-1.0f * (float) Math.PI / 180.0f;
					else
						yawspeed = numeroRandom*1.0f * (float) Math.PI / 180.0f;

					if (sonarValues[3] + sonarValues[4] + sonarValues[5] < 0.5f)
						xspeed = 0.0f;

					else {
						xspeed = DEF_X_SPEED;
					}

					posi.setSpeed(xspeed, yawspeed);

				}

			}

			encendido = false;

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
				yawspeed = -DEF_YAW_SPEED * 3;
			else
				yawspeed = -DEF_YAW_SPEED;

			// Move the robot
			posi.setSpeed(0, yawspeed);
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
