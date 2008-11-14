package com.hp.hpl.guess.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.hp.hpl.guess.Guess;

/**
 * Displays a status dialog and let the user cancel the running
 * operation. The operation progress can be determinant or indeterminant.
 * 
 * The operation must run in its own thread an the thread must be started
 * before the show() method is called.
 * 
 */
public class StatusDialog {
	
	/**
	 * If the user clicked cancel
	 */
	private boolean userCanceled = false;
	
	/**
	 * GUI Widgets
	 */
	private JDialog dialogWindow = new JDialog(Guess.getMainUIWindow(), true);
	private JCheckBox chkRedrawGraph = new JCheckBox();
	private JProgressBar statusProgressbar = new JProgressBar();
	private JLabel descriptionLabel = new JLabel();
	private JButton stopButton = new JButton();
	
	/**
	 * init the gui
	 */
	public StatusDialog() {

		dialogWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
				hide();
			}
		});
		
		dialogWindow.getContentPane().add(statusProgressbar);
		dialogWindow.getContentPane().setLayout(null);
		dialogWindow.pack();
		dialogWindow.setResizable(false);
		dialogWindow.setBounds(200, 200, 434, 134);
		dialogWindow.getContentPane().add(descriptionLabel);
		dialogWindow.getContentPane().add(stopButton);

		statusProgressbar.setBounds(12, 41, 403, 25);
		statusProgressbar.setIndeterminate(true);

		descriptionLabel.setBounds(10, 19, 309, 16);

		stopButton.setText("Stop");
		stopButton.setBounds(325, 74, 90, 25);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
				hide();
			}
		});
		
		chkRedrawGraph.setBounds(12, 74, 307, 24);
		chkRedrawGraph.setSelected(true);
		chkRedrawGraph.setText("Update graph while layout is running.");
		dialogWindow.getContentPane().add(chkRedrawGraph);

	}
	
	/**
	 * set the dialog title
	 * @param title
	 */
	public void setTitle(String title) {
		dialogWindow.setTitle(title + " - GUESS");
	}

	/**
	 * set the operation description
	 * @param description
	 */
	public void setDescription(String description) {
		descriptionLabel.setText(description);
	}
	
	/**
	 * set the minimum value for the progressbar
	 * @param minValue
	 */
	public void setMinValue(int minValue) {
		 statusProgressbar.setMinimum(minValue);
	}
	
	/**
	 * set the maximum value for the progressbar
	 * @param maxValue
	 */
	public void setMaxValue(int maxValue) {
		statusProgressbar.setMaximum(maxValue);
	}
	
	/**
	 * set the current value for the progressbar
	 * @param curValue
	 */
	public void setCurValue(int curValue) {
		statusProgressbar.setValue(curValue);
	}

	/**
	 * set if the operation progress is indeterminant
	 * @param isIndeterminate
	 */
	public void setIndeterminate(boolean isIndeterminate) {
		statusProgressbar.setIndeterminate(isIndeterminate);
	}
	
	/**
	 * return true if the graph should be repainted
	 * @return
	 */
	public boolean isRedrawGraph() {
		return chkRedrawGraph.isSelected();
	}
	
	/**
	 * return true if the operation should be stopped
	 * @return
	 */
	public boolean isCanceled() {
		return userCanceled;
	}

	/**
	 * show the dialog
	 */
	public void show() {
		dialogWindow.setVisible(true);
	}

	/**
	 * hide the dialog
	 */
	public void hide() {
		dialogWindow.setVisible(false);
	}
	
	/**
	 * let the operation stop
	 */
	private void stop() {
		userCanceled = true;
	}

	
} 

