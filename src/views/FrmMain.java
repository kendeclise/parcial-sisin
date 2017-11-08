package views;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class FrmMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5370125019878325062L;
	private JPanel contentPane;
	private JComboBox<Integer> cboPuerto;
	private JComboBox<Integer> cboVelocidad;
	private JButton btnProcesar;
	private JTextArea txtConsola;
	private JTextField txtServidor1;
	private JTextField txtServidor2;
	private JTextField txtServidor3;
	private JTextField txtServidor4;
	private JLabel label;
	private JLabel label_1;
	private JLabel label_2;
	private JPanel panel_1;
	private JCheckBox chckbxWander;
	private JCheckBox chckbxWallFollower;
	private JCheckBox chckbxBlobfinder;
	private JButton btnDetener;
	private JScrollPane scrollPane;	
	private JTextArea txtCabecera;
	private JScrollPane scrollPaneCabecera;

	public FrmMain() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 599, 378);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtConsola = new JTextArea();
		txtConsola.setEditable(false);
		txtConsola.setForeground(Color.DARK_GRAY);
		txtConsola.setFont(new Font("Monospaced", Font.BOLD, 11));
		//txtConsola.setBounds(23, 165, 519, 119);
		txtConsola.setBorder(new LineBorder(Color.LIGHT_GRAY));
		//txtConsola.setEnabled(false);
		//contentPane.add(txtConsola);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Comportamientos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(294, 28, 132, 126);
		contentPane.add(panel);
		panel.setLayout(null);

		chckbxWander = new JCheckBox("Wander");
		chckbxWander.setBounds(20, 29, 103, 23);
		panel.add(chckbxWander);

		chckbxWallFollower = new JCheckBox("Wall Follower");
		chckbxWallFollower.setBounds(20, 55, 106, 23);
		panel.add(chckbxWallFollower);

		chckbxBlobfinder = new JCheckBox("Blobfinder");
		chckbxBlobfinder.setBounds(20, 81, 108, 23);
		panel.add(chckbxBlobfinder);

		btnProcesar = new JButton("PROCESAR");
		btnProcesar.setBounds(436, 41, 131, 47);
		contentPane.add(btnProcesar);

		panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Opciones", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(23, 28, 261, 126);
		contentPane.add(panel_1);

		JLabel lblServidor = new JLabel("Servidor:");
		lblServidor.setBounds(28, 27, 77, 14);
		panel_1.add(lblServidor);

		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setBounds(28, 55, 58, 14);
		panel_1.add(lblPuerto);

		cboPuerto = new JComboBox<Integer>();
		cboPuerto.setBounds(96, 52, 142, 20);
		panel_1.add(cboPuerto);

		txtServidor1 = new JTextField();
		txtServidor1.setBounds(96, 24, 30, 20);
		panel_1.add(txtServidor1);
		txtServidor1.setText("127");
		txtServidor1.setColumns(10);

		txtServidor2 = new JTextField();
		txtServidor2.setBounds(136, 24, 30, 20);
		panel_1.add(txtServidor2);
		txtServidor2.setText("0");
		txtServidor2.setColumns(10);

		txtServidor3 = new JTextField();
		txtServidor3.setBounds(171, 24, 30, 20);
		panel_1.add(txtServidor3);
		txtServidor3.setText("0");
		txtServidor3.setColumns(10);

		txtServidor4 = new JTextField();
		txtServidor4.setBounds(208, 24, 30, 20);
		panel_1.add(txtServidor4);
		txtServidor4.setText("1");
		txtServidor4.setColumns(10);

		label = new JLabel(".");
		label.setBounds(125, 27, 11, 14);
		panel_1.add(label);

		label_1 = new JLabel(".");
		label_1.setBounds(165, 27, 11, 14);
		panel_1.add(label_1);

		label_2 = new JLabel(".");
		label_2.setBounds(203, 27, 11, 14);
		panel_1.add(label_2);

		JLabel lblVelocidad = new JLabel("Velocidad:");
		lblVelocidad.setBounds(28, 83, 161, 14);
		panel_1.add(lblVelocidad);

		cboVelocidad = new JComboBox<Integer>();
		cboVelocidad.setBounds(96, 80, 142, 20);
		panel_1.add(cboVelocidad);
		
		scrollPane = new JScrollPane(txtConsola);
		scrollPane.setBounds(23, 200, 544, 119);
		contentPane.add(scrollPane);
		
		btnDetener = new JButton("DETENER");
		btnDetener.setBounds(436, 99, 131, 47);
		contentPane.add(btnDetener);
		
		txtCabecera = new JTextArea();
		txtCabecera.setForeground(Color.DARK_GRAY);
		txtCabecera.setFont(new Font("Monospaced", Font.BOLD, 11));
		txtCabecera.setEditable(false);
		txtCabecera.setBorder(new LineBorder(Color.LIGHT_GRAY));
		txtCabecera.setBounds(25, 169, 542, 33);
		
		scrollPaneCabecera = new JScrollPane(txtCabecera);
		scrollPaneCabecera.setBounds(25, 169, 542, 20);
		contentPane.add(scrollPaneCabecera);
		

	}

	// Getters

	public JComboBox<Integer> getCboPuerto() {
		return cboPuerto;
	}

	public JTextArea getTxtConsola() {
		return txtConsola;
	}

	public JTextField getTxtServidor1() {
		return txtServidor1;
	}

	public JTextField getTxtServidor2() {
		return txtServidor2;
	}

	public JTextField getTxtServidor3() {
		return txtServidor3;
	}

	public JTextField getTxtServidor4() {
		return txtServidor4;
	}

	public JComboBox<Integer> getCboVelocidad() {
		return cboVelocidad;
	}

	public JButton getBtnProcesar() {
		return btnProcesar;
	}
	
	public JButton getBtnDetener() {
		return btnDetener;
	}

	public JCheckBox getChckbxWander() {
		return chckbxWander;
	}

	public JCheckBox getChckbxWallFollower() {
		return chckbxWallFollower;
	}

	public JCheckBox getChckbxBlobfinder() {
		return chckbxBlobfinder;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public JTextArea getTxtCabecera() {
		return txtCabecera;
	}

	public JScrollPane getScrollPaneCabecera() {
		return scrollPaneCabecera;
	}
	
	
}