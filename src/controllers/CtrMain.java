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
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.ranger.PlayerRangerData;
import models.Blobfinder;
import models.ComportamientoThread;
import models.WallFollower;
import models.Wander;
import models.WanderThread;
import views.FrmMain;

public class CtrMain {

	// Variables
	private FrmMain frmPanelControl;
	public Boolean chkWander = false;
	private Boolean chkWallFollower = false;
	private Boolean chkBlobfinder = false;

	private ComportamientoThread comportamientos;

	private Wander wan;

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
		frmPanelControl.getTxtServidor4().setText("76");

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
//		frmPanelControl.getScrollPaneCabecera().setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		frmPanelControl.getScrollPaneCabecera().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// Extras
		frmPanelControl.getCboPuerto().setSelectedIndex(1);
		frmPanelControl.getChckbxWander().setSelected(true);
		chkWander = true;

		configuracionTextFields();
		configuracionBtnProcesar();
		configuracionBtnDetener();

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

				// if (chkWander) {
				// if (wt == null) {
				// wt = new WanderThread(servidor, puerto, speed, true);
				// wt.start();
				// } else {
				// wt.setActive(true);
				// }
				// } else {
				// if (wt != null)
				// wt.setActive(false);
				// }

				if (comportamientos == null) {
					comportamientos = new ComportamientoThread(servidor, puerto, frmPanelControl, chkWander, chkWallFollower, chkBlobfinder);
					comportamientos.setAceleracion(aceleracion);
				} else {
					comportamientos.setWander(chkWander);
					comportamientos.setWallFollower(chkWallFollower);
					comportamientos.setBlobfinder(chkBlobfinder);
					comportamientos.setAceleracion(aceleracion);
				}

				String simboloWander = (chkWander == true) ? "\u2713" : "X";
				String simboloBlobfinder = (chkBlobfinder == true) ? "\u2713" : "X";
				String simboloWallFollower = (chkWallFollower == true) ? "\u2713" : "X";


				frmPanelControl.getTxtCabecera().setText(" Servidor: " + servidor + " | Puerto: " + puerto
						+ " | Comportamientos: WN[" + simboloWander + "]-WF["+simboloWallFollower+"]-BF["+simboloBlobfinder+"]");


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

				// Ejecuta la acción para detener los comportamientos
				if (comportamientos == null) {
					comportamientos = new ComportamientoThread(servidor, puerto, frmPanelControl, chkWander, chkWallFollower, chkBlobfinder);
					comportamientos.setWander(chkWander);
					comportamientos.setWallFollower(chkWallFollower);
					comportamientos.setBlobfinder(chkBlobfinder);
				} else {
					comportamientos.setWander(chkWander);
					comportamientos.setWallFollower(chkWallFollower);
					comportamientos.setBlobfinder(chkBlobfinder);
				}

			}
		});
	}

	public void configuracionBtnProcesarVersionAntigua() {
		// Acción para el btnProcesar
		frmPanelControl.getBtnProcesar().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Comportamientos elegidos

				chkWander = frmPanelControl.getChckbxWander().isSelected();
				chkWallFollower = frmPanelControl.getChckbxWallFollower().isSelected();
				chkBlobfinder = frmPanelControl.getChckbxBlobfinder().isSelected();

				// Validación
				if (chkWander || chkWallFollower || chkBlobfinder) {

					servidor = frmPanelControl.getTxtServidor1().getText() + "."
							+ frmPanelControl.getTxtServidor2().getText() + "."
							+ frmPanelControl.getTxtServidor3().getText() + "."
							+ frmPanelControl.getTxtServidor4().getText();
					puerto = (int) frmPanelControl.getCboPuerto().getSelectedItem();

					frmPanelControl.getTxtConsola()
							.setText("-----------------------------------------------------------------------------\n");
					frmPanelControl.getTxtConsola().append(" Servidor: " + servidor + " | Puerto: " + puerto
							+ " | Comportamientos: WN[\u2713]-BF[X]-WF[X]");
					frmPanelControl.getTxtConsola()
							.append("\n-----------------------------------------------------------------------------");

					if (chkWander) {

						if (wan == null) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										int speed = Integer.parseInt(
												frmPanelControl.getCboVelocidad().getSelectedItem().toString());
										wan = new Wander(servidor, puerto, speed, frmPanelControl.getTxtConsola());
									} catch (Exception e) {
										System.out.println("Problema de conversion, velocidad erronea!");
									}
								}
							}).start();
						}

					}

					if (chkBlobfinder) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								int speed = Integer
										.parseInt(frmPanelControl.getCboVelocidad().getSelectedItem().toString());
								new Blobfinder(servidor, puerto, speed);

							}
						}).start();
						;
					}

					if (chkWallFollower) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								int speed = Integer
										.parseInt(frmPanelControl.getCboVelocidad().getSelectedItem().toString());
								new WallFollower(servidor, puerto, speed);

							}
						}).start();
						;
					}

				} else {
					JOptionPane.showMessageDialog(frmPanelControl, "Debe seleccionar al menos un comportamiento",
							"Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
	}
}
