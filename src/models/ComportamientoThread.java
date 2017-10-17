package models;

import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.JScrollBar;

import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
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
	
	// Interfaces
	private PlayerClient robot;
	private Position2DInterface motor;
	private RangerInterface ranger;

	// Configuraciones
	private final double MIN_DISTANCIA_PARED = 0.3;
	private final double MAX_DISTANCIA_PARED = 1.0;

	public ComportamientoThread(String servidor, int puerto, FrmMain frmMain) {
		this.servidor = servidor;
		this.puerto = puerto;
		this.frmMain = frmMain;

		// Instanciando las interfaces
		robot = new PlayerClient(servidor, puerto);
		ranger = robot.requestInterfaceRanger(0, PlayerConstants.PLAYER_OPEN_MODE);
		motor = robot.requestInterfacePosition2D(0, PlayerConstants.PLAYER_OPEN_MODE);

		// Encender el ranger y enceder el motor
		ranger.setRangerPower(1);
		motor.setMotorPower(1);

		// Inicio el hilo
		start();
	}

	// Método override Run
	@Override
	public void run() {
		while (true) {

			if (wallFollower) {
				comportamientoWallFollower();
			}
			if (blobfinder) {
				comportamientoBlobfinder();
			}
			if (wander) {
				comportamientoWander();
			}
			if(!wallFollower && !blobfinder && !wander) {
				if(motor != null)
				motor.setSpeed(0,0);
			}

		}
	}

	// Métodos

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
				velocidad = 1.5f*aceleracion;
			}

			motor.setSpeed(velocidad, giro);
			impresion();
		}
		return false;

	}
	
	public void impresion() {
		String coordenadas = "\n Coordenadas del bot {X;Y}: ["+ formateador.format(motor.getX()) + " ; " +
				 formateador.format(motor.getY()) + "]";
		frmMain.getTxtConsola().append(coordenadas);
		
		JScrollBar vertical = frmMain.getScrollPane().getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}

	public void comportamientoWallFollower() {
		System.out.println("wallFollower");
	}

	public void comportamientoBlobfinder() {
		System.out.println("blobfinder");
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
