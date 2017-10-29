package main;

import javax.swing.UIManager;

import controllers.CtrMain;
import models.Blobfinder;

/*
 * Autor: Jorge Luis Bustamante
 */

public class Main {

	public static void main(String[] args) {

		// Look and Feel
		try {
			// UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
			// UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
			// UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
			// UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
			// UIManager.setLookAndFeel ( new WebLookAndFeel () );
			// UIManager.setLookAndFeel ( "com.alee.laf.WebLookAndFeel" );
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		new CtrMain();
	}

}
