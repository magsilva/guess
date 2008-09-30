package com.hp.hpl.guess.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.Version;
import com.hp.hpl.guess.Node;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;

import com.hp.hpl.guess.freehep.HEPDialog;
import com.hp.hpl.guess.piccolo.GFrame;

import com.jgoodies.looks.*;

public class GMenuBar extends JMenuBar {

	String[] fileItems = new String[] { "Export Image...", "Export Graph..." };

	char[] fileShortcuts = { 'I', 'R' };

	String[] layoutItems = new String[] { "Bin Pack", "GEM", "Circular",
			"Physics", "Kamada-Kawai", "Fruchterman-Rheingold", "Spring",
			"MDS", "Random", "Radial..." };

	JCheckBoxMenuItem logItem = new JCheckBoxMenuItem("Save Logfile...");

	HEPDialog hd = new HEPDialog(null);

	private Preferences userPrefs = Preferences.userNodeForPackage(getClass());

	public String getInputFromUser(Object question, String title, Object def) {
		String toRet = (String) JOptionPane.showInputDialog(null, question,
				title + " - GUESS", JOptionPane.QUESTION_MESSAGE, null, null,
				def);
		return (toRet);
	}

	protected JMenu fileMenu = new JMenu("File");
	protected JMenu editMenu = new JMenu("Edit");
	protected JMenu layoutMenu = new JMenu("Layout");
	protected JMenu scriptMenu = new JMenu("Script");
	protected JMenu viewMenu = new JMenu("View");
	protected JMenu helpMenu = new JMenu("Help");

	public boolean displayProtected() {
		// System.out.println(Guess.getAppletMode() + " " +
		// Guess.getSignedAppletMode());
		if ((Guess.getAppletMode() == true)
				&& (Guess.getSignedAppletMode() == false)) {
			return (false);
		} else {
			return (true);
		}
	}

	/**
	 * Loads the MRU of the scripts and rebuild the script menu
	 */
	private void rebuildScriptMenu() {
		scriptMenu.removeAll();

		ActionListener scriptListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Run Script...")) {
					runScript();
				} else if (event.getActionCommand().equals("runScript1")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 1, ""));
				} else if (event.getActionCommand().equals("runScript2")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 2, ""));
				} else if (event.getActionCommand().equals("runScript3")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 3, ""));
				} else if (event.getActionCommand().equals("runScript4")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 4, ""));
				} else if (event.getActionCommand().equals("runScript5")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 5, ""));
				} else if (event.getActionCommand().equals("runScript6")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 6, ""));
				} else if (event.getActionCommand().equals("runScript7")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 7, ""));
				} else if (event.getActionCommand().equals("runScript8")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 8, ""));
				} else if (event.getActionCommand().equals("runScript9")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 9, ""));
				} else if (event.getActionCommand().equals("runScript10")) {
					Guess.getInterpreter().execfile(
							userPrefs.get("scriptMRU" + 10, ""));

				}
			}
		};

		JMenuItem runScript = new JMenuItem("Run Script...");
		runScript.addActionListener(scriptListener);
		scriptMenu.add(runScript);

		// Add MRUs
		for (int i = 1; i < 11; i++) {
			if (!userPrefs.get("scriptMRU" + i, "").equals("")) {
				if (i == 1) {
					scriptMenu.addSeparator();
				}
				File fn = new File(userPrefs.get("scriptMRU" + i, ""));
				JMenuItem jmi = new JMenuItem(i + ": " + fn.getName());
				jmi.addActionListener(scriptListener);
				jmi.setActionCommand("runScript" + i);
				jmi.setMnemonic(Integer.toString(i + 10).charAt(1));
				jmi.setAccelerator(KeyStroke.getKeyStroke(Integer.toString(
						i + 10).charAt(1), Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask(), false));
				scriptMenu.add(jmi);
			}
		}

	}

	/**
	 * Create the menu view
	 */
	private void buildViewMenu() {
		ActionListener viewListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Center")) {
					VisFactory.getFactory().getDisplay().center();
				} else if (event.getActionCommand().equals("Information Window")) {
					if (InfoWindow.isIWVisible()) {
						Guess.getMainUIWindow().close(InfoWindow.getInfoWindow());
						if (InfoWindow.getInfoWindow().getWindow()!=null) {
							InfoWindow.getInfoWindow().getWindow().setVisible(false);
						}
						userPrefs.putBoolean("openInformationWindow", false);
					} else {
						InfoWindow.create();
						userPrefs.putBoolean("openInformationWindow", true);
					}
				} else if (event.getActionCommand().equals("Console")) {
					if (Guess.getJythonConsole().isCVisible()) {
						Guess.getMainUIWindow().close(Guess.getJythonConsole());
						if (Guess.getJythonConsole().getWindow()!=null) {
							Guess.getJythonConsole().getWindow().setVisible(false);
						}
						userPrefs.putBoolean("openConsole", false);
					} else {
						userPrefs.putBoolean("openConsole", true);
						Guess.getJythonConsole().create();
					}
				} else if (event.getActionCommand().equals("Fullscreen")) {
					JCheckBoxMenuItem fullscreen = (JCheckBoxMenuItem) event.getSource();
					Guess.getMainUIWindow().setFullScreenMode(fullscreen.getState());
					userPrefs.putBoolean("openFullscreen", fullscreen.getState());
				}
			}
		};

		JMenuItem centerMenuItem = new JMenuItem("Center");
		centerMenuItem.addActionListener(viewListener);
		centerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false));
		centerMenuItem.setMnemonic('c');
		viewMenu.add(centerMenuItem);
		viewMenu.addSeparator();
		
		JCheckBoxMenuItem informationWindowMenuItem = new JCheckBoxMenuItem("Information Window");
		informationWindowMenuItem.addActionListener(viewListener);
		informationWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, false));
		informationWindowMenuItem.setMnemonic('i');
		informationWindowMenuItem.setState(userPrefs.getBoolean("openInformationWindow", false));
		viewMenu.add(informationWindowMenuItem);

		if (Guess.guiMode) { // use internal console
		JCheckBoxMenuItem consoleMenuItem = new JCheckBoxMenuItem("Console");
		consoleMenuItem.addActionListener(viewListener);
		consoleMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false));
		consoleMenuItem.setState(userPrefs.getBoolean("openConsole", true));
		consoleMenuItem.setMnemonic('o');
		viewMenu.add(consoleMenuItem);
		}
		viewMenu.addSeparator();
		
		JCheckBoxMenuItem fullscreenMenuItem = new JCheckBoxMenuItem("Fullscreen");
		fullscreenMenuItem.addActionListener(viewListener);
		fullscreenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0, false));
		fullscreenMenuItem.setMnemonic('f');
		fullscreenMenuItem.setState(userPrefs.getBoolean("openFullscreen", false));
		viewMenu.add(fullscreenMenuItem);	
	}
	
	private void buildFileMenu() {

		ActionListener printListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Exit")) {
					for (int i = 0; i < Guess.getMainUIWindow().getWindowListeners().length; i++) {
						Guess.getMainUIWindow().getWindowListeners()[i].windowClosing(new WindowEvent(Guess.getMainUIWindow(), WindowEvent.WINDOW_CLOSING));
					}
					
				} else if (event.getActionCommand().equals("Export Image...")) {
					if (VisFactory.getUIMode() == VisFactory.PICCOLO) {
						hd.showHEPDialog(null, "Export Image - GUESS",
								(GFrame) VisFactory.getFactory().getDisplay(),
								"output.jpg", true);
					} else {
						StatusBar
								.setErrorStatus("This method is only supported in piccolo mode right now");
					}
				} else if (event.getActionCommand().equals("Export Graph...")) {
					saveGDF();
				} else if (event.getActionCommand().equals("Save Logfile...")) {
					logToggle();
				}
			}
		};

		ActionListener loadListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("GDF")) {
					loadFromFile("GDF");
				} else if (event.getActionCommand().equals("XML/GML")) {
					loadFromFile("XML");
				} else if (event.getActionCommand().equals("Pajek")) {
					loadFromFile("Pajek");
				}
			}
		};

		JMenu load = new JMenu("Import Graph");
		load.setMnemonic('m');
		JMenuItem l1 = new JMenuItem("GDF");
		l1.addActionListener(loadListener);
		l1.setMnemonic('g');
		load.add(l1);
		l1 = new JMenuItem("XML/GML");
		l1.setMnemonic('x');
		l1.addActionListener(loadListener);
		load.add(l1);
		l1 = new JMenuItem("Pajek");
		l1.setMnemonic('p');
		l1.addActionListener(loadListener);
		load.add(l1);

		fileMenu.add(load);
		if (!displayProtected()) {
			load.setEnabled(false);
		}

		for (int i = 0; i < fileItems.length; i++) {
			JMenuItem item = null;
			if (i < fileShortcuts.length) {
				item = new JMenuItem(fileItems[i], fileShortcuts[i]);
				item.setAccelerator(KeyStroke.getKeyStroke(fileShortcuts[i],
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),
						false));

			} else {
				item = new JMenuItem(fileItems[i]);
			}

			item.addActionListener(printListener);
			fileMenu.add(item);
			if (!displayProtected()) {
				item.setEnabled(false);
			}
		}
		fileMenu.addSeparator();

		// added the log button sep. since we need to
		// access it later
		logItem.addActionListener(printListener);
		logItem.setMnemonic('l');
		fileMenu.add(logItem);
		if (!displayProtected()) {
			logItem.setEnabled(false);
		}
		fileMenu.addSeparator();

		// we want exit to always be last
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setMnemonic('x');
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask(), false));
		
		exitMenuItem.addActionListener(printListener);
		fileMenu.add(exitMenuItem);
	}
	
	private void buildLayoutMenu() {

		ActionListener layoutListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String command = event.getActionCommand();
				StatusBar.runProgressBar(true);
				try {
					if (command.equals("GEM")) {
						Guess.getGraph().gemLayout();
					} else if (command.equals("Radial...")) {
						String centerN = getInputFromUser(
								"Please enter a node to " + "use a the center",
								"User input", "");
						if (centerN != null) {
							Node x = Guess.getGraph().getNodeByName(centerN);
							if (x != null) {
								Guess.getGraph().radialLayout(x);
								StatusBar.setStatus("radialLayout(" + centerN
										+ ")");
							} else {
								StatusBar
										.setErrorStatus("Can't find node named "
												+ centerN);
							}
						}
					} else if (command.equals("Circular")) {
						Guess.getGraph().circleLayout();
						StatusBar.setStatus("circleLayout()");
					} else if (command.equals("Physics")) {
						Guess.getGraph().physicsLayout();
						StatusBar.setStatus("physicsLayout()");
					} else if (command.equals("Kamada-Kawai")) {
						Guess.getGraph().jkkLayout1();
						StatusBar.setStatus("jkkLayout1()");
					} else if (command.equals("Fruchterman-Rheingold")) {
						Guess.getGraph().frLayout();
						StatusBar.setStatus("frLayout()");
					} else if (command.equals("Spring")) {
						Guess.getGraph().springLayout();
						StatusBar.setStatus("springLayout()");
					} else if (command.equals("MDS")) {
						Guess.getGraph().mdsLayout();
						StatusBar.setStatus("mdsLayout()");
					} else if (command.equals("Random")) {
						Guess.getGraph().randomLayout();
						StatusBar.setStatus("randomLayout()");
					} else if (command.equals("Bin Pack")) {
						Guess.getGraph().binPackLayout();
						StatusBar.setStatus("binPackLayout()");
					}
				} catch (Exception e) {
					StatusBar.setErrorStatus(e.toString());
				}
				StatusBar.runProgressBar(false);
			}
		};

		for (int i = 0; i < layoutItems.length; i++) {
			JMenuItem item = new JMenuItem(layoutItems[i]);
			item.addActionListener(layoutListener);
			layoutMenu.add(item);
		}
	}
	
	private void buildScriptMenu() {
		rebuildScriptMenu();
	}
	
	private void buildHelpMenu() {

		ActionListener helpListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("About GUESS")) {
					// Show about dialog
					AboutDialog aboutDlg = new AboutDialog();
					aboutDlg.setVisible(true);
				} else if (event.getActionCommand().equals("Error Log")) {
					// Show error log
					ExceptionWindow.getExceptionWindow(null).setVisible(true);
				} else if (event.getActionCommand().equals("GUESS Wiki")) {
					// Open wiki in default browser
					// needs java 1.6
					Desktop dsk = Desktop.getDesktop();
					try {
						dsk.browse(new URI("http://guess.wikispot.org/"));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				} else if (event.getActionCommand().equals("GUESS Homepage")) {
					// Open wiki in default browser
					// needs java 1.6
					Desktop dsk = Desktop.getDesktop();
					try {
						dsk.browse(new URI("http://graphexploration.cond.org/"));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		};

		
		JMenuItem errorLogMenuItem = new JMenuItem("Error Log");
		errorLogMenuItem.addActionListener(helpListener);
		errorLogMenuItem.setMnemonic('r');
		helpMenu.add(errorLogMenuItem);
		helpMenu.addSeparator();

		JMenuItem homepageMenuItem = new JMenuItem("GUESS Homepage");
		homepageMenuItem.addActionListener(helpListener);
		homepageMenuItem.setMnemonic('h');
		helpMenu.add(homepageMenuItem);
		
		JMenuItem wikiMenuItem = new JMenuItem("GUESS Wiki");
		wikiMenuItem.addActionListener(helpListener);
		wikiMenuItem.setMnemonic('w');
		helpMenu.add(wikiMenuItem);
		
		helpMenu.addSeparator();

		JMenuItem aboutMenuItem = new JMenuItem("About GUESS");
		aboutMenuItem.addActionListener(helpListener);
		aboutMenuItem.setMnemonic('a');
		helpMenu.add(aboutMenuItem);
	}
	
	private void buildEditMenu() {

		ActionListener editListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Background Color...")) {
					FrameListener fl = VisFactory.getFactory().getDisplay();
					Color c = JColorChooser.showDialog(null,
							"Please pick a color - GUESS", fl
									.getDisplayBackground());
					if (c != null) {
						fl.setDisplayBackground(c);
						StatusBar.setStatus("v.setDisplayBackground(\""
								+ c.getRed() + "," + c.getGreen() + ","
								+ c.getBlue() + "\")");
					}
				} else if (event.getActionCommand().equals("Modify Field...")) {
					FieldModWindow.getFieldModWindow();
				} else if (event.getActionCommand().equals("Toggle Arrows")) {
					VisFactory.getFactory().setDirected(
							!VisFactory.getFactory().getDirected());
				}
			}
		};
		JMenuItem modifyFieldMenuItem = new JMenuItem("Modify Field...");
		modifyFieldMenuItem.addActionListener(editListener);
		modifyFieldMenuItem.setMnemonic('m');
		editMenu.add(modifyFieldMenuItem);
		editMenu.addSeparator();
		
		JMenuItem toggleArrowsMenuItem = new JMenuItem("Toggle Arrows");
		toggleArrowsMenuItem.addActionListener(editListener);
		toggleArrowsMenuItem.setMnemonic('t');
		editMenu.add(toggleArrowsMenuItem);
		JMenuItem bgcolor = new JMenuItem("Background Color...");
		bgcolor.setMnemonic('b');
		bgcolor.addActionListener(editListener);
		editMenu.add(bgcolor);
		editMenu.addSeparator();
	}
	
	public GMenuBar() {
		this.putClientProperty(Options.HEADER_STYLE_KEY, Boolean.TRUE);

		fileMenu.setMnemonic('f');
		editMenu.setMnemonic('e');
		layoutMenu.setMnemonic('l');
		scriptMenu.setMnemonic('s');
		viewMenu.setMnemonic('v');
		helpMenu.setMnemonic('h');

		buildFileMenu();
		buildEditMenu();
		buildLayoutMenu();
		buildScriptMenu();
		buildViewMenu();
		buildHelpMenu();

		super.add(fileMenu);
		super.add(editMenu);
		super.add(layoutMenu);
		super.add(scriptMenu);
		super.add(viewMenu);
		super.add(helpMenu);
	}

	public JMenu add(JMenu c) {
		JMenu temp = getMenu(getMenuCount() - 1);
		if (temp == helpMenu) {
			remove(getMenuCount() - 1);
		}
		super.add(c);
		super.add(helpMenu);
		return (c);
	}

	File prevRun = null;
	File prevLog = null;
	File prevGDF = null;
	File prevLoad = null;

	public void runScript() {
		SunFileFilter filter = new SunFileFilter();
		try {
			if (prevRun == null) {
				prevRun = new File(".");
			}
			JFileChooser chooser = new JFileChooser(prevRun.getCanonicalPath());
			filter.addExtension("py");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName = chooser.getSelectedFile().getAbsolutePath();

				// Is filename already in MRU List?
				if (!userPrefs.get("scriptMRU1", "").equals(fileName)
						&& !userPrefs.get("scriptMRU2", "").equals(fileName)
						&& !userPrefs.get("scriptMRU3", "").equals(fileName)
						&& !userPrefs.get("scriptMRU4", "").equals(fileName)
						&& !userPrefs.get("scriptMRU5", "").equals(fileName)
						&& !userPrefs.get("scriptMRU6", "").equals(fileName)
						&& !userPrefs.get("scriptMRU7", "").equals(fileName)
						&& !userPrefs.get("scriptMRU8", "").equals(fileName)
						&& !userPrefs.get("scriptMRU9", "").equals(fileName)
						&& !userPrefs.get("scriptMRU10", "").equals(fileName)) {

					// Shift all elements down
					userPrefs.put("scriptMRU10", userPrefs
							.get("scriptMRU9", ""));
					userPrefs
							.put("scriptMRU9", userPrefs.get("scriptMRU8", ""));
					userPrefs
							.put("scriptMRU8", userPrefs.get("scriptMRU7", ""));
					userPrefs
							.put("scriptMRU7", userPrefs.get("scriptMRU6", ""));
					userPrefs
							.put("scriptMRU6", userPrefs.get("scriptMRU5", ""));
					userPrefs
							.put("scriptMRU5", userPrefs.get("scriptMRU4", ""));
					userPrefs
							.put("scriptMRU4", userPrefs.get("scriptMRU3", ""));
					userPrefs
							.put("scriptMRU3", userPrefs.get("scriptMRU2", ""));
					userPrefs
							.put("scriptMRU2", userPrefs.get("scriptMRU1", ""));

					// Add current filename to 1st position
					userPrefs.put("scriptMRU1", fileName);

					// Rebuild menu
					rebuildScriptMenu();

					// Run script
					Guess.getInterpreter().execfile(fileName);
					prevRun = new File(fileName);
				}

			}
		} catch (IOException e) {
			ExceptionWindow.getExceptionWindow(e);
			JOptionPane.showMessageDialog(null, "Error loading file " + e,
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void saveGDF() {
		SunFileFilter filter = new SunFileFilter();
		try {
			if (prevGDF == null) {
				prevGDF = new File(".");
			}
			JFileChooser chooser = new JFileChooser(prevGDF.getCanonicalPath());
			filter.addExtension("gdf");
			chooser.setFileFilter(filter);
			chooser.setDialogTitle("Export Graph - GUESS");
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName = chooser.getSelectedFile().getAbsolutePath();
				Guess.getGraph().exportGDF(fileName);
				prevGDF = new File(fileName);
			}
		} catch (IOException e) {
			ExceptionWindow.getExceptionWindow(e);
			JOptionPane.showMessageDialog(null, "Error saving file " + e,
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void loadFromFile(String type) {
		SunFileFilter filter = new SunFileFilter();
		try {
			if (prevLoad == null) {
				prevLoad = new File(".");
			}
			JFileChooser chooser = new JFileChooser(prevLoad.getCanonicalPath());
			if (type.equals("GDF")) {
				filter.addExtension("gdf");
			} else if (type.equals("XML")) {
				filter.addExtension("xml");
				filter.addExtension("gml");
				filter.addExtension("graphml");
			} else if (type.equals("Pajek")) {
				filter.addExtension("net");
			}
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName = chooser.getSelectedFile().getAbsolutePath();
				if (type.equals("GDF")) {
					Guess.getGraph().makeFromGDF(fileName);
				} else if (type.equals("XML")) {
					Guess.getGraph().makeFromGML(fileName);
				} else if (type.equals("Pajek")) {
					Guess.getGraph().makeFromPajek(fileName);
				}
				prevLoad = new File(fileName);
				VisFactory.getFactory().getDisplay().center();
			}
		} catch (IOException e) {
			ExceptionWindow.getExceptionWindow(e);
			JOptionPane.showMessageDialog(null, "Error loading file " + e,
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void logToggle() {
		SunFileFilter filter = new SunFileFilter();
		if (logItem.isSelected() == false) {
			Guess.getInterpreter().stoplog();
			logItem.setSelected(false);
			StatusBar.setStatus("Logging stopped...");
			return;
		}

		try {
			if (prevLog == null) {
				prevLog = new File(".");
			}
			JFileChooser chooser = new JFileChooser(prevLog.getCanonicalPath());
			filter.addExtension("py");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				String fileExtension = filter.getExtension(f);
				String fileName = f.getAbsolutePath();
				if (fileExtension == null) {
					fileName = fileName + ".py";
					f = new File(fileName);
				}
				if (f.exists()) {
					int yn = JOptionPane.showConfirmDialog(null, "File "
							+ fileName + " exists, overwrite?", "Exists",
							JOptionPane.YES_NO_OPTION);
					if (yn == JOptionPane.NO_OPTION) {
						return;
					}
				}

				Guess.getInterpreter().log(fileName);
				prevLog = new File(fileName);
				logItem.setSelected(true);
				StatusBar.setStatus("Logging started...");
			}
		} catch (IOException e) {
			ExceptionWindow.getExceptionWindow(e);
			JOptionPane.showMessageDialog(null, "Error loading file " + e,
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
