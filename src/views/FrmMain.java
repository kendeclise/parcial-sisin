package views;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class FrmMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5370125019878325062L;
	private JPanel contentPane;
	private JButton btnWander;
	private JButton btnBlobfinder;
	private JButton btnFollower;
	private JComboBox<Integer> cboPuerto;
	private JTextArea txtConsola;
	private JTextField txtServidor1;
	private JTextField txtServidor2;
	private JTextField txtServidor3;
	private JTextField txtServidor4;
	private JLabel label;
	private JLabel label_1;
	private JLabel label_2;
	
	public FrmMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 424, 313);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnWander = new JButton("Wander");
		btnWander.setBounds(273, 32, 124, 23);
		contentPane.add(btnWander);
		
		btnBlobfinder = new JButton("Blobfinder");
		btnBlobfinder.setBounds(273, 67, 124, 23);
		contentPane.add(btnBlobfinder);
		
		btnFollower = new JButton("Wall Follower");
		btnFollower.setBounds(273, 102, 124, 23);
		contentPane.add(btnFollower);
		
		JLabel lblServidor = new JLabel("Servidor:");
		lblServidor.setBounds(23, 36, 77, 14);
		contentPane.add(lblServidor);
		
		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setBounds(23, 75, 58, 14);
		contentPane.add(lblPuerto);
		
		
		
		cboPuerto = new JComboBox<Integer>();
		cboPuerto.setBounds(91, 72, 142, 20);
		contentPane.add(cboPuerto);
		
		txtConsola = new JTextArea();
		txtConsola.setBounds(23, 137, 374, 126);
		txtConsola.setBorder(BorderFactory.createLineBorder(Color.GRAY));		
		contentPane.add(txtConsola);
		
		txtServidor1 = new JTextField();
		txtServidor1.setBounds(91, 33, 30, 20);
		txtServidor1.setText("127");
		contentPane.add(txtServidor1);
		txtServidor1.setColumns(10);
		
		txtServidor2 = new JTextField();
		txtServidor2.setBounds(131, 33, 30, 20);
		txtServidor2.setText("0");
		txtServidor2.setColumns(10);
		contentPane.add(txtServidor2);
		
		txtServidor3 = new JTextField();
		txtServidor3.setBounds(166, 33, 30, 20);
		txtServidor3.setText("0");
		txtServidor3.setColumns(10);
		contentPane.add(txtServidor3);
		
		txtServidor4 = new JTextField();
		txtServidor4.setBounds(203, 33, 30, 20);
		txtServidor4.setText("1");
		txtServidor4.setColumns(10);
		contentPane.add(txtServidor4);
		
		label = new JLabel(".");
		label.setBounds(120, 36, 11, 14);
		contentPane.add(label);
		
		label_1 = new JLabel(".");
		label_1.setBounds(160, 36, 11, 14);
		contentPane.add(label_1);
		
		label_2 = new JLabel(".");
		label_2.setBounds(198, 36, 11, 14);
		contentPane.add(label_2);
		
		
	}
	
	//Getters

	public JPanel getContentPane() {
		return contentPane;
	}

	public JButton getBtnWander() {
		return btnWander;
	}

	public JButton getBtnBlobfinder() {
		return btnBlobfinder;
	}

	public JButton getBtnFollower() {
		return btnFollower;
	}


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
	
	
	
	

	
	
	
}