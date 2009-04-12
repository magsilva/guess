package com.hp.hpl.guess.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jidesoft.swing.FolderChooser;
import java.lang.reflect.*;

public class welcomeDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * GUI Widgets
	 */
	private JDialog welcomedlg;

	private JLabel welcomeLbl;
	
	private ButtonGroup buttonGroup;
	private JRadioButton openDatabaseBtn;
	private JRadioButton CreateEmptyBtn;
	private JRadioButton ImportGraphBtn;
	
	private JTextField openDatabaseFileNameTxt;
	private JLabel fileNameOpenDatabaseLbl;
	private JButton browseOpenDatabaseFileNameBtn;
	
	private JTextField databaseDirectoryTxt;
	private JTextField databaseNameTxt;
	private JLabel databaseDirLbl;
	private JLabel databaseNameLbl;
	private JButton importToDatabaseDirectoryBtn;
	private JComboBox importToCombo;
	private JLabel fileNameImportGraphLbl;
	private JButton importToFileNameBtn;
	private JLabel importToLbl;
	private JTextField importToFileNameTxt;
	
	private JButton okBtn;
	private JLabel headerLogo;
	private JLabel headerText;
	private JLabel headerSpacer;
	private JButton cancelBtn;	

	/**
	 * Save and load user preferences
	 */
	private Preferences userPrefs = Preferences.userNodeForPackage(getClass());

	/**
	 * Types of opening the database
	 */
	public final int DB_OPEN_DATABASE = 0;
	public final int DB_IMPORT_GRAPH_MEMORY = 1;
	public final int DB_IMPORT_GRAPH_PERSISTENT = 2;
	public final int DB_CREATE_EMPTY = 3;
	
	/**
	 * the user's choice
	 */
	private int usersChoice = -1;
	
	public welcomeDialog() {
	    //		super(null,ModalityType.TOOLKIT_MODAL);
	    super((java.awt.Frame)null);
		setModal(true);
		welcomedlg = this;
		initGUI();
		getPreferences();
		enableDisableWidgets();
		setLocationRelativeTo(null);
		
		// Give focus to ok button
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				okBtn.requestFocusInWindow();
			}
		});
	}
	
	/**
	 * Load preferences
	 */
	private void getPreferences() {
		openDatabaseFileNameTxt.setText(userPrefs.get("openfilename", ""));
		importToFileNameTxt.setText(userPrefs.get("importfilename", ""));
		importToCombo.setSelectedIndex(userPrefs.getInt("importto", 0));
		databaseDirectoryTxt.setText(userPrefs.get("dbdirectory", ""));
		databaseNameTxt.setText(userPrefs.get("dbname", ""));

		int radioSelected = userPrefs.getInt("LastButtonSelection", -1);
		if (radioSelected==0) {
			openDatabaseBtn.setSelected(true);
		} else if (radioSelected==1) {
			ImportGraphBtn.setSelected(true);
		} else if (radioSelected==2) {
			CreateEmptyBtn.setSelected(true);
		}
	}
	
	/**
	 * Save preferences
	 */
	private void setPreferences() {
		userPrefs.put("openfilename", openDatabaseFileNameTxt.getText());
		userPrefs.put("importfilename", importToFileNameTxt.getText());
		userPrefs.putInt("importto", importToCombo.getSelectedIndex());
		userPrefs.put("dbdirectory", databaseDirectoryTxt.getText());
		userPrefs.put("dbname", databaseNameTxt.getText());
		
		int radioSelected = -1;
		if (openDatabaseBtn.isSelected()) {
			radioSelected = 0;
		} else if (ImportGraphBtn.isSelected()) {
			radioSelected = 1;
		} else if (CreateEmptyBtn.isSelected()) {
			radioSelected = 2;
		}
		userPrefs.putInt("LastButtonSelection", radioSelected);
	}
	
	
	/**
	 * Enable / Disable the elements
	 */
	private void enableDisableWidgets() {
		
		// Save old focus owner
		Component FocusOwner = null;
		if (welcomedlg!=null) {
			FocusOwner = welcomedlg.getFocusOwner();
		}
		
		// First disable all fields
		openDatabaseFileNameTxt.setEnabled(false);
		fileNameOpenDatabaseLbl.setEnabled(false);
		browseOpenDatabaseFileNameBtn.setEnabled(false);
		
		databaseDirectoryTxt.setEnabled(false);
		databaseNameTxt.setEnabled(false);
		databaseDirLbl.setEnabled(false);
		databaseNameLbl.setEnabled(false);
		importToDatabaseDirectoryBtn.setEnabled(false);
		importToCombo.setEnabled(false);
		fileNameImportGraphLbl.setEnabled(false);
		importToFileNameBtn.setEnabled(false);
		importToLbl.setEnabled(false);
		importToFileNameTxt.setEnabled(false);
		
		okBtn.setEnabled(false);
		
		// Open Database
		if (openDatabaseBtn.isSelected()) {
			openDatabaseFileNameTxt.setEnabled(true);
			fileNameOpenDatabaseLbl.setEnabled(true);
			browseOpenDatabaseFileNameBtn.setEnabled(true);
			
			// Filename not null
			if (!openDatabaseFileNameTxt.getText().equals("")) {
				okBtn.setEnabled(true);
			}
		}
		
		// Import to
		if (ImportGraphBtn.isSelected()) {
			
			importToCombo.setEnabled(true);
			fileNameImportGraphLbl.setEnabled(true);
			importToFileNameBtn.setEnabled(true);
			importToLbl.setEnabled(true);
			importToFileNameTxt.setEnabled(true);

			// Persistent
			if (importToCombo.getSelectedIndex()==1) {
				databaseDirectoryTxt.setEnabled(true);
				databaseNameTxt.setEnabled(true);
				databaseDirLbl.setEnabled(true);
				databaseNameLbl.setEnabled(true);
				importToDatabaseDirectoryBtn.setEnabled(true);
				
				// DB Name and dir not null
				if ((!databaseDirectoryTxt.getText().equals("")) &&
						(!databaseNameTxt.getText().equals(""))){
					okBtn.setEnabled(true);
				}
			} else {
				// Filename not null
				if (!importToFileNameTxt.getText().equals("")) {
					okBtn.setEnabled(true);
				}
			}
		}
		
		// Create Empty
		if (CreateEmptyBtn.isSelected()) {
			okBtn.setEnabled(true);
		}
		
		// restore focus
		if ((FocusOwner!=null) && FocusOwner.isFocusable()) {
			FocusOwner.requestFocusInWindow();
		}
	}
	
	
	/**
	 * Actionlistener to enable / disable fields
	 */
	ActionListener enableDisableActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			enableDisableWidgets();
		}
	};
	
	/**
	 * Documentlistener to enable / disable fields
	 */
	DocumentListener enableDisableDocumentListener = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			enableDisableWidgets();
		}
		public void insertUpdate(DocumentEvent e) {
			enableDisableWidgets();
		}
		public void removeUpdate(DocumentEvent e) {
			enableDisableWidgets();
		}
	};
	
	
	/**
	 * Actionlistener for ok button
	 */
	ActionListener okAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			boolean success = false;
			
			// Open Database
			if (openDatabaseBtn.isSelected()) {
				
				// Validate file name
				File f = new File(openDatabaseFileNameTxt.getText());
				if (f.exists()) {
					usersChoice = DB_OPEN_DATABASE;
					success = true;
				} else {
					JOptionPane.showMessageDialog(
										welcomedlg,
										"File does not exist. Please check the File Name box.",
										"Error - GUESS",
										JOptionPane.ERROR_MESSAGE);
					return;
				}
				

			// Import Graph
			} else if (ImportGraphBtn.isSelected()) {
				
				if (importToCombo.getSelectedIndex()==0) {
					// Validate file name
					File f = new File(importToFileNameTxt.getText());
					if (f.exists()) {
						usersChoice = DB_IMPORT_GRAPH_MEMORY;
						success = true;
					} else {
						JOptionPane.showMessageDialog(
											welcomedlg,
											"File does not exist. Please check the File Name box.",
											"Error - GUESS",
											JOptionPane.ERROR_MESSAGE);
						return;
					}
						
				// import persistent
				} else if (importToCombo.getSelectedIndex()==1) {
					File f = new File(databaseDirectoryTxt.getText());
					if ((!f.exists()) || (!f.isDirectory())) {
						JOptionPane.showMessageDialog(
										null,
										"Directory does not exist. Please check the Directory box.",
										"Error - GUESS",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (databaseNameTxt.getText().equals("")) {
						JOptionPane.showMessageDialog(
										null,
										"You must declare a database name. Please insert a name to the Name box.",
										"Error - GUESS",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					f = new File(databaseDirectoryTxt.getText() + File.separatorChar +
							databaseNameTxt.getText() + ".properties");
					if (f.exists()) {
						int yn = JOptionPane.showConfirmDialog(null,
								"Database exists, overwrite?",
								"Overwrite Database - GUESS",
								JOptionPane.YES_NO_OPTION);
						if (yn == JOptionPane.NO_OPTION) {
							return;
						} else {
							usersChoice = DB_IMPORT_GRAPH_PERSISTENT;
							success = true;
						}
					} else {
						usersChoice = DB_IMPORT_GRAPH_PERSISTENT;
						success = true;
					}
				}
				
			// Create empty db
			} else if (CreateEmptyBtn.isSelected()) {
				usersChoice = DB_CREATE_EMPTY;
				success = true;
			}
				
			if (success) {
				// Save preferences for next time
				setPreferences();
				setVisible(false);
			}
		}
	};
	
	/**
	 * Actionlistener for cancel button
	 */
	ActionListener cancelAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Exit Guess
			System.exit(0);
		}
	};
	
	
	/**
	 * Actionlistener Open Database browse button
	 */
	ActionListener openDatabaseBrowseAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Browse for database file
			JFileChooser chooser = null;
			try {
				chooser = new JFileChooser(new 
						File(openDatabaseFileNameTxt.getText()).getCanonicalPath());
				
				SunFileFilter filter = new SunFileFilter();
			    filter.addExtension("properties");
			    chooser.setFileFilter(filter);
			    
				int returnVal = chooser.showOpenDialog(welcomedlg);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						openDatabaseFileNameTxt.setText(chooser.getSelectedFile().
								getCanonicalPath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			} catch (IOException e2) {
			}
			
			// Enable or disable fields
			enableDisableWidgets();
		}
	};
	
	/**
	 * Actionlistener Import Graph browse button
	 */
	ActionListener importGraphBrowseAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			// Browse for graph file to import
			JFileChooser chooser = null;
			try {
				chooser = new JFileChooser(new 
						File(importToFileNameTxt.getText()).getCanonicalPath());
			
				int returnVal = chooser.showOpenDialog(welcomedlg);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						importToFileNameTxt.setText(chooser.getSelectedFile().
								getCanonicalPath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			} catch (IOException e2) {
			}
			
			// Enable or disable fields
			enableDisableWidgets();
		}
	};
	
	/**
	 * Actionlistener Import Graph DB Directory browse button
	 */
	ActionListener importGraphDBDirectoryBrowseAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Browse for directory
			FolderChooser chooser = new FolderChooser();
		    int returnVal = chooser.showOpenDialog(welcomedlg);
		    if(returnVal == FolderChooser.APPROVE_OPTION) {
		       try {
		    	   databaseDirectoryTxt.setText(chooser.getSelectedFile().getCanonicalPath());
		       } catch (IOException e1) {
		    	   e1.printStackTrace();
		       }
		    }
			
			// Enable or disable fields
			enableDisableWidgets();
		}
	};
	
	
	KeyListener enterListener = new KeyAdapter()
	{
		public void keyReleased( KeyEvent e ) {
			if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					okBtn.doClick();
			}
		}
	};
	
	/**
	 * Return the users Choice
	 * @return See DB_OPEN_DATABASE, DB_IMPORT_GRAPH_MEMORY, ...
	 */
	public int getUsersChoice() {
		return usersChoice;
	}
	
	/**
	 * Return the file name of the field to
	 * open a database
	 * @return
	 */
	public String getFileNameOpenDatabase() {
		return openDatabaseFileNameTxt.getText().substring(0, 
				openDatabaseFileNameTxt.getText().length() - 11);
	}
	
	/**
	 * Return the file name of a graph to import
	 * @return
	 */
	public String getFileNameImportGraph() {
		return importToFileNameTxt.getText();
	}
	
	/**
	 * Return a name of a new persisten db
	 * @return
	 */
	public String getNameImportGraph() {
		return databaseNameTxt.getText();
	}
	
	/**
	 * Return a directory of a new persistent db
	 * @return
	 */
	public String getDirectoryImportGraph() {
		return databaseDirectoryTxt.getText();
	}

    private void setIImage(String image) {
	try {
	    // hack to make it work with jdk 1.5
	    ImageIcon imageIcon = 
		new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(image)));
	    Method m = 
		getClass().getMethod("setImageIcon",
				     new Class[]{Class.forName("java.awt.Image")});
	    m.invoke(this,imageIcon.getImage());
	    //this.setIconImage(imageIcon.getImage());
	} catch (Exception ex) {
	}
    }

	/**
	 * Init the gui elements
	 */
	private void initGUI() {
		//Set Look & Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Set Window Icon
		setIImage("images/guess-icon.png");

		
		// Set default button
		getRootPane().setDefaultButton(okBtn);
		
		// Set title
		setTitle("Choose Database - GUESS");	
		
		try {
			GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.columnWidths = new int[] {12, 11, 11, 7, 11, 7, 12};
			thisLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0};
			thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0};
			thisLayout.rowHeights = new int[] {50, 12, 7, 11, 7, 7, 11, 3, 3, 6, 3, 6, 3, 6, 3, 11, 7, 17, 7, 12};
			getContentPane().setLayout(thisLayout);
			
			{
				openDatabaseBtn = new JRadioButton();
				getContentPane().add(openDatabaseBtn, new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				openDatabaseBtn.setText("Open Existing Database");
				openDatabaseBtn.setMnemonic(java.awt.event.KeyEvent.VK_P);
				openDatabaseBtn.addActionListener(enableDisableActionListener);
				getButtonGroup().add(openDatabaseBtn);
				openDatabaseBtn.addKeyListener(enterListener);
			}
			{
				ImportGraphBtn = new JRadioButton();
				getContentPane().add(ImportGraphBtn, new GridBagConstraints(1, 7, 3, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				ImportGraphBtn.setText("Import Graph to New Database");
				ImportGraphBtn.setMnemonic(java.awt.event.KeyEvent.VK_G);
				ImportGraphBtn.addActionListener(enableDisableActionListener);
				getButtonGroup().add(ImportGraphBtn);
				ImportGraphBtn.addKeyListener(enterListener);
			}
			{
				CreateEmptyBtn = new JRadioButton();
				getContentPane().add(CreateEmptyBtn, new GridBagConstraints(1, 16, 3, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				CreateEmptyBtn.setText("Create Empty Database");
				CreateEmptyBtn.setMnemonic(java.awt.event.KeyEvent.VK_E);
				CreateEmptyBtn.addActionListener(enableDisableActionListener);
				getButtonGroup().add(CreateEmptyBtn);
				CreateEmptyBtn.addKeyListener(enterListener);
			}
			{
				welcomeLbl = new JLabel();
				getContentPane().add(welcomeLbl, new GridBagConstraints(1, 2, 5, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				welcomeLbl.setText("What kind of database do you want Guess to start with?");
			}
			{
				fileNameOpenDatabaseLbl = new JLabel();
				getContentPane().add(fileNameOpenDatabaseLbl, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				fileNameOpenDatabaseLbl.setText("File Name");
				fileNameOpenDatabaseLbl.setEnabled(false);
				fileNameOpenDatabaseLbl.setLabelFor(openDatabaseFileNameTxt);
			}
			{
				openDatabaseFileNameTxt = new JTextField();
				getContentPane().add(openDatabaseFileNameTxt, new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				openDatabaseFileNameTxt.setEnabled(false);
				openDatabaseFileNameTxt.getDocument().addDocumentListener(enableDisableDocumentListener);
				openDatabaseFileNameTxt.addKeyListener(enterListener);
			}
			{
				browseOpenDatabaseFileNameBtn = new JButton();
				getContentPane().add(browseOpenDatabaseFileNameBtn, new GridBagConstraints(5, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				browseOpenDatabaseFileNameBtn.setText("Browse...");
				browseOpenDatabaseFileNameBtn.setEnabled(false);
				browseOpenDatabaseFileNameBtn.setMnemonic(java.awt.event.KeyEvent.VK_B);
				browseOpenDatabaseFileNameBtn.addActionListener(enableDisableActionListener);
				browseOpenDatabaseFileNameBtn.addActionListener(openDatabaseBrowseAction);
			}
			{
				fileNameImportGraphLbl = new JLabel();
				getContentPane().add(fileNameImportGraphLbl, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				fileNameImportGraphLbl.setText("File Name");
				fileNameImportGraphLbl.setEnabled(false);
			}
			{
				importToLbl = new JLabel();
				getContentPane().add(importToLbl, new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				importToLbl.setText("Import To");
				importToLbl.setEnabled(false);
			}
			{
				databaseNameLbl = new JLabel();
				getContentPane().add(databaseNameLbl, new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				databaseNameLbl.setText("Name");
				databaseNameLbl.setEnabled(false);
			}
			{
				databaseDirLbl = new JLabel();
				getContentPane().add(databaseDirLbl, new GridBagConstraints(1, 14, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				databaseDirLbl.setText("Directory");
				databaseDirLbl.setEnabled(false);
			}
			{
				ComboBoxModel importToComboModel = 
					new DefaultComboBoxModel(
							new String[] { "Memory", "Persistent" });
				importToCombo = new JComboBox();
				importToLbl.setLabelFor(importToCombo);
				getContentPane().add(importToCombo, new GridBagConstraints(3, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				importToCombo.setModel(importToComboModel);
				importToCombo.setEnabled(false);
				importToCombo.addActionListener(enableDisableActionListener);
				importToCombo.addKeyListener(enterListener);
			}
			{
				importToFileNameTxt = new JTextField();
				fileNameImportGraphLbl.setLabelFor(importToFileNameTxt);
				getContentPane().add(importToFileNameTxt, new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				importToFileNameTxt.setEnabled(false);
				importToFileNameTxt.getDocument().addDocumentListener(enableDisableDocumentListener);
				importToFileNameTxt.addKeyListener(enterListener);
			}
			{
				importToFileNameBtn = new JButton();
				getContentPane().add(importToFileNameBtn, new GridBagConstraints(5, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				importToFileNameBtn.setText("Browse...");
				importToFileNameBtn.setEnabled(false);
				importToFileNameBtn.setMnemonic(java.awt.event.KeyEvent.VK_R);
				importToFileNameBtn.addActionListener(enableDisableActionListener);
				importToFileNameBtn.addActionListener(importGraphBrowseAction);
			}
			{
				databaseNameTxt = new JTextField();
				databaseNameLbl.setLabelFor(databaseNameTxt);
				getContentPane().add(databaseNameTxt, new GridBagConstraints(3, 12, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				databaseNameTxt.setEnabled(false);
				databaseNameTxt.getDocument().addDocumentListener(enableDisableDocumentListener);
				databaseNameTxt.addKeyListener(enterListener);
			}
			{
				databaseDirectoryTxt = new JTextField();
				databaseDirLbl.setLabelFor(databaseDirectoryTxt);
				getContentPane().add(databaseDirectoryTxt, new GridBagConstraints(3, 14, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				databaseDirectoryTxt.setEnabled(false);
				databaseDirectoryTxt.getDocument().addDocumentListener(enableDisableDocumentListener);
				databaseDirectoryTxt.addKeyListener(enterListener);
			}
			{
				importToDatabaseDirectoryBtn = new JButton();
				getContentPane().add(importToDatabaseDirectoryBtn, new GridBagConstraints(5, 14, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				importToDatabaseDirectoryBtn.setText("Browse...");
				importToDatabaseDirectoryBtn.setEnabled(false);
				importToDatabaseDirectoryBtn.setMnemonic(java.awt.event.KeyEvent.VK_W);
				importToDatabaseDirectoryBtn.addActionListener(enableDisableActionListener);
				importToDatabaseDirectoryBtn.addActionListener(importGraphDBDirectoryBrowseAction);
			}
			{
				okBtn = new JButton();
				getContentPane().add(okBtn, new GridBagConstraints(3, 18, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				okBtn.setText("Ok");
				okBtn.setSize(79, 23);
				okBtn.setPreferredSize(new java.awt.Dimension(79, 23));
				okBtn.setMinimumSize(new java.awt.Dimension(79, 23));
				okBtn.setMnemonic(java.awt.event.KeyEvent.VK_O);
				okBtn.addActionListener(okAction);
			}
			{
				cancelBtn = new JButton();
				getContentPane().add(cancelBtn, new GridBagConstraints(5, 18, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				getContentPane().add(getHeaderText(), new GridBagConstraints(3, 0, 4, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				getContentPane().add(getHeaderLogo(), new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				getContentPane().add(getHeaderSpacer(), new GridBagConstraints(2, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

				cancelBtn.setText("Cancel");
				cancelBtn.setSize(79, 23);
				cancelBtn.setPreferredSize(new java.awt.Dimension(79, 23));
				cancelBtn.setMinimumSize(new java.awt.Dimension(79, 23));
				cancelBtn.setMnemonic(java.awt.event.KeyEvent.VK_C);
				cancelBtn.addActionListener(cancelAction);
			}

			this.setSize(382, 420);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private ButtonGroup getButtonGroup() {
		if(buttonGroup == null) {
			buttonGroup = new ButtonGroup();
		}
		return buttonGroup;
	}

	private JLabel getHeaderSpacer() {
		if(headerSpacer == null) {
			headerSpacer = new JLabel() {
				private static final long serialVersionUID = 1L;
				public void paintComponent (Graphics g) {
				      super.paintComponent (g);
				      g.drawImage (((ImageIcon) getIcon()).getImage(), 0, 0, getWidth (), getHeight (), null);
				   }
			};
			headerSpacer.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("images/guess-welcome-spacer.png"))));
		}
		return headerSpacer;
	}
	
	private JLabel getHeaderText() {
		if(headerText == null) {
			headerText = new JLabel();
			headerText.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("images/guess-welcome-text.png"))));
		}
		return headerText;
	}
	
	private JLabel getHeaderLogo() {
		if(headerLogo == null) {
			headerLogo = new JLabel();
			headerLogo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("images/guess-welcome-logo.png"))));
		}
		return headerLogo;
	}

}
