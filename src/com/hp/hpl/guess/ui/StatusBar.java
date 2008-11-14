package com.hp.hpl.guess.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

import com.hp.hpl.guess.Guess;


public class StatusBar extends JPanel {

	private static final long serialVersionUID = 1L;

	private static StatusBar sb = null;
	
	protected static JLabel label = null;



	private static void onEmptyHide() {
		if (sb==null) {
			return;
		}
		if (label.getText().trim().equals("")) {
			sb.setVisible(false);
		} else {
			sb.setVisible(true);
		}
	}
	
	public static void repaintNow() {
		if (sb != null) {
			sb.repaint();
		}
	}

	public static void setStatus(String status) {
		if (label != null) {
			sb.setEmptyIcon();
			label.setText(status);
		}
		onEmptyHide();
	}
	
	public static void setStatus(String status, boolean state) {
		setStatus(status);
	}
	

	public static void setErrorStatus(String status) {
		if (label != null) {
			sb.setErrorIcon();
			label.setText(status);
		}
		onEmptyHide();
	}
	
	private void setErrorIcon() {
		label.setIcon(new ImageIcon(getClass().getResource("/images/dialog-error.png")));
	}
	
	private void setEmptyIcon() {
		label.setIcon(null);
	}

	
	public StatusBar() {
		super();

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.SOUTHWEST;
		
		
		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.LEFT);
		
		Border b1 = BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(180,188,197));
		Border b2 = BorderFactory.createEmptyBorder(2, 6, 2, 6);
		label.setBorder(BorderFactory.createCompoundBorder(b1, b2));
		
		label.setForeground(new Color(68, 70, 72));
		
		label.setBackground(new Color(227, 235, 250));
		
		add(label, c);
		
		setOpaque(false);
		label.setOpaque(true);
		
		StatusBar.sb = this;
		
		onEmptyHide();
	}

}
