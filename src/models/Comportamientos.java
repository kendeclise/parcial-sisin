package models;

import javaclient3.BlobfinderInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;

public class Comportamientos {

	// Variables

	// Usados por el player client
	private PlayerClient robot = null;
	private Position2DInterface posi = null;
	private RangerInterface rngi = null;
	private BlobfinderInterface blfi = null;

	// Parametros configurables

	// Servidor
	private String servidor;
	private int puerto;

	// Parametro de alerta del SONAR (limita cuan cerca el robot puede estar de la pared)
	private double SONAR_THRESHOLD = 0.5;

	// Diametro de la rueda(el mismo del robot pioneer 3)
	private double WHEEL_DIAMETER = 24.0;

	// Define la velocidad rotacional por defecto en radianes
	private double DEF_YAW_SPEED = 0.30;
	private double DEF_X_SPEED = 0.50;

	// Arreglo que almacena los valores del sensor SONAR
	private double[] sonarValues;

	// Velocidad translacional/rotacional
	private double xspeed, yawspeed;
	private double leftSide, rightSide;

	// Numero de blobs(gotas) encontrados
	private int blobCount;

	// Constructor
	public Comportamientos(String servidor, int puerto) {

		this.servidor = servidor;
		this.puerto = puerto;

		try {
			// Instanciando las variables del robot, sus posiciones(motor), el ranger y el
			// blobfinder
			robot = new PlayerClient(servidor, puerto);
			posi = robot.requestInterfacePosition2D(0, PlayerConstants.PLAYER_OPEN_MODE);
			rngi = robot.requestInterfaceRanger(0, PlayerConstants.PLAYER_OPEN_MODE);
			blfi = robot.requestInterfaceBlobfinder(0, PlayerConstants.PLAYER_OPEN_MODE);

		} catch (PlayerException e) {
			System.err.println("BlobfinderExample: > Error conectando al \"PLAYER\"");
			System.err.println("   [  " + e.toString() + "  ]");
			// System.exit(1);
		}

		robot.runThreaded(-1, -1);		
		Blobfinder();
	}

	// Funciones

	public void Blobfinder() {
	
		//Bucle infinito
				while(true){
					
					try{
						//Un peque�o delay
						Thread.sleep(200);
					} catch(Exception e){}
					
					while(!rngi.isDataReady());//Sigue en un bucle hasta que la data de la intefaz del ranger no este lista
				
					//Obteniendo todos los datos del SONAR
					sonarValues = rngi.getData().getRanges();
					
					//Leer y calcular el promedio de los valores de los sonares del lado izquierdo y derecho
					leftSide = (sonarValues[1] + sonarValues[2]) / 2; //Lado izquierdo -> sonares 1 y 2 , adicional podrian usar tambien el sonar 3, obviamente se dividiría entre 3 si se agrega
					rightSide = (sonarValues[5] + sonarValues[6]) /2; //Lado derecho -> sonares 5 y 6, adicional se puede agregar el sonar 7
				
					leftSide = leftSide / 10;
					rightSide = rightSide / 10;
					
					// Calculando las velocidad translacional y rotacional
					xspeed = (leftSide + rightSide)/2; //Velocidad Translacion
					yawspeed = ((leftSide-rightSide) * (180/Math.PI)/WHEEL_DIAMETER); //Velocidad de rotacion
				
					
						
					//si el camino esta limpio(se encuentra en el rango limite con la pared)
					//en la izquierda o derecha usa los valores de {x,yaw}
					//para asignar la velocidad del bot
					if(((sonarValues[1] > SONAR_THRESHOLD) &&
						(sonarValues[2] > SONAR_THRESHOLD) &&
						(sonarValues[3] > SONAR_THRESHOLD)) ||
					   ((sonarValues[4] > SONAR_THRESHOLD) &&
						(sonarValues[5] > SONAR_THRESHOLD) &&
						(sonarValues[6] > SONAR_THRESHOLD)))
					{//Si no hay obstaculos se asigna la velocidad
						posi.setSpeed(xspeed, yawspeed);
					}else{//Si hay obstaculos (en el lado derecho o izquierdo) se rota
						if(sonarValues[0] < sonarValues[7]){
							posi.setSpeed(0, -DEF_YAW_SPEED); // De derecha a izquierda
						}else{
							posi.setSpeed(0, DEF_YAW_SPEED); //De izquierda a derecha
						}
					}
					
					//Obtiene el número de blobs detectados
					while(!blfi.isDataReady());//Sigue en un bucle hasta que la data de la intefaz de blobs no esta lista
					blobCount = blfi.getData().getBlobs_count();
					
					//Si encontró al menos 1 blob, recorremos los n blobs que obtuvimos
					if(blobCount > 0){
						for(int i=0; i<blobCount; i++){
							//Marco/Capturo la zona(coordenadas) donde se encontro este blob y lo marco para que no
							//Vuelva a buscar el bot más blobs
							PlayerBlobfinderBlob unblob = blfi.getData().getBlobs()[i];//Con este comando marco la zona
							//Obtengo los datos Coordenadas, datos de la izquierda y derecha, de arriba y
							//de abajo y el area para mostrar en la consola
							int x = unblob.getX();
							int y = unblob.getY();
							
							int left = unblob.getLeft();
							int right = unblob.getRight();
							
							int top = unblob.getTop();
							int bottom = unblob.getBottom();
							
							int area = unblob.getArea();
							
							System.out.println("Blog ["+i+"], tiene un Area: ["+area+
									"] coordenadas del blob: ["+right+", "+top+"] -> "+
									"["+left+","+bottom+"]"+
									" con un centro en: ["+x+","+y+"]");
							
							
						}
					}
				}
	}

	// Getters y Setters

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

}
