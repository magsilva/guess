package com.hp.hpl.guess.ui;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.action.GAction;
import com.hp.hpl.guess.action.GActionManager;
import com.hp.hpl.guess.action.GStateAction;
import com.hp.hpl.guess.freehep.HEPDialog;
import com.hp.hpl.guess.piccolo.GFrame;
import com.hp.hpl.guess.piccolo.PreviewPopup;
import com.hp.hpl.guess.storage.StorageFactory;
import com.jgoodies.looks.Options;
import com.jidesoft.swing.JideMenu;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicMenuUI;


public class GMenuBar extends JMenuBar {

	private static final long serialVersionUID = 2412310463167673835L;

	String[] layoutItems = new String[] { "Bin Pack", "GEM", "Circular",
			"Physics", "Kamada-Kawai", "Fruchterman-Rheingold", "Spring",
			"MDS", "Random", "Radial...", "Neighbour..." };

	JCheckBoxMenuItem logItem = new JCheckBoxMenuItem("Save Logfile...");

	HEPDialog hd = new HEPDialog(null);

	private Preferences userPrefs = Preferences.userNodeForPackage(getClass());

	public String getInputFromUser(Object question, String title, Object def) {
		String toRet = (String) JOptionPane.showInputDialog(null, question,
				title + " - GUESS", JOptionPane.QUESTION_MESSAGE, null, null,
				def);
		return (toRet);
	}
	
    

	protected JideMenu fileMenu = new JideMenu("File");
	protected JMenu editMenu = new JMenu("Edit");
	protected JMenu layoutMenu = new JMenu("Layout");
	protected JMenu scriptMenu = new JMenu("Script");
	protected JMenu toolMenu = new JMenu("Tool");
	protected JMenu viewMenu = new JMenu("View");
	protected JMenu helpMenu = new JMenu("Help");

	
	protected JPanel statePanel = new JPanel();
	

	final StateSelectorPopup stateSelector = new StateSelectorPopup();
	
	
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
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							runScript();
						}
					};
					GActionManager.runAction(scriptAction, "Run Script...");
					
				} else if (event.getActionCommand().equals("runScript1")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 1, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 1");
					
				} else if (event.getActionCommand().equals("runScript2")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 2, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 2");
					
				} else if (event.getActionCommand().equals("runScript3")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 3, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 3");
					
				} else if (event.getActionCommand().equals("runScript4")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 4, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 4");
					
				} else if (event.getActionCommand().equals("runScript5")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 5, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 5");
					
				} else if (event.getActionCommand().equals("runScript6")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 6, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 6");
					
				} else if (event.getActionCommand().equals("runScript7")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 7, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 7");
					
				} else if (event.getActionCommand().equals("runScript8")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 8, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 8");
					
				} else if (event.getActionCommand().equals("runScript9")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 9, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 9");
					
				} else if (event.getActionCommand().equals("runScript10")) {
					GStateAction scriptAction = new GStateAction() {
						public void actionContent() {
							Guess.getInterpreter().execfile(
									userPrefs.get("scriptMRU" + 10, ""));
						}
					};
					GActionManager.runAction(scriptAction, "Run Script 10");

				}
			}
		};

		JMenuItem runScript = new JMenuItem("Run Script...");
		runScript.addActionListener(scriptListener);
		runScript.setIcon(new ImageIcon(getClass().getResource("/images/text-x-generic.png")));
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
				} else if (event.getActionCommand().equals("Video Window")) {
					if (VideoWindow.isVWVisible()) {
						Guess.getMainUIWindow().close(VideoWindow.getVideoWindow());
						if (VideoWindow.getVideoWindow().getWindow()!=null) {
							VideoWindow.getVideoWindow().getWindow().setVisible(false);
						}
						userPrefs.putBoolean("openVideoWindow", false);
					} else {
						VideoWindow.create();
						userPrefs.putBoolean("openVideoWindow", true);
					}
				} else if (event.getActionCommand().equals("Fullscreen")) {
					JCheckBoxMenuItem fullscreen = (JCheckBoxMenuItem) event.getSource();
					Guess.getMainUIWindow().setFullScreenMode(fullscreen.getState());
					userPrefs.putBoolean("openFullscreen", fullscreen.getState());
				} else if (event.getActionCommand().equals("Overview")) {
					PreviewPopup overview = new PreviewPopup((GFrame) VisFactory.getFactory().getDisplay(), true);
					overview.showUpInCorner(viewMenu.getParent(), "", 
							viewMenu.getX(), 
							viewMenu.getY());
				} else if (event.getActionCommand().equals("Zoom")) {
					Guess.setZooming(Guess.ZOOMING_ZOOM);
				} else if (event.getActionCommand().equals("Space")) {
					Guess.setZooming(Guess.ZOOMING_SPACE);		
				}
				
				
			}
		};

		JMenuItem centerMenuItem = new JMenuItem("Center");
		centerMenuItem.addActionListener(viewListener);
		centerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false));
		centerMenuItem.setMnemonic('c');
		centerMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/view-refresh.png")));
		viewMenu.add(centerMenuItem);
		viewMenu.addSeparator();
		
		JCheckBoxMenuItem informationWindowMenuItem = new JCheckBoxMenuItem("Information Window");
		informationWindowMenuItem.addActionListener(viewListener);
		informationWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, false));
		informationWindowMenuItem.setMnemonic('i');
		informationWindowMenuItem.setState(userPrefs.getBoolean("openInformationWindow", false));
		informationWindowMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/format-justify-fill.png")));
		viewMenu.add(informationWindowMenuItem);

		if (Guess.guiMode) { // use internal console
		JCheckBoxMenuItem consoleMenuItem = new JCheckBoxMenuItem("Console");
		consoleMenuItem.addActionListener(viewListener);
		consoleMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false));
		consoleMenuItem.setState(userPrefs.getBoolean("openConsole", true));
		consoleMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/utilities-terminal.png")));
		consoleMenuItem.setMnemonic('o');
		viewMenu.add(consoleMenuItem);
		}
	
		JCheckBoxMenuItem videoWindowMenuItem = new JCheckBoxMenuItem("Video Window");
		videoWindowMenuItem.addActionListener(viewListener);
		//videoWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false));
		videoWindowMenuItem.setState(userPrefs.getBoolean("openVideoWindow", false));
		videoWindowMenuItem.setMnemonic('o');
		videoWindowMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/video-x-generic.png")));
		viewMenu.add(videoWindowMenuItem);

		viewMenu.addSeparator();
		
		ButtonGroup zoomModeGroup = new ButtonGroup();

		JRadioButtonMenuItem zoomZoomMenuItem = new JRadioButtonMenuItem("Zoom");
		zoomZoomMenuItem.addActionListener(viewListener);
		zoomZoomMenuItem.setSelected(Guess.getZooming()==Guess.ZOOMING_ZOOM);
		zoomZoomMenuItem.setMnemonic('z');
		zoomZoomMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0, false));
		zoomModeGroup.add(zoomZoomMenuItem);
		viewMenu.add(zoomZoomMenuItem);
		
		JRadioButtonMenuItem zoomSpaceMenuItem = new JRadioButtonMenuItem("Space");
		zoomSpaceMenuItem.addActionListener(viewListener);
		zoomSpaceMenuItem.setSelected(Guess.getZooming()==Guess.ZOOMING_SPACE);
		zoomSpaceMenuItem.setMnemonic('p');
		zoomSpaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false));
		zoomModeGroup.add(zoomSpaceMenuItem);
		viewMenu.add(zoomSpaceMenuItem);
		viewMenu.addSeparator();
		
		if (VisFactory.getUIMode() == VisFactory.PICCOLO) {
			JMenuItem overViewMenuItem = new JMenuItem("Overview");
			overViewMenuItem.addActionListener(viewListener);
			overViewMenuItem.setMnemonic('w');
			overViewMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/previewscroller.png")));
			viewMenu.add(overViewMenuItem);
		}
		
		JCheckBoxMenuItem fullscreenMenuItem = new JCheckBoxMenuItem("Fullscreen");
		fullscreenMenuItem.addActionListener(viewListener);
		fullscreenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0, false));
		fullscreenMenuItem.setMnemonic('f');
		fullscreenMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/view-fullscreen.png")));
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
						StatusBar.setErrorStatus("This method is only supported in piccolo mode right now");
					}
				} else if (event.getActionCommand().equals("Export Graph...")) {
					saveGDF();
				} else if (event.getActionCommand().equals("Save Logfile...")) {
					logToggle();
				}
			}
		};

		ActionListener importListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				loadFromFile();
			}
		};

		
		JMenuItem itemImportGraph = new JMenuItem("Import Graph...");
		itemImportGraph.setMnemonic('m');
		itemImportGraph.addActionListener(importListener);
		fileMenu.add(itemImportGraph);
		if (!displayProtected()) {
			itemImportGraph.setEnabled(false);
		}
		

		
		JMenuItem itemExportGraph = new JMenuItem("Export Graph...", 'r');
		itemExportGraph.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		itemExportGraph.addActionListener(printListener);

		fileMenu.add(itemExportGraph);
		if (!displayProtected()) {
			itemExportGraph.setEnabled(false);
		}
		fileMenu.addSeparator();
			
		

		JMenuItem itemExportImage = new JMenuItem("Export Image...", 'i');
		itemExportImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		itemExportImage.addActionListener(printListener);
		itemExportImage.setIcon(new ImageIcon(getClass().getResource("/images/image-x-generic.png")));		
		fileMenu.add(itemExportImage);
		if (!displayProtected()) {
			itemExportImage.setEnabled(false);
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
		exitMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/system-log-out.png")));
		exitMenuItem.addActionListener(printListener);
		fileMenu.add(exitMenuItem);
	}
	
	private void buildLayoutMenu() {
		layoutMenu.putClientProperty(Options.NO_ICONS_KEY, Boolean.TRUE);
		
		ActionListener layoutListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String command = event.getActionCommand();
				try {
					if (command.equals("GEM")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().gemLayout();
							}
						};
						GActionManager.runAction(layoutAction, "GEM Layout");
						StatusBar.setStatus("GEM Layout");
						
					} else if (command.equals("Radial...")) {
						String centerN = getInputFromUser(
								"Please enter a node to " + "use a the center",
								"User input", "");
						if (centerN != null) {
							final Node x = Guess.getGraph().getNodeByName(centerN);
							if (x != null) {
								
								GStateAction layoutAction = new GStateAction() {
									public void actionContent() {
										Guess.getGraph().radialLayout(x);
									}
								};
								GActionManager.runAction(layoutAction, "Radial Layout");
								StatusBar.setStatus("radialLayout(" + centerN
										+ ")");
							} else {
								StatusBar.setErrorStatus("Can't find node named "
												+ centerN);
							}
						}
					} else if (command.equals("Neighbour...")) {
						String startN = getInputFromUser(
								"Please enter a node to set the start",
								"User input", "");
						if (startN != null) {
							Node x = Guess.getGraph().getNodeByName(startN);
							if (x != null) {
								Guess.getGraph().neighbourLayout(x, 1);
								StatusBar.setStatus("neighbourLayout(" + startN
										+ ")");
							} else {
								StatusBar
										.setErrorStatus("Can't find node named "
												+ startN);
							}
						}
					} else if (command.equals("Circular")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().circleLayout();
							}
						};
						GActionManager.runAction(layoutAction, "Circle Layout");
						StatusBar.setStatus("Circle Layout");
					} else if (command.equals("Physics")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().physicsLayout();
							}
						};
						GActionManager.runAction(layoutAction, "Physics Layout");
						StatusBar.setStatus("Physics Layout");
					} else if (command.equals("Kamada-Kawai")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().jkkLayout1();
							}
						};
						GActionManager.runAction(layoutAction, "Kamada-Kawai (jkkLayout1)");
						StatusBar.setStatus("Kamada-Kawai (jkkLayout1)");
					} else if (command.equals("Fruchterman-Rheingold")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().frLayout();
							}
						};
						GActionManager.runAction(layoutAction, "Fruchterman-Rheingold");
						StatusBar.setStatus("Fruchterman-Rheingold");
					} else if (command.equals("Spring")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().springLayout();
							}
						};
						GActionManager.runAction(layoutAction, "Spring Layout");
						StatusBar.setStatus("Spring Layout");
					} else if (command.equals("MDS")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().mdsLayout();
							}
						};
						GActionManager.runAction(layoutAction, "MDS Layout");
						StatusBar.setStatus("MDS Layout");
					} else if (command.equals("Random")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().randomLayout();
							}
						};
						GActionManager.runAction(layoutAction, "Random Layout");
						StatusBar.setStatus("Random Layout");
					} else if (command.equals("Bin Pack")) {
						GStateAction layoutAction = new GStateAction() {
							public void actionContent() {
								Guess.getGraph().binPackLayout();
							}
						};
						GActionManager.runAction(layoutAction, "Bin Pack Layout");
						StatusBar.setStatus("Bin Pack Layout");
					}
				} catch (Exception e) {
					StatusBar.setErrorStatus(e.toString());
				}
			}
		};

		
		Arrays.sort(layoutItems);
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
		errorLogMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/dialog-error.png")));
		helpMenu.add(errorLogMenuItem);
		helpMenu.addSeparator();

		JMenuItem homepageMenuItem = new JMenuItem("GUESS Homepage");
		homepageMenuItem.addActionListener(helpListener);
		homepageMenuItem.setMnemonic('h');
		homepageMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/go-home.png")));
		helpMenu.add(homepageMenuItem);
		
		JMenuItem wikiMenuItem = new JMenuItem("GUESS Wiki");
		wikiMenuItem.addActionListener(helpListener);
		wikiMenuItem.setMnemonic('w');
		wikiMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/help-browser.png")));
		helpMenu.add(wikiMenuItem);
		
		helpMenu.addSeparator();

		JMenuItem aboutMenuItem = new JMenuItem("About GUESS");
		aboutMenuItem.addActionListener(helpListener);
		aboutMenuItem.setMnemonic('a');
		aboutMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/guess-icon.png")));
		helpMenu.add(aboutMenuItem);
	}
	
	
	private void buildToolMenu() {

		toolMenu.setIcon(new ImageIcon(getClass().getResource("/images/browse.gif")));
		toolMenu.setText("Browse");

		
		ActionListener toolListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				FrameListener fl = VisFactory.getFactory().getDisplay();
				GFrame gfl = null;
				if (fl instanceof GFrame) {
					gfl = ((GFrame) fl);
				}
				
				if (event.getActionCommand().equals("Browse")) {
					gfl.switchHandler(0);
					toolMenu.setIcon(new ImageIcon(getClass().getResource("/images/browse.gif")));
					toolMenu.setText("Browse");
				} else if (event.getActionCommand().equals("Manipulate Nodes")) {
					gfl.switchHandler(1);
					toolMenu.setIcon(new ImageIcon(getClass().getResource("/images/nodeed.gif")));
					toolMenu.setText("Nodes");
				} else if (event.getActionCommand().equals("Manipulate Edges")) {
					gfl.switchHandler(2);
					toolMenu.setIcon(new ImageIcon(getClass().getResource("/images/edgeed.gif")));
					toolMenu.setText("Edges");
				} else if (event.getActionCommand().equals("Manipulate Hulls")) {
					gfl.switchHandler(3);
					toolMenu.setIcon(new ImageIcon(getClass().getResource("/images/hulled.gif")));
					toolMenu.setText("Hulls");
				} else if (event.getActionCommand().equals("Draw")) {
					gfl.switchHandler(4);
					toolMenu.setIcon(new ImageIcon(getClass().getResource("/images/draw.gif")));
					toolMenu.setText("Draw");
				}
			}
		};

		ButtonGroup editModeGroup = new ButtonGroup();
		
		JRadioButtonMenuItem browseMenuItem = new JRadioButtonMenuItem("Browse");
		browseMenuItem.addActionListener(toolListener);
		browseMenuItem.setMnemonic('b');
		browseMenuItem.setSelected(true);
		browseMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/browse.gif")));
		toolMenu.add(browseMenuItem);
		editModeGroup.add(browseMenuItem);
		
		JRadioButtonMenuItem nodesMenuItem = new JRadioButtonMenuItem("Manipulate Nodes");
		nodesMenuItem.addActionListener(toolListener);
		nodesMenuItem.setMnemonic('m');
		nodesMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/nodeed.gif")));
		toolMenu.add(nodesMenuItem);
		editModeGroup.add(nodesMenuItem);
		
		JRadioButtonMenuItem edgesMenuItem = new JRadioButtonMenuItem("Manipulate Edges");
		edgesMenuItem.addActionListener(toolListener);
		edgesMenuItem.setMnemonic('a');
		edgesMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/edgeed.gif")));
		toolMenu.add(edgesMenuItem);
		editModeGroup.add(edgesMenuItem);
		
		JRadioButtonMenuItem hullsMenuItem = new JRadioButtonMenuItem("Manipulate Hulls");
		hullsMenuItem.addActionListener(toolListener);
		hullsMenuItem.setMnemonic('h');
		hullsMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/hulled.gif")));
		toolMenu.add(hullsMenuItem);
		editModeGroup.add(hullsMenuItem);
		
		JRadioButtonMenuItem drawMenuItem = new JRadioButtonMenuItem("Draw");
		drawMenuItem.addActionListener(toolListener);
		drawMenuItem.setMnemonic('d');
		drawMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/draw.gif")));
		toolMenu.add(drawMenuItem);
		editModeGroup.add(drawMenuItem);
	}
	
	
	private void buildEditMenu() {
		final JMenuItem undoMenuItem = new JMenuItem("Undo (Nothing)");
		undoMenuItem.setActionCommand("undo");
		undoMenuItem.setEnabled(false);
		undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask(), false));
		undoMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/edit-undo.png")));
		
		final JMenuItem redoMenuItem = new JMenuItem("Redo (Nothing)");
		redoMenuItem.setActionCommand("redo");
		redoMenuItem.setEnabled(false);
		redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask() + KeyEvent.SHIFT_DOWN_MASK, false));
		redoMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/edit-redo.png")));

		
		ActionListener undoRedoListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				GAction undoAction = GActionManager.getLastUndoAction();
				if ((undoAction!=null) && (undoAction.getDescription()!=null)) {
					undoMenuItem.setText("Undo (" + undoAction.getDescription() + ")");
					undoMenuItem.setEnabled(true);
				} else {
					undoMenuItem.setText("Undo (Nothing)");
					undoMenuItem.setEnabled(false);
				}
				
				GAction redoAction = GActionManager.getLastRedoAction();
				if ((redoAction!=null) && (redoAction.getDescription()!=null)) {
					redoMenuItem.setText("Redo (" + redoAction.getDescription() + ")");
					redoMenuItem.setEnabled(true);
				} else {
					redoMenuItem.setText("Redo (Nothing)");
					redoMenuItem.setEnabled(false);
				}				
			}
		};
		
		GActionManager.addActionListener(undoRedoListener);

		
		ActionListener editListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Background Color...")) {
					FrameListener fl = VisFactory.getFactory().getDisplay();
					Color c = JColorChooser.showDialog(null,
							"Please pick a color - GUESS", fl
									.getDisplayBackground());
					if (c != null) {
						fl.setDisplayBackground(c);
						// Save background color for next time
						userPrefs.putInt("backgroundcolor-red", c.getRed());
						userPrefs.putInt("backgroundcolor-green", c.getGreen());
						userPrefs.putInt("backgroundcolor-blue", c.getBlue());
						
						StatusBar.setStatus("v.setDisplayBackground(\""
								+ c.getRed() + "," + c.getGreen() + ","
								+ c.getBlue() + "\")");
					}
				} else if (event.getActionCommand().equals("Modify Field...")) {
					FieldModWindow.getFieldModWindow();
				} else if (event.getActionCommand().equals("Toggle Arrows")) {
					VisFactory.getFactory().setDirected(
							!VisFactory.getFactory().getDirected());
				} else if (event.getActionCommand().equals("undo")) {
					GActionManager.undo();
				} else if (event.getActionCommand().equals("redo")) {
					GActionManager.redo();
				}
			}
		};
		
		undoMenuItem.addActionListener(editListener);
		redoMenuItem.addActionListener(editListener);
		editMenu.add(undoMenuItem);
		editMenu.add(redoMenuItem);
		editMenu.addSeparator();

		
		JMenuItem modifyFieldMenuItem = new JMenuItem("Modify Field...");
		modifyFieldMenuItem.addActionListener(editListener);
		modifyFieldMenuItem.setMnemonic('m');
		editMenu.add(modifyFieldMenuItem);
		editMenu.addSeparator();
		
		JMenuItem toggleArrowsMenuItem = new JMenuItem("Toggle Arrows");
		toggleArrowsMenuItem.addActionListener(editListener);
		toggleArrowsMenuItem.setMnemonic('t');
		toggleArrowsMenuItem.setIcon(new ImageIcon(getClass().getResource("/images/mail-send-receive.png")));
		editMenu.add(toggleArrowsMenuItem);
		
		JMenuItem bgcolor = new JMenuItem("Background Color...");
		bgcolor.setMnemonic('b');
		bgcolor.addActionListener(editListener);
		bgcolor.setIcon(new ImageIcon(getClass().getResource("/images/preferences-desktop-wallpaper.png")));
		editMenu.add(bgcolor);
		editMenu.addSeparator();
	}
	
	public GMenuBar() {
		
		setBorder(BorderFactory.createEmptyBorder());
		
		
		fileMenu.setMnemonic('f');
		editMenu.setMnemonic('e');
		layoutMenu.setMnemonic('l');
		scriptMenu.setMnemonic('s');
		viewMenu.setMnemonic('v');
		helpMenu.setMnemonic('h');
		toolMenu.setMnemonic('t');	
		
		Insets menuInsets = new Insets(0,10,0,10);
		fileMenu.setMargin(menuInsets);
		editMenu.setMargin(menuInsets);
		layoutMenu.setMargin(menuInsets);
		scriptMenu.setMargin(menuInsets);
		viewMenu.setMargin(menuInsets);
		helpMenu.setMargin(menuInsets);
		toolMenu.setMargin(menuInsets);

		fileMenu.setUI(new BasicMenuUI());
		editMenu.setUI(new BasicMenuUI());
		layoutMenu.setUI(new BasicMenuUI());
		scriptMenu.setUI(new BasicMenuUI());
		viewMenu.setUI(new BasicMenuUI());
		helpMenu.setUI(new BasicMenuUI());
		toolMenu.setUI(new BasicMenuUI());
		
		buildFileMenu();
		buildEditMenu();
		buildLayoutMenu();
		buildScriptMenu();
		buildViewMenu();
		buildHelpMenu();
		
		buildToolMenu();
		
		buildStateSection();
		

		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0;

		c.gridy = 0;

		c.anchor = GridBagConstraints.NORTH;


		c.gridx = 0;
		add(fileMenu, c);
			
		c.gridx = 1;
		add(editMenu, c);
		
		c.gridx = 2;
		add(layoutMenu, c);
		
		c.gridx = 3;
		add(scriptMenu, c);
		
		c.gridx = 4;
		add(viewMenu, c);
		
		c.gridx = 5;
		add(helpMenu, c);
		
		c.gridx = 6;
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.VERTICAL;
		add(toolMenu, c);
		
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(4,8,4,4);
		c.weightx = 0;
		c.gridx = 7;
		add(statePanel, c);
		
	}
	
	private void buildStateSection() {
	
		
		// Create Border
		Border b1 = BorderFactory.createLineBorder(new Color(236, 243, 248), 1);
		
		Border b2a = BorderFactory.createMatteBorder(1, 1, 0, 0, new Color(107,110,113));
		Border b2b = BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(189,193,196));
		Border b2 = BorderFactory.createCompoundBorder(b2b, b2a);
		
		Border b3 = BorderFactory.createLineBorder(new Color(240,240,250), 1);
		
		Border b1b2 = BorderFactory.createCompoundBorder(b2, b1);
		Border b1b2b3 = BorderFactory.createCompoundBorder(b3, b1b2);
		statePanel.setBorder(b1b2b3);
		statePanel.setBackground(new Color(247, 251, 255));
		
		
		// Create Layout
		statePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.weighty = 0;
		c.weightx = 0;
		c.gridy = 0;
		
		final JButton btnPreviousState = new JButton();
		final JButton btnNextState = new JButton();
		final JTextField textState = new JTextField();
		final JButton btnAddState = new JButton();
		final JButton btnSaveState = new JButton();
		final JButton btnLoadState = new JButton();
		final JButton btnShowStates = new JButton();
		
		
		// Load previous
		btnPreviousState.setIcon(new ImageIcon(getClass().getResource("/images/btn-back.png")));
		btnPreviousState.setRolloverIcon(new ImageIcon(getClass().getResource("/images/btn-back-hover.png")));
		btnPreviousState.setDisabledIcon(new ImageIcon(getClass().getResource("/images/btn-back-disabled.png")));
		btnPreviousState.setBorder(BorderFactory.createEmptyBorder(0,1,0,1));
		btnPreviousState.setOpaque(false);
		btnPreviousState.setContentAreaFilled(false);
		btnPreviousState.setFocusPainted(false);
		btnPreviousState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stateSelector.loadPrevious();
			}
		});
		c.gridx = 0;
		statePanel.add(btnPreviousState, c);
		
		// Load next
		btnNextState.setIcon(new ImageIcon(getClass().getResource("/images/btn-next.png")));
		btnNextState.setRolloverIcon(new ImageIcon(getClass().getResource("/images/btn-next-hover.png")));
		btnNextState.setDisabledIcon(new ImageIcon(getClass().getResource("/images/btn-next-disabled.png")));
		btnNextState.setBorder(BorderFactory.createEmptyBorder(0,1,0,1));
		btnNextState.setOpaque(false);
		btnNextState.setContentAreaFilled(false);
		btnNextState.setFocusPainted(false);
		btnNextState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stateSelector.loadNext();
			}
		});
		c.gridx = 1;
		statePanel.add(btnNextState, c);
		
		
		// Name of current or new state 
		textState.setOpaque(false);
		textState.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 1));
		textState.setForeground(new Color(107,110,113));
		textState.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				textState.selectAll();
			}
			public void focusLost(FocusEvent e) {
				textState.select(0, 0);
			}
		});
		textState.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				
				if ((e.isControlDown()) && 
						(e.getKeyCode() == KeyEvent.VK_ENTER)) {
					if (btnLoadState.isVisible()) {
						btnLoadState.doClick();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (btnAddState.isVisible()) {
						btnAddState.doClick();
					}
					if (btnSaveState.isVisible()) {
						btnSaveState.doClick();
					}
				}
			}
		});
		textState.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setStateButtonState(btnPreviousState, btnNextState, btnAddState,  btnSaveState, btnLoadState, btnShowStates, textState);
			}
			public void insertUpdate(DocumentEvent e) {
				setStateButtonState(btnPreviousState, btnNextState, btnAddState,  btnSaveState, btnLoadState, btnShowStates, textState);
			}
			public void removeUpdate(DocumentEvent e) {
				setStateButtonState(btnPreviousState, btnNextState, btnAddState,  btnSaveState, btnLoadState, btnShowStates, textState);
			}
		});
		stateSelector.addStateSelectorEventListener(new StateSelectorEventListener() {
			public void stateLoaded(String state) {
				textState.setText(state);
				setStateButtonState(btnPreviousState, btnNextState, btnAddState,  btnSaveState, btnLoadState, btnShowStates, textState);
			}
			public void stateSaved(String state) {
				textState.setText(state);
				setStateButtonState(btnPreviousState, btnNextState, btnAddState,  btnSaveState, btnLoadState, btnShowStates, textState);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.weightx = 1;
		statePanel.add(textState, c);
		
		
		// Save states
		btnSaveState.setIcon(new ImageIcon(getClass().getResource("/images/btn-state-save.png")));
		btnSaveState.setRolloverIcon(new ImageIcon(getClass().getResource("/images/btn-state-save-hover.png")));
		btnSaveState.setBorder(BorderFactory.createEmptyBorder(0,1,0,1));
		btnSaveState.setOpaque(false);
		btnSaveState.setContentAreaFilled(false);
		btnSaveState.setFocusPainted(false);
		btnSaveState.setToolTipText("Save state (Enter)");
		btnSaveState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StorageFactory.getSL().saveState(textState.getText());
			}
		});
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.weightx = 0;
		statePanel.add(btnSaveState, c);
		
		
		// Load states
		btnLoadState.setIcon(new ImageIcon(getClass().getResource("/images/btn-state-load.png")));
		btnLoadState.setRolloverIcon(new ImageIcon(getClass().getResource("/images/btn-state-load-hover.png")));
		btnLoadState.setBorder(BorderFactory.createEmptyBorder(0,1,0,1));
		btnLoadState.setOpaque(false);
		btnLoadState.setContentAreaFilled(false);
		btnLoadState.setFocusPainted(false);
		btnLoadState.setToolTipText("Load state (Ctrl+Enter)");
		btnLoadState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StorageFactory.getSL().loadState(textState.getText());
			}
		});
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.weightx = 0;
		statePanel.add(btnLoadState, c);
		
		// Add state
		btnAddState.setIcon(new ImageIcon(getClass().getResource("/images/btn-add.png")));
		btnAddState.setRolloverIcon(new ImageIcon(getClass().getResource("/images/btn-add-hover.png")));
		btnAddState.setDisabledIcon(new ImageIcon(getClass().getResource("/images/btn-add-disabled.png")));
		btnAddState.setBorder(BorderFactory.createEmptyBorder(0,1,0,1));
		btnAddState.setOpaque(false);
		btnAddState.setContentAreaFilled(false);
		btnAddState.setFocusPainted(false);
		btnAddState.setToolTipText("Add state (Enter)");
		btnAddState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StorageFactory.getSL().saveState(textState.getText());
			}
		});
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.weightx = 0;
		statePanel.add(btnAddState, c);
		
		// Show states
		btnShowStates.setIcon(new ImageIcon(getClass().getResource("/images/btn-more.png")));
		btnShowStates.setRolloverIcon(new ImageIcon(getClass().getResource("/images/btn-more-hover.png")));
		btnShowStates.setDisabledIcon(new ImageIcon(getClass().getResource("/images/btn-more-disabled.png")));
		btnShowStates.setBorder(BorderFactory.createEmptyBorder(0,1,0,1));
		btnShowStates.setOpaque(false);
		btnShowStates.setContentAreaFilled(false);
		btnShowStates.setFocusPainted(false);
		btnShowStates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stateSelector.show(
						statePanel.getRootPane(), 
						statePanel.getX() + statePanel.getWidth() - stateSelector.getPreferredSize().width - 2, 
						statePanel.getY() + statePanel.getHeight() - 3
						);
			}
		});
		c.gridx = 5;
		statePanel.add(btnShowStates, c);

		setStateButtonState(btnPreviousState, btnNextState, btnAddState, btnSaveState, btnLoadState, btnShowStates, textState);
		
		statePanel.setPreferredSize(new Dimension(200,24));
		statePanel.setMinimumSize(new Dimension(200,24));
	}

	private void setStateButtonState(JButton btnPrevious, JButton btnNext,
			JButton btnAdd, JButton btnSave, JButton btnLoad, JButton btnMore, 
			JTextField curState) {
		
		btnPrevious.setEnabled(stateSelector.hasPrevious());
		btnNext.setEnabled(stateSelector.hasNext());
		btnMore.setEnabled(stateSelector.isNotEmpty());
		btnAdd.setEnabled(((curState.getText().length()>0) && 
				(curState.getText().charAt(0)!=GStateAction.delimiter.charAt(0))));
		
		btnAdd.setVisible(!stateSelector.isState(curState.getText()));
		btnSave.setVisible(stateSelector.isState(curState.getText()));
		btnLoad.setVisible(stateSelector.isState(curState.getText()));

	}
	
/*	public JMenu add(JMenu c) {
		JMenu temp = getMenu(getMenuCount() - 1);
		if (temp == helpMenu) {
			remove(getMenuCount() - 1);
		}
		super.add(c);
		super.add(helpMenu);
		return (c);
	}*/

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
					userPrefs.put("scriptMRU10", userPrefs.get("scriptMRU9", ""));
					userPrefs.put("scriptMRU9", userPrefs.get("scriptMRU8", ""));
					userPrefs.put("scriptMRU8", userPrefs.get("scriptMRU7", ""));
					userPrefs.put("scriptMRU7", userPrefs.get("scriptMRU6", ""));
					userPrefs.put("scriptMRU6", userPrefs.get("scriptMRU5", ""));
					userPrefs.put("scriptMRU5", userPrefs.get("scriptMRU4", ""));
					userPrefs.put("scriptMRU4", userPrefs.get("scriptMRU3", ""));
					userPrefs.put("scriptMRU3", userPrefs.get("scriptMRU2", ""));
					userPrefs.put("scriptMRU2", userPrefs.get("scriptMRU1", ""));

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

	public void loadFromFile() {
		SunFileFilter filter = new SunFileFilter();
		try {
			if (prevLoad == null) {
				prevLoad = new File(".");
			}
			JFileChooser chooser = new JFileChooser(prevLoad.getCanonicalPath());
				
			filter.addExtension("gdf");
			filter.addExtension("xml");
			filter.addExtension("gml");
			filter.addExtension("graphml");
			filter.addExtension("net");

			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName = chooser.getSelectedFile().getAbsolutePath();
				String ext = (fileName.lastIndexOf(".")==-1)?"":fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
				ext = ext.toLowerCase();
				
				if (ext.equals("gdf")) {
					Guess.getGraph().makeFromGDF(fileName);
				} else if (ext.equals("xml") || ext.equals("gml") || ext.equals("graphml")) {
					Guess.getGraph().makeFromGML(fileName);
				} else if (ext.equals("net")) {
					Guess.getGraph().makeFromPajek(fileName);
				} else {
					System.err.println("Unknown File Extension.");
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
