package controllers;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.JOptionPane;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.ranger.PlayerRangerData;
import views.FrmMain;


public class CtrMain {

	// Variables
	private FrmMain frmPanelControl;
	private String servidor;
	private int puerto;
	
	//Variables de wallFollower
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

	public CtrMain() {
		// Instanciando el Jframe
		if (frmPanelControl == null) {
			frmPanelControl = new FrmMain();
		}

		// Agregando items al combo
		frmPanelControl.getCboPuerto().addItem(6101);
		frmPanelControl.getCboPuerto().addItem(6102);
		frmPanelControl.getCboPuerto().addItem(6103);
		frmPanelControl.getCboPuerto().addItem(6104);
		frmPanelControl.getCboPuerto().addItem(6665);

		// Limitando los caracteres de la direcci�n IP
		frmPanelControl.getTxtServidor1().addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {

				if (frmPanelControl.getTxtServidor1().getText().length() >= 4) {

					e.consume();

				}
				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {

					e.consume();
				}

			}

		});

		frmPanelControl.getTxtServidor2().addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {
				if (frmPanelControl.getTxtServidor2().getText().length() >= 4)
					e.consume();

				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {

					e.consume();
				}
			}

		});

		frmPanelControl.getTxtServidor3().addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {
				if (frmPanelControl.getTxtServidor3().getText().length() >= 4)
					e.consume();

				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {

					e.consume();
				}
			}

		});

		frmPanelControl.getTxtServidor4().addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {
				if (frmPanelControl.getTxtServidor4().getText().length() >= 4)
					e.consume();

				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {

					e.consume();
				}
			}

		});

		// Cuando se pierde y gana el foco (TextField de la ip del servidor)
		frmPanelControl.getTxtServidor1().addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				frmPanelControl.getTxtServidor1().select(0, 0);
				// Valido que sea un n�mero
				try {
					int numero = Integer.parseInt(frmPanelControl.getTxtServidor1().getText());
					if (numero <= 0) {
						frmPanelControl.getTxtServidor1().setText("0");
					}
					if (numero > 255) {
						frmPanelControl.getTxtServidor1().setText("255");
					}
				} catch (Exception err) {
					frmPanelControl.getTxtServidor1().setText("1");
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				frmPanelControl.getTxtServidor1().select(0, frmPanelControl.getTxtServidor1().getText().length());
			}
		});

		frmPanelControl.getTxtServidor2().addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				frmPanelControl.getTxtServidor2().select(0, 0);
				// Valido que sea un n�mero
				try {
					int numero = Integer.parseInt(frmPanelControl.getTxtServidor2().getText());
					if (numero < 0) {
						frmPanelControl.getTxtServidor2().setText("0");
					}
					if (numero > 255) {
						frmPanelControl.getTxtServidor2().setText("255");
					}
				} catch (Exception err) {
					frmPanelControl.getTxtServidor2().setText("0");
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				frmPanelControl.getTxtServidor2().select(0, frmPanelControl.getTxtServidor2().getText().length());
			}
		});

		frmPanelControl.getTxtServidor3().addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				frmPanelControl.getTxtServidor3().select(0, 0);
				// Valido que sea un n�mero
				try {
					int numero = Integer.parseInt(frmPanelControl.getTxtServidor3().getText());
					if (numero < 0) {
						frmPanelControl.getTxtServidor3().setText("0");
					}
					if (numero > 255) {
						frmPanelControl.getTxtServidor3().setText("254");
					}
				} catch (Exception err) {
					frmPanelControl.getTxtServidor3().setText("0");
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				frmPanelControl.getTxtServidor3().select(0, frmPanelControl.getTxtServidor3().getText().length());
			}
		});

		frmPanelControl.getTxtServidor4().addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				frmPanelControl.getTxtServidor4().select(0, 0);
				// Valido que sea un n�mero
				try {
					int numero = Integer.parseInt(frmPanelControl.getTxtServidor4().getText());
					if (numero < 0) {
						frmPanelControl.getTxtServidor4().setText("1");
					}
					if (numero > 255) {
						frmPanelControl.getTxtServidor4().setText("255");
					}
				} catch (Exception err) {
					frmPanelControl.getTxtServidor4().setText("1");
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				frmPanelControl.getTxtServidor4().select(0, frmPanelControl.getTxtServidor4().getText().length());
			}
		});

		// Acci�n del bot�n Wander
		frmPanelControl.getBtnWander().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				servidor = frmPanelControl.getTxtServidor1().getText() + "."
						+ frmPanelControl.getTxtServidor2().getText() + "."
						+ frmPanelControl.getTxtServidor3().getText() + "."
						+ frmPanelControl.getTxtServidor4().getText();
				puerto = (int) frmPanelControl.getCboPuerto().getSelectedItem();

				comportamientoWander();
				frmPanelControl.getTxtConsola().setText("Ip Server: " + servidor + "\n" + "Puerto: " + puerto);
			}
		});
		
		//Acción del botón WallFollower
		frmPanelControl.getBtnFollower().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				servidor = frmPanelControl.getTxtServidor1().getText() + "."
						+ frmPanelControl.getTxtServidor2().getText() + "."
						+ frmPanelControl.getTxtServidor3().getText() + "."
						+ frmPanelControl.getTxtServidor4().getText();
				puerto = (int) frmPanelControl.getCboPuerto().getSelectedItem();
				
				comportamientoWallFollower();
				frmPanelControl.getTxtConsola().setText("Ip Server: " + servidor + "\n" + "Puerto: " + puerto);
			}
		});

		// Mostrando el formulario
		show(frmPanelControl);

	}

	public void show(FrmMain frame) {
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void comportamientoWander() {
		new Thread(new Runnable() {			
			
		      public void run() {
		    	  
		    	  try {
		    		  
		    		  PlayerClient robot = new PlayerClient(servidor, puerto);
						RangerInterface ranger = robot.requestInterfaceRanger(0, 
								PlayerConstants.PLAYER_OPEN_MODE);
						
						Position2DInterface motor = robot.requestInterfacePosition2D(0, 
								PlayerConstants.PLAYER_OPEN_MODE);
						
						//Encender el ranger y enceder el motor
						ranger.setRangerPower(1);
						motor.setMotorPower(1);
			    	  
			    	  while(true){
							float giro, velocidad;
							
							//Leer todos los datos del robot
							robot.readAll();
							
							if(ranger.isDataReady()){
								PlayerRangerData rangerData = ranger.getData();
								double[] ranges = rangerData.getRanges();
								
								if(ranges.length == 0){
									continue;
								}
								
								
								
								// Simple código para evitar obstáculos
								if(ranges[0] + ranges[1] < ranges[6] + ranges[7])
									giro = -15.0f * (float) Math.PI / 180.0f;
								else
									giro = 15.0f * (float) Math.PI / 180.0f;
								
								if(ranges[3] <0.5f)
									velocidad = 0.0f;
								
								else
									velocidad = 0.2f;
								
								motor.setSpeed(velocidad, giro);
								
							}
						}
		    		  
		    		  
		    	  }catch(Exception error) {
		    		  JOptionPane.showMessageDialog(frmPanelControl, "No hay conexión con el servidor, por favor intente con otra ip",
		    				  						"Ocurrió un error",JOptionPane.ERROR_MESSAGE);
		    		 // error.printStackTrace();
		    	  }
		    	  
		    	  
		      }	
		      
		    	  		    	  
		    }).start();
	}

	public void comportamientoWallFollower() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PlayerClient robot = null;
				Position2DInterface posi = null;
				RangerInterface rngi = null;
				
				try {
					// Connect to the Player server and request access to Position and Sonar
					robot = new PlayerClient (servidor, puerto);
					posi = robot.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
					rngi = robot.requestInterfaceRanger (0, PlayerConstants.PLAYER_OPEN_MODE);
				}
				catch (PlayerException e) {
					System.err.println ("WallFollowerExample: > Error connecting to Player: ");
					System.err.println (" [ " + e.toString() + " ]");
					System.exit (1);
				}
					
					robot.runThreaded (-1, -1);
					
					// Go ahead and find a wall and align to it on the robot's left side
					getWall (posi, rngi);
				
				while (true) {
		            // get all SONAR values and perform the necessary adjustments
		            getSonars (rngi);
		            
		            // by default, just move in front
		            xSpeed   = DEF_X_SPEED;
		            yawSpeed = 0;
		            
		            // if we're getting too close to the wall with the front side...
		            if (frontSide < MAX_WALL_THRESHOLD) {
		                // back up a little bit if we're bumping in front
		                xSpeed   = -0.10f;
		                yawSpeed = - DEF_YAW_SPEED * 4;
		            } else
		                // if we're getting too close to the wall with the left side...
		                if (leftSide < MIN_WALL_THRESHOLD) {
		                    // move slower at corners
		                    xSpeed   = DEF_X_SPEED / 2;
		                    yawSpeed = - DEF_YAW_SPEED ;
		                }
		                else
		                    // if we're getting too far away from the wall with the left side...
		                    if (leftSide > MAX_WALL_THRESHOLD) {
		                        // move slower at corners
		                        xSpeed   = DEF_X_SPEED / 2;
		                        yawSpeed = DEF_YAW_SPEED;
		                    }
		            
		            // Move the robot
		            posi.setSpeed (xSpeed, yawSpeed);
		            System.out.println ("Left side : [" + leftSide + "], xSpeed : [" + xSpeed + "], yawSpeed : [" + yawSpeed + "]");
		            try { Thread.sleep (100); } catch (Exception e) { }
		            
				}
				
			}
		}).start();
	}
	
	static void getWall (Position2DInterface posi, RangerInterface rngi) {
        // get all SONAR values and perform the necessary adjustments
        getSonars (rngi);
        
        // if the robot is in open space, go ahead until it "sees" the wall
        while ((leftSide > MAX_WALL_THRESHOLD) && 
                (frontSide > MAX_WALL_THRESHOLD)) {
            posi.setSpeed (DEF_X_SPEED, 0);
            try { Thread.sleep (100); } catch (Exception e) { }
            getSonars (rngi);
        }
        
        double previousLeftSide = sonarValues[0];
        
        // rotate until we get a smaller value in sonar 0 
        while (sonarValues[0] <= previousLeftSide) {
            previousLeftSide = sonarValues[0];
            
            // rotate more if we're almost bumping in front
            if (Math.min (leftSide, frontSide) == frontSide)
                yawSpeed = -DEF_YAW_SPEED * 3;
            else
                yawSpeed = -DEF_YAW_SPEED;
            
            // Move the robot
            posi.setSpeed (0, yawSpeed);
            try { Thread.sleep (100); } catch (Exception e) { }
            
            getSonars (rngi);
        }
        posi.setSpeed (0, 0);
    }
    
    static void getSonars (RangerInterface rngi) {
        while (!rngi.isDataReady ());
        sonarValues = rngi.getData ().getRanges ();
        
        // ignore erroneous readings/keep interval [SONAR_MIN_VALUE; SONAR_MAX_VALUE]
        for (int i = 0; i < rngi.getData ().getRanges_count (); i++)
            if (sonarValues[i] < SONAR_MIN_VALUE)
                sonarValues[i] = SONAR_MIN_VALUE;
            else
                if (sonarValues[i] > SONAR_MAX_VALUE)
                    sonarValues[i] = SONAR_MAX_VALUE;
        
        leftSide = Math.min (Math.min (sonarValues[0], sonarValues [1]), sonarValues [2]);
        frontSide = Math.min (sonarValues [3], sonarValues [4]);
    }
}

