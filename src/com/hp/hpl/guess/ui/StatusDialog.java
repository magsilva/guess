package com.hp.hpl.guess.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class StatusDialog {

	private JProgressBar statusProgressbar = new JProgressBar();
	private JDialog mainDialog = new JDialog();
	private JLabel descriptionLabel = new JLabel();
	private JButton stopButton = new JButton();
	private Thread operationThread = null;
	
	
	public StatusDialog() {

		mainDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				operationThread.stop();
				hide();
			}
		});
		
		mainDialog.getContentPane().add(statusProgressbar);
		mainDialog.getContentPane().setLayout(null);
		mainDialog.pack();
		mainDialog.setResizable(false);
		mainDialog.setBounds(200, 200, 434, 105);
		mainDialog.getContentPane().add(descriptionLabel);
		mainDialog.getContentPane().add(stopButton);

		statusProgressbar.setBounds(12, 41, 307, 25);
		statusProgressbar.setIndeterminate(true);

		descriptionLabel.setBounds(10, 19, 309, 16);

		stopButton.setText("Stop");
		stopButton.setBounds(325, 41, 90, 25);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operationThread.stop();
				hide();
			}
		});

	}
	
	public void setTitle(String title) {
		mainDialog.setTitle(title + " - GUESS");
	}

	public void setDescription(String description) {
		descriptionLabel.setText(description);
	}
	
	public void setThread(Thread thrd) {
		operationThread = thrd;
	}
	
	public void setMinValue(int minValue) {
		 statusProgressbar.setMinimum(minValue);
	}
	
	public void setMaxValue(int maxValue) {
		statusProgressbar.setMaximum(maxValue);
	}
	
	public void setCurValue(int curValue) {
		statusProgressbar.setValue(curValue);
	}

	public void setIndeterminate(boolean isIndeterminate) {
		statusProgressbar.setIndeterminate(isIndeterminate);
	}

	public void show() {
		mainDialog.setVisible(true);
	}

	public void hide() {
		mainDialog.setVisible(false);
	}
}
