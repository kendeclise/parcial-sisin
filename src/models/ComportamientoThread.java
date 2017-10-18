package models;

import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.JScrollBar;

import javaclient3.BlobfinderInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;
import javaclient3.structures.ranger.PlayerRangerData;
import views.FrmMain;

public class ComportamientoThread extends Thread {

	// Variables
	private String servidor;
	private int puerto;
	private boolean wander = false;
	private boolean wallFollower = false;
	private boolean blobfinder = false;
	private int aceleracion = 1;
	private FrmMain frmMain;
	private DecimalFormat formateador = new DecimalFormat("####.#####");
	private int blobCount;
	private double frontSide, leftSide;
	private double[] sonarValues;
	private double xSpeed, yawSpeed;

	// Interfaces
	private PlayerClient robot;
	private Position2DInterface motor;
	private RangerInterface ranger;
	private BlobfinderInterface blfi;

	// Configuraciones por defecto
	private final double MIN_DISTANCIA_PARED = 0.3;
	private final double MAX_DISTANCIA_PARED = 0.4;
	private final double DIAMETRO_RUEDA = 24.0;
	private final double DEF_YAW_SPEED = 0.15; // velocidad Angular
	private final double DEF_X_SPEED = 0.2; // velocidad Traslaci�n
	// Valores del sonar minimos
	private final double SONAR_MIN_VALUE = 0.2;
	private final double SONAR_MAX_VALUE = 5.0;

	public ComportamientoThread(String servidor, int puerto, FrmMain frmMain, boolean wander, boolean wallFollower,
			boolean blobfinder) {
		this.servidor = servidor;
		this.puerto = puerto;
		this.frmMain = frmMain;
		this.wander = wander;
		this.wallFollower = wallFollower;
		this.blobfinder = blobfinder;

		try {
			// Instanciando las interfaces
			robot = new PlayerClient(servidor, puerto);
			ranger = robot.requestInterfaceRanger(0, PlayerConstants.PLAYER_OPEN_MODE);
			motor = robot.requestInterfacePosition2D(0, PlayerConstants.PLAYER_OPEN_MODE);
			if (blobfinder)
				blfi = robot.requestInterfaceBlobfinder(0, PlayerConstants.PLAYER_OPEN_MODE);

			// Encender el ranger y enceder el motor
			ranger.setRangerPower(1);
			motor.setMotorPower(1);

			if (blobfinder || wallFollower)
				robot.runThreaded(-1, -1);

			if (wallFollower) {
				frmMain.getTxtConsola().setText("Buscando una pared cercana");
				getWall(motor, ranger);
				// frmMain.getTxtConsola().setText("");
			}

			// Inicio el hilo
			// start();
			if (wander) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (true) {
							// new Wall(robot,motor,ranger);
							comportamientoWander();
						}

					}
				}).start();
			}

			if (wallFollower) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (true) {
							//h new Wall(robot,motor,ranger);
							comportamientoWallFollower();
						}

					}
				}).start();
			}

		} catch (PlayerException e) {
			frmMain.getTxtConsola().setText("[PlayerClient]: Error de conexi�n al servidor: \"" + servidor + ":"
					+ puerto + "\"\nPlayerClient init: java.net.ConnectException: Tiempo de espera excedido");
			System.err.println("   [  " + e.toString() + "  ]");
		}

	}

	// M�todo override Run
	@Override
	public void run() {
		while (true) {

			if (blobfinder) {
				comportamientoBlobfinder();
			}

			if (wander) {
				comportamientoWander();
			}

			if (wallFollower) {
				comportamientoWallFollower();
				//new Wall(robot, motor, ranger);
			}
			if (!wallFollower && !blobfinder && !wander) {
				if (motor != null) {
					motor.setSpeed(0, 0);
				}

			}

		}
	}

	// M�todos

	public boolean comportamientoWander() {
		float giro, velocidad;

		// Leer todos los datos del robot
		robot.readAll();

		if (ranger.isDataReady()) {
			PlayerRangerData rangerData = ranger.getData();
			double[] ranges = rangerData.getRanges();

			if (ranges.length == 0) {
				return false;
			}

			Random aleatorio = new Random();

			int numeroRandom = (20 + aleatorio.nextInt((20 + 1) - 20));

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
			impresion();
		}
		return false;

	}

	public void impresion() {
		String coordenadas = "\n Coordenadas del bot {X;Y}: [" + formateador.format(motor.getX()) + " ; "
				+ formateador.format(motor.getY()) + "]";
		frmMain.getTxtConsola().append(coordenadas);

		JScrollBar vertical = frmMain.getScrollPane().getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}

	public void comportamientoWallFollower() {

		// get all SONAR values and perform the necessary adjustments
		getSonars(ranger);

		// by default, just move in front
		xSpeed = DEF_X_SPEED;
		yawSpeed = 0;

		// if we're getting too close to the wall with the front side...
		if (frontSide < MAX_DISTANCIA_PARED) {
			// back up a little bit if we're bumping in front
			xSpeed = -0.10f;
			yawSpeed = -DEF_YAW_SPEED * 4;
		} else
		// if we're getting too close to the wall with the left side...
		if (leftSide < MIN_DISTANCIA_PARED) {
			// move slower at corners
			xSpeed = DEF_X_SPEED / 2;
			yawSpeed = -DEF_YAW_SPEED;
		} else
		// if we're getting too far away from the wall with the left side...
		if (leftSide > MAX_DISTANCIA_PARED) {
			// move slower at corners
			xSpeed = DEF_X_SPEED / 2;
			yawSpeed = DEF_YAW_SPEED;
		}

		// Move the robot
		motor.setSpeed(xSpeed, yawSpeed);
		// System.out.println(
		// "Left side : [" + leftSide + "], xSpeed : [" + xSpeed + "], yawSpeed : [" +
		// yawSpeed + "]");
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

	}

	public void getWall(Position2DInterface posi, RangerInterface rngi) {
		double yawSpeed;
		// get all SONAR values and perform the necessary adjustments
		getSonars(rngi);

		// if the robot is in open space, go ahead until it "sees" the wall
		while ((leftSide > MAX_DISTANCIA_PARED) && (frontSide > MAX_DISTANCIA_PARED)) {
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

	public void comportamientoBlobfinder() {

		double[] sonarValues;
		double xspeed, yawspeed;
		double leftSide, rightSide;

		try {
			// Un peque�o delay para que pueda iniciar la data
			Thread.sleep(80);
		} catch (Exception e) {
		}

		while (!ranger.isDataReady())
			;// Sigue en un bucle hasta que la data de la intefaz del ranger no esté lista

		// Obteniendo todos los datos del SONAR
		sonarValues = ranger.getData().getRanges();

		// Leer y calcular el promedio de los valores de los sonares del lado izquierdo
		// y derecho
		leftSide = (sonarValues[1] + sonarValues[2]) / 2; // Lado izquierdo -> sonares 1 y 2 , adicional podrian usar
															// también el sonar 3, obviamente se dividiría entre 3 si
															// se agrega
		rightSide = (sonarValues[5] + sonarValues[6]) / 2; // Lado derecho -> sonares 5 y 6, adicional se puede agregar
															// el sonar 7

		leftSide = leftSide / 10;
		rightSide = rightSide / 10;

		// Calculando las velocidad translacional y rotacional
		xspeed = (leftSide + rightSide) / 2; // Velocidad Translación
		yawspeed = ((leftSide - rightSide) * (180 / Math.PI) / DIAMETRO_RUEDA); // Velocidad de rotación

		// si el camino está limpio(se encuentra en el rango límite con la pared)
		// en la izquierda o derecha usa los valores de {x,yaw}
		// para asignar la velocidad del bot
		if (((sonarValues[1] > MIN_DISTANCIA_PARED) && (sonarValues[2] > MIN_DISTANCIA_PARED)
				&& (sonarValues[3] > MIN_DISTANCIA_PARED))
				|| ((sonarValues[4] > MIN_DISTANCIA_PARED) && (sonarValues[5] > MIN_DISTANCIA_PARED)
						&& (sonarValues[6] > MIN_DISTANCIA_PARED))) {// Si no hay obstáculos se asigna la velocidad
			motor.setSpeed(xspeed, yawspeed);
		} else {// Si hay obstáculos (en el lado derecho o izquierdo) se rota
			if (sonarValues[0] < sonarValues[7]) {
				motor.setSpeed(0, -DEF_YAW_SPEED); // De derecha a izquierda
			} else {
				motor.setSpeed(0, DEF_YAW_SPEED); // De izquierda a derecha
			}
		}

		// Obtiene el número de blobs detectados
		while (!blfi.isDataReady())
			;// Sigue en un bucle hasta que la data de la intefaz de blobs no esté lista
		blobCount = blfi.getData().getBlobs_count();

		// Si encontró al menos 1 blob, recorremos los n blobs que obtuvimos
		if (blobCount > 0) {
			for (int i = 0; i < blobCount; i++) {
				// Marco/Capturo la zona(coordenadas) donde se encontró este blob y lo marco
				// para que no
				// Vuelva a buscar el bot más blobs
				PlayerBlobfinderBlob unblob = blfi.getData().getBlobs()[i];// Con este comando marco la zona
				// Obtengo los datos Coordenadas, datos de la izquierda y derecha, de arriba y
				// de abajo y el area para mostrar en la consola
				int x = unblob.getX();
				int y = unblob.getY();

				int left = unblob.getLeft();
				int right = unblob.getRight();

				int top = unblob.getTop();
				int bottom = unblob.getBottom();

				int area = unblob.getArea();

			}
		}

	}

	// Getters & Setters

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

	public PlayerClient getRobot() {
		return robot;
	}

	public void setRobot(PlayerClient robot) {
		this.robot = robot;
	}

	public Position2DInterface getMotor() {
		return motor;
	}

	public void setMotor(Position2DInterface motor) {
		this.motor = motor;
	}

	public RangerInterface getRanger() {
		return ranger;
	}

	public void setRanger(RangerInterface ranger) {
		this.ranger = ranger;
	}

	public int getAceleracion() {
		return aceleracion;
	}

	public void setAceleracion(int aceleracion) {
		this.aceleracion = aceleracion;
	}

	public FrmMain getFrmMain() {
		return frmMain;
	}

	public void setFrmMain(FrmMain frmMain) {
		this.frmMain = frmMain;
	}

}
