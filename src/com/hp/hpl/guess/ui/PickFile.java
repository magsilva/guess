package com.hp.hpl.guess.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import java.io.File;
import java.io.IOException;
import java.util.prefs.*;

import com.jgoodies.looks.*;

public class PickFile extends JPanel implements ActionListener {

	private static final long serialVersionUID = -1782036809617887316L;
	private File approved = null;
	private static final char sep = File.separatorChar;
	private Preferences userPrefs = Preferences.userNodeForPackage(getClass());

	/**
	 * The Dialog
	 */
	private JDialog owner = new JDialog();

	/**
	 * Labels
	 */
	private JLabel labelFileName = new JLabel();;
	private JLabel labelImportTo = new JLabel();;
	private JLabel labelName = new JLabel();
	private JLabel labelDBConf = new JLabel();
	private JLabel labelDirectory = new JLabel();

	/**
	 * Text Fields
	 */
	private JFormattedTextField txtDBDirectory = new JFormattedTextField();
	private JFormattedTextField txtFileName = new JFormattedTextField();
	private JFormattedTextField txtDBName = new JFormattedTextField();
	private JComboBox comboImportTo = new JComboBox();

	/**
	 * Panels and Panes
	 */
	private JLayeredPane layeredPane = new JLayeredPane();
	private JPanel panelContent = new JPanel();
	private JPanel contentpanel = null;

	/**
	 * Buttons
	 */
	private JButton btnBrowseFile = new JButton("Browse...");
	private JButton btnCancel = new JButton("Cancel");
	private JButton btnOk = new JButton("Ok");
	private JButton btnBrowseDirectory = new JButton("Browse...");

	/**
	 * do some initial setup to the UI look and feel
	 */
	public static void configureUI() {
		String lafName = Options.getSystemLookAndFeelClassName();

		try {
			UIManager.setLookAndFeel(lafName);
		} catch (Exception e) {
			ExceptionWindow.getExceptionWindow(e);
			System.err.println("Can't set look & feel:" + e);
		}
	}

	/**
	 * Default constructor
	 */
	public PickFile() {
		// Set main layout and size
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		owner.getContentPane().setLayout(flowLayout);
		owner.setLocationByPlatform(true);
		owner.setSize(375, 260);
		owner.setTitle("Import - GUESS");
		owner.setResizable(false);
		owner.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// Add Contentpanel
		contentpanel = createPanel();
		owner.getContentPane().add(contentpanel);
	}

	/**
	 * Main method for panel
	 */
	public static void main(String[] args) {
		PickFile pf = new PickFile();
		File f = pf.showDialog();
		if (f == null) {
			System.out.println("Cancel");
		} else {
			System.out.println(f);
			if (pf.isPersistent()) {
				System.out.println(pf.getName());
				System.out.println(pf.getDirectory());
			} else {
				System.out.println("in memory");
			}
		}
		System.exit(0);
	}

	/**
	 * Shows the Importdialog and returns the selected file.
	 * 
	 * @return Selected File
	 */
	public File showDialog() {
		owner.pack();
		owner.setModal(true);
		owner.setVisible(true);

		return getSelectedFile();
	}

	/**
	 * Returns the selected file to open
	 * 
	 * @return Selected File
	 */
	public File getSelectedFile() {
		return (approved);
	}

	/**
	 * Returns the directory for the database
	 * 
	 * @return Directory
	 */
	public File getDirectory() {
		return new File(txtDBName.getText());
	}

	/**
	 * Returns the name of the database
	 * 
	 * @return Name of Database
	 */
	public String getName() {
		return txtDBName.getText();
	}

	/**
	 * Returns true if the user wants the database stored persistent.
	 * 
	 * @return true, if db is persistent
	 */
	public boolean isPersistent() {
		if (comboImportTo.getSelectedIndex() == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates and returns the main panel of the dialog.
	 * 
	 * @return main panel
	 */
	public JPanel createPanel() {
		labelDBConf.setBounds(20, 85, 135, 20);
		labelDBConf.setOpaque(true);
		labelDBConf.setBackground(SystemColor.control);
		labelDBConf.setLabelFor(layeredPane);
		labelDBConf.setText("Database Configuration");

		layeredPane.setBounds(12, 95, 349, 84);
		layeredPane.setName("");
		layeredPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		txtDBDirectory.setBounds(87, 42, 152, 25);
		layeredPane.add(txtDBDirectory, new Integer(100));
		txtDBDirectory.setEditable(true);
		txtDBDirectory.setPreferredSize(new Dimension(0, 25));

		labelName.setDisplayedMnemonic(KeyEvent.VK_N);
		labelName.setBounds(12, 17, 120, 16);
		layeredPane.add(labelName, new Integer(100));
		labelName.setText("Name:");
		labelName.setLabelFor(txtDBDirectory);

		btnBrowseDirectory.setBounds(245, 42, 90, 25);
		layeredPane.add(btnBrowseDirectory, new Integer(100));
		btnBrowseDirectory.setMnemonic('r');
		btnBrowseDirectory.setPreferredSize(new Dimension(90, 25));
		btnBrowseDirectory.setText("Browse...");
		btnBrowseDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser chooser = new JFileChooser(new File(".")
							.getCanonicalPath());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setDialogTitle("Please select a directory");
					int returnVal = chooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						txtDBDirectory.setText(chooser.getSelectedFile()
								.getAbsolutePath());
					}
				} catch (IOException ex) {
					ExceptionWindow.getExceptionWindow(ex);
				}
			}
		});

		labelDirectory.setBounds(12, 46, 120, 16);
		layeredPane.add(labelDirectory, new Integer(100));
		labelDirectory.setDisplayedMnemonic(KeyEvent.VK_D);
		labelDirectory.setName("");
		labelDirectory.setText("Directory:");
		txtDBName.setBounds(87, 13, 152, 25);
		layeredPane.add(txtDBName, new Integer(100));
		labelDirectory.setLabelFor(txtDBName);



		
		labelFileName.setDisplayedMnemonic(KeyEvent.VK_F);
		labelFileName.setBounds(12, 16, 80, 16);
		labelFileName.setText("File Name:");
		
		
		panelContent.setPreferredSize(new Dimension(373, 235));
		panelContent.setLayout(null);
		panelContent.setActionMap(null);
		panelContent.add(labelDBConf);
		panelContent.add(layeredPane);
		panelContent.add(labelFileName);
		panelContent.add(txtFileName);
		panelContent.add(btnBrowseFile);
		panelContent.add(labelImportTo);
		panelContent.add(comboImportTo);
		panelContent.add(btnOk);
		panelContent.add(btnCancel);
		
		
		labelFileName.setLabelFor(txtFileName);
		txtFileName.setBounds(98, 12, 167, 25);

		btnBrowseFile.setActionCommand("JButton");
		btnBrowseFile.setMnemonic('B');
		btnBrowseFile.setMargin(new Insets(2, 14, 2, 14));
		btnBrowseFile.setBounds(271, 12, 90, 25);
		btnBrowseFile.setPreferredSize(new Dimension(90, 25));
		btnBrowseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String toLoad = ".";
					try {
						toLoad = System.getProperty("gHome");
						if (toLoad == null) {
							toLoad = ".";
						} else {
							File testF = new File(toLoad);
							if (!(testF.exists()) || !(testF.isDirectory())) {
								toLoad = ".";
							}
						}
					} catch (Exception hde) {
						toLoad = ".";
					}
					JFileChooser chooser = new JFileChooser(new File(toLoad)
							.getCanonicalPath());
					int returnVal = chooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						txtFileName.setText(chooser.getSelectedFile()
								.getAbsolutePath());
					}
				} catch (IOException ex) {
					ExceptionWindow.getExceptionWindow(ex);
				}
			}
		});


		labelImportTo.setDisplayedMnemonic(KeyEvent.VK_I);
		labelImportTo.setBounds(12, 47, 80, 16);
		labelImportTo.setText("Import To:");

		labelImportTo.setLabelFor(comboImportTo);
		comboImportTo.setBounds(98, 43, 167, 25);
		comboImportTo.addItem("Memory");
		comboImportTo.addItem("Persistent");

		btnOk.setBounds(175, 200, 90, 25);

		btnOk.setMnemonic('O');
		btnOk.setPreferredSize(new Dimension(90, 25));
		
		ActionListener btnOkAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Save Preferences
				userPrefs.put("filename", txtFileName.getText());
				userPrefs.put("dbdirectory", txtDBDirectory.getText());
				userPrefs.put("dbname", txtDBName.getText());
				userPrefs.putInt("importto", comboImportTo.getSelectedIndex());
				
				try {
					File f = new File(txtFileName.getText());
					if (f.exists()) {
						approved = f;
						// owner.dispose();
					} else {
						JOptionPane
								.showMessageDialog(
										null,
										"File does not exist. Please check the File Name box.",
										"Error - GUESS",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (comboImportTo.getSelectedIndex() == 1) {
						f = new File(txtDBDirectory.getText());
						if ((!f.exists()) || (!f.isDirectory())) {
							JOptionPane
									.showMessageDialog(
											null,
											"Directory does not exist. Please check the Directory box.",
											"Error - GUESS",
											JOptionPane.ERROR_MESSAGE);
							return;
						}
						if ((txtDBName.getText() == null)
								|| (txtDBName.getText().equals(""))) {
							JOptionPane
									.showMessageDialog(
											null,
											"You must declare a database name. Please insert a name to the Name box.",
											"Error - GUESS",
											JOptionPane.ERROR_MESSAGE);
							return;
						}
						f = new File(txtDBDirectory.getText() + sep
								+ txtDBName.getText() + ".properties");
						if (f.exists()) {
							int yn = JOptionPane.showConfirmDialog(null,
									"Database exists, overwrite?",
									"Overwrite Database - GUESS",
									JOptionPane.YES_NO_OPTION);
							if (yn == JOptionPane.NO_OPTION) {
								return;
							}
						}
					}

					owner.dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.toString(),
							"Error - GUESS", JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		btnOk.addActionListener(btnOkAction);
		
		txtFileName.addActionListener(btnOkAction);
		
		
		btnCancel.setBounds(271, 200, 90, 25);

		btnCancel.setMnemonic('C');
		btnCancel.setPreferredSize(new Dimension(90, 25));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				owner.dispose();
			}
		});

		
		// Set Default Button
		owner.getRootPane().setDefaultButton(btnOk);
		
		// Enable / Disable the "Database Configuration"
		// if the user wants the data stored persistent.
		comboImportTo.addActionListener(this);
		persistentEnable(false);
		
		txtFileName.setText(userPrefs.get("filename", ""));
		comboImportTo.setSelectedIndex(userPrefs.getInt("importto", 0));
		txtDBDirectory.setText(userPrefs.get("dbdirectory", ""));
		txtDBName.setText(userPrefs.get("dbname", ""));

		return panelContent;
	}

	/**
	 * Actionhandler for Database Configuration
	 */
	public void actionPerformed(ActionEvent e) {
		persistentEnable(isPersistent());
	}

	/**
	 * Enable / Disable the "Database Configuration"
	 * @param Enable
	 */
	public void persistentEnable(boolean state) {
		labelDBConf.setEnabled(state);
		labelDirectory.setEnabled(state);
		labelName.setEnabled(state);
		txtDBName.setEnabled(state);
		btnBrowseDirectory.setEnabled(state);
		txtDBDirectory.setEnabled(state);

		if (state) {
			if ((txtDBName.getText() == null)
					|| (txtDBName.getText().equals(""))) {
				String simp = txtFileName.getText();
				if ((simp != null) && (!simp.equals(""))) {
					try {
						SunFileFilter filter = new SunFileFilter();

						String fileExtension = filter.getExtension(new File(
								simp));

						simp = simp.substring(0, simp.length() - 1
								- fileExtension.length());

						int seploc = simp.lastIndexOf(sep);
						seploc++;
						simp = simp.substring(seploc);
						txtDBName.setText(simp);
					} catch (Exception ex) {
						ExceptionWindow.getExceptionWindow(ex);
					}
				}
			}
			if ((txtDBDirectory.getText() == null)
					|| (txtDBDirectory.getText().equals(""))) {
				try {
					txtDBDirectory.setText("");
				} catch (Exception ex) {
					ExceptionWindow.getExceptionWindow(ex);
				}
			}
		}
	}

}
