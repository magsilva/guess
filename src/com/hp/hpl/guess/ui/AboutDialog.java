package com.hp.hpl.guess.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.hp.hpl.guess.Version;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 5817022284621043902L;
	public AboutDialog() {
		super();
		setLocationByPlatform(true);
		setTitle("About - GUESS");
		getContentPane().setLayout(null);
		setResizable(false);
		setSize(new Dimension(450, 376));
		
		
		// Set background image
		ImageIcon imageIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("images/guess-about.png"))); 
	    JLabel background = new JLabel(imageIcon);
	    background.setBounds(0, 0, 450, 500);
	    getContentPane().add(background);

		final JLabel versionLabel = new JLabel();
		versionLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		versionLabel.setText("<html><center>Major version: " + Version.MAJOR_VERSION + "<br>Minor version: " + Version.MINOR_VERSION + "</center></html>");
		versionLabel.setBounds(190, 135, 210, 51);
		getContentPane().add(versionLabel);

		final JLabel contributions = new JLabel();
		contributions.setHorizontalTextPosition(SwingConstants.LEFT);
		contributions.setVerticalAlignment(SwingConstants.TOP);
		contributions.setHorizontalAlignment(SwingConstants.LEFT);
		contributions.setText("Full credits are distributed with the source.");
		contributions.setBounds(10, 207, 401, 16);
		getContentPane().add(contributions);

		final JButton closeButton = new JButton();
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				dispose();
			}
		});
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.setText("Close");
		closeButton.setBounds(344, 313, 90, 25);
		getContentPane().add(closeButton);

		final JLabel wikiLabel = new JLabel();
		wikiLabel.setVerticalAlignment(SwingConstants.TOP);
		wikiLabel.setText("<html>You can find the GUESS Homepage at: <a href=\"http://graphexploration.cond.org\">" +
				"http://graphexploration.cond.org</a>, the Wiki is located at <a href=\"http://guess.wikispot.org/\">http://guess.wikispot.org/</a>");
		wikiLabel.setBounds(10, 230, 424, 51);
		getContentPane().add(wikiLabel);
	    
	}
	

}
