package models;

import java.util.Random;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.ranger.PlayerRangerData;

public class Wander {

	// Variables

	private JTextArea consola;
	private int speed = 0;

	// Usados por el player client
	private PlayerClient robot = null;
	private Position2DInterface motor = null;
	private RangerInterface ranger = null;

	// Servidor
	private String servidor = "localhost";
	private int puerto = 6665;

	// define the wall threshold
	static double MIN_WALL_THRESHOLD = 0.3;
	static double DEF_YAW_SPEED = 0.15;

	static double[] ranges;

	public Wander(String servidor, int puerto, int speed, JTextArea consola) {
		this.servidor = servidor;
		this.puerto = puerto;
		this.speed = speed;
		this.consola = consola;

		// Instanciando variables
		robot = new PlayerClient(servidor, puerto);
		ranger = robot.requestInterfaceRanger(0, PlayerConstants.PLAYER_OPEN_MODE);
		motor = robot.requestInterfacePosition2D(0, PlayerConstants.PLAYER_OPEN_MODE);

		// Encender el ranger y enceder el motor
		ranger.setRangerPower(1);
		motor.setMotorPower(1);

		bucle();

	}

	public void bucle() {
		int cont = 0;
		while (true) {

			float giro, velocidad;
			double leftSide;
			cont++;

			// Leer todos los datos del robot
			robot.readAll();

			if (ranger.isDataReady() && cont % 1 == 0) {
				PlayerRangerData rangerData = ranger.getData();
				ranges = rangerData.getRanges();
				leftSide = Math.min(Math.min(ranges[0], ranges[1]), ranges[2]);

				if (ranges.length == 0) {
					continue;
				}

				// Imprimir datos del sonar
				// System.out.printf("[");
				// for (double f : ranges) {
				// System.out.printf("%.2f", f);
				// }
				// System.out.println("]");

				// Note las diferentes unidades para el Angulo en
				// estos dos accesores!
				// System.out.println(motor.getX() + " " + motor.getY() + " " + motor.getYaw());
				//
				// System.out.println(motor.getData().getPos().getPx() + " " +
				// motor.getData().getPos().getPy() + " "
				// + motor.getData().getPos().getPa());

				// if(cont % 50 == 0) {
				// String coordenadas = "\nCoordenadas del bot {X;Y}: ["+motor.getX() + " ; " +
				// motor.getY() + "]";
				// consola.append(coordenadas);
				// if(cont% 300 == 0) {
				// consola.setText(coordenadas);
				// }
				// }

				// SwingUtilities.invokeLater(new Runnable() {
				// @Override
				// public void run() {
				// String coordenadas = "\nCoordenadas del bot {X;Y}: ["+motor.getX() + " ; " +
				// motor.getY() + "]";
				// consola.append(coordenadas);
				// }
				// });

				
				
				if (leftSide < MIN_WALL_THRESHOLD) {
					// move slower at corners
					velocidad = speed / 2;
					giro = -35.0f * (float) Math.PI / 180.0f;
				}else {
					Random aleatorio = new Random();

					int numeroRandom = (20 + aleatorio.nextInt((40 + 1) - 20));

					// Simple codigo para evitar obstaculos
					if (ranges[0] + ranges[1] < ranges[6] + ranges[7])
						giro = numeroRandom * -1.0f * (float) Math.PI / 180.0f;
					else
						giro = numeroRandom * 1.0f * (float) Math.PI / 180.0f;

					if (ranges[3] < 0.5f)
						velocidad = 0.0f;

					else
						velocidad = speed / 2.3f;
				}
				

				motor.setSpeed(velocidad, giro);
				

			}
		}
	}

}
