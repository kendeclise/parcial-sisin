package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import models.Comportamiento;
import models.ComportamientoThread;
import views.FrmMain;

public class CtrMain {

	// Variables
	private FrmMain frmPanelControl;
	public Boolean chkWander = false;
	private Boolean chkWallFollower = false;
	private Boolean chkBlobfinder = false;

	String directorio = System.getProperty("user.dir") + "\\src\\runnable\\Comportamientos.jar";

	// Procesos
	Process p_6101 = null;
	Process p_6102 = null;
	Process p_6103 = null;
	Process p_6104 = null;
	Process p_6665 = null;

	// private WanderThread wt;

	// Variables de configuración del bot

	private String servidor = "localhost";
	private int puerto = 6665;

	public CtrMain() {
		// Instanciando el Jframe
		if (frmPanelControl == null) {
			frmPanelControl = new FrmMain();
		}

		// Configuración del GUI

		// Opciones adicionales
		frmPanelControl.setTitle("Panel de control");
		frmPanelControl.getBtnProcesar().setToolTipText("Ejecuta los comportamientos seleccionados para el bot");
		frmPanelControl.getBtnDetener().setToolTipText("Detiene todos los comportamientos del bot");

		// Ip
		frmPanelControl.getTxtServidor1().setText("192");
		frmPanelControl.getTxtServidor2().setText("168");
		frmPanelControl.getTxtServidor3().setText("1");
		frmPanelControl.getTxtServidor4().setText("40");

		// Agregando items al combo (puertos disponibles)
		frmPanelControl.getCboPuerto().addItem(6101);
		frmPanelControl.getCboPuerto().addItem(6102);
		frmPanelControl.getCboPuerto().addItem(6103);
		frmPanelControl.getCboPuerto().addItem(6104);
		frmPanelControl.getCboPuerto().addItem(6665);

		frmPanelControl.getCboVelocidad().addItem(1);
		frmPanelControl.getCboVelocidad().addItem(2);
		frmPanelControl.getCboVelocidad().addItem(3);
		frmPanelControl.getCboVelocidad().addItem(4);

		frmPanelControl.getCboVelocidad().setEnabled(false);
		// frmPanelControl.getScrollPaneCabecera().setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// frmPanelControl.getScrollPaneCabecera().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// Extras
		frmPanelControl.getCboPuerto().setSelectedIndex(1);
		frmPanelControl.getChckbxWander().setSelected(true);
		chkWander = true;

		configuracionTextFields();
		configuracionBtnProcesar();
		configuracionBtnDetener();

		configuracionBotonCerrar();
		
		// Mostrando el formulario
		mostrarGUI();

	}

	public void mostrarGUI() {
		frmPanelControl.setLocationRelativeTo(null);
		frmPanelControl.setVisible(true);
	}

	public void configuracionTextFields() {

		// Limitando los caracteres de la direccion IP
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
				// Valido que sea un numero
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
				// Valido que sea un nï¿½mero
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
				// Valido que sea un numero
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
				// Valido que sea un nï¿½mero
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
	}

	public void configuracionBtnProcesar() {
		// Acción para el btnProcesar
		frmPanelControl.getBtnProcesar().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Comportamientos elegidos

				chkWander = frmPanelControl.getChckbxWander().isSelected();
				chkWallFollower = frmPanelControl.getChckbxWallFollower().isSelected();
				chkBlobfinder = frmPanelControl.getChckbxBlobfinder().isSelected();

				// Validación

				servidor = frmPanelControl.getTxtServidor1().getText() + "."
						+ frmPanelControl.getTxtServidor2().getText() + "."
						+ frmPanelControl.getTxtServidor3().getText() + "."
						+ frmPanelControl.getTxtServidor4().getText();
				puerto = (int) frmPanelControl.getCboPuerto().getSelectedItem();

				int aceleracion = Integer.parseInt(frmPanelControl.getCboVelocidad().getSelectedItem().toString());

				// new Comportamiento(servidor, puerto, chkWander, chkWallFollower,
				// chkBlobfinder);
				// new ComportamientoThread(servidor, puerto, frmPanelControl, chkWander,
				// chkWallFollower, chkBlobfinder);
				if (puerto == 6101) {
					try {
						if (p_6101 != null)
							p_6101.destroyForcibly();

						p_6101 = Runtime.getRuntime()
								.exec("java -jar " + directorio + " " + servidor + " " + puerto + " "
										+ chkWander.toString() + " " + chkWallFollower.toString() + " "
										+ chkBlobfinder.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (puerto == 6102) {
					try {
						if (p_6102 != null)
							p_6102.destroyForcibly();

						p_6102 = Runtime.getRuntime()
								.exec("java -jar " + directorio + " " + servidor + " " + puerto + " "
										+ chkWander.toString() + " " + chkWallFollower.toString() + " "
										+ chkBlobfinder.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (puerto == 6103) {
					try {
						if (p_6103 != null)
							p_6103.destroyForcibly();

						p_6103 = Runtime.getRuntime()
								.exec("java -jar " + directorio + " " + servidor + " " + puerto + " "
										+ chkWander.toString() + " " + chkWallFollower.toString() + " "
										+ chkBlobfinder.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (puerto == 6104) {
					try {
						if (p_6104 != null)
							p_6104.destroyForcibly();

						p_6104 = Runtime.getRuntime()
								.exec("java -jar " + directorio + " " + servidor + " " + puerto + " "
										+ chkWander.toString() + " " + chkWallFollower.toString() + " "
										+ chkBlobfinder.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (puerto == 6665) {
					try {
						if (p_6665 != null)
							p_6665.destroyForcibly();

						p_6665 = Runtime.getRuntime()
								.exec("java -jar " + directorio + " " + servidor + " " + puerto + " "
										+ chkWander.toString() + " " + chkWallFollower.toString() + " "
										+ chkBlobfinder.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				// Cabecera comportamientos seleccionados

				String simboloWander = (chkWander == true) ? "\u2713" : "X";
				String simboloBlobfinder = (chkBlobfinder == true) ? "\u2713" : "X";
				String simboloWallFollower = (chkWallFollower == true) ? "\u2713" : "X";

				frmPanelControl.getTxtCabecera()
						.setText(" Servidor: " + servidor + " | Puerto: " + puerto + " | Comportamientos: WN["
								+ simboloWander + "]-WF[" + simboloWallFollower + "]-BF[" + simboloBlobfinder + "]");

			}
		});
	}

	public void configuracionBtnDetener() {
		// Accion para el btnDetener
		frmPanelControl.getBtnDetener().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Cambio los valores de las variables y las check de la interfaz
				chkWander = false;
				chkWallFollower = false;
				chkBlobfinder = false;

				frmPanelControl.getChckbxWander().setSelected(false);
				frmPanelControl.getChckbxWallFollower().setSelected(false);
				frmPanelControl.getChckbxBlobfinder().setSelected(false);

				// Limpieza de los JtextArea
				frmPanelControl.getTxtConsola().setText("");
				frmPanelControl.getTxtCabecera().setText("");
				
				puerto = (int) frmPanelControl.getCboPuerto().getSelectedItem();

				// Ejecuta la acción para detener los comportamientos
				if (puerto == 6101) {

					if (p_6101 != null)
						p_6101.destroyForcibly();

				}
				if (puerto == 6102) {

					if (p_6102 != null)
						p_6102.destroyForcibly();

				}
				if (puerto == 6103) {

					if (p_6103 != null)
						p_6103.destroyForcibly();

				}
				if (puerto == 6104) {

					if (p_6104 != null)
						p_6104.destroyForcibly();

				}
				if (puerto == 6665) {

					if (p_6665 != null)
						p_6665.destroyForcibly();

				}

			}
		});
	}

	public void configuracionBotonCerrar() {
		
		frmPanelControl.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				// Ejecuta la acción para detener los comportamientos
				if (puerto == 6101) {

					if (p_6101 != null)
						p_6101.destroyForcibly();

				}
				if (puerto == 6102) {

					if (p_6102 != null)
						p_6102.destroyForcibly();

				}
				if (puerto == 6103) {

					if (p_6103 != null)
						p_6103.destroyForcibly();

				}
				if (puerto == 6104) {

					if (p_6104 != null)
						p_6104.destroyForcibly();

				}
				if (puerto == 6665) {

					if (p_6665 != null)
						p_6665.destroyForcibly();

				}
				
				System.exit(0);
			}
			
		});		
		
	}
	
}
