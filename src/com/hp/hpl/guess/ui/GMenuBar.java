package com.hp.hpl.guess.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.Version;
import com.hp.hpl.guess.Node;
import java.io.*;
import com.hp.hpl.guess.freehep.HEPDialog;
import com.hp.hpl.guess.piccolo.GFrame;

import com.jgoodies.looks.*;


public class GMenuBar extends JMenuBar {

    String[ ] fileItems = new String[ ] { "Export Image...",
					  "Export Screenshot...",
					  "Run Script..."};

    char[ ] fileShortcuts = { 'I','S','R'};
    
    String[] layoutItems = 
	new String[] {"Bin Pack","GEM","Circular",
		      "Physics","Kamada-Kawai",
		      "Fruchterman-Rheingold","Spring",
		      "MDS","Random","Radial"};

    JCheckBoxMenuItem logItem = new JCheckBoxMenuItem("Log...");

    HEPDialog hd = new HEPDialog(null);

    public String getInputFromUser(Object question, String title,
				   Object def) {
	// doesn't make use of title at present
	String toRet = JOptionPane.showInputDialog(question,def);
	return(toRet);
    }

    public GMenuBar() {
	this.putClientProperty(Options.HEADER_STYLE_KEY, Boolean.TRUE);
	
	JMenu fileMenu = new JMenu("File");
	JMenu editMenu = new JMenu("Edit");
	JMenu displayMenu = new JMenu("Display");
	JMenu layoutMenu = new JMenu("Layout");
	JMenu helpMenu = new JMenu("Help");

	ActionListener displayListener = new ActionListener(  ) {
		public void actionPerformed(ActionEvent event) {
		    if (event.getActionCommand().equals("Background Color")) {
			FrameListener fl = 
			    VisFactory.getFactory().getDisplay();
			Color c = 
			    JColorChooser.showDialog(null,
						     "Please pick a color",
						     fl.getDisplayBackground());
			if (c != null) {
			    fl.setDisplayBackground(c);
			    StatusBar.setStatus("v.setDisplayBackground(\""+
						c.getRed()+","+
						c.getGreen()+","+
						c.getBlue()+"\")");
			}
		    } else if (event.getActionCommand().equals("Information Window")) {
			InfoWindow.create();
		    } else if (event.getActionCommand().equals("Center")) {
			VisFactory.getFactory().getDisplay().center();
		    } else if (event.getActionCommand().equals("Toggle Arrows")) {
			VisFactory.getFactory().setDirected(!VisFactory.getFactory().getDirected());
		    }
		}
	    };


	JMenuItem bgcolor = new JMenuItem("Center");
	bgcolor.addActionListener(displayListener);
	displayMenu.add(bgcolor);
	bgcolor = new JMenuItem("Background Color");
	bgcolor.addActionListener(displayListener);
	displayMenu.add(bgcolor);
	bgcolor = new JMenuItem("Information Window");
	bgcolor.addActionListener(displayListener);
	displayMenu.add(bgcolor);
	bgcolor = new JMenuItem("Toggle Arrows");
	bgcolor.addActionListener(displayListener);
	displayMenu.add(bgcolor);

	ActionListener printListener = new ActionListener(  ) {
		public void actionPerformed(ActionEvent event) {
		    if (event.getActionCommand().equals("Exit")) {
			Guess.shutdown();
		    } else if (event.getActionCommand().equals("Export Image...")) {
			if (VisFactory.getUIMode() == VisFactory.PICCOLO) {
			    hd.showHEPDialog(null,"Export Image",
					     (GFrame)VisFactory.getFactory().getDisplay(),
					     "output.jpg",
					     (Component)VisFactory.getFactory().getDisplay());
			} else {
			    StatusBar.setErrorStatus("This method is only supported in piccolo mode right now");
			}
		    } else if (event.getActionCommand().equals("Export Screenshot...")) {
			hd.showHEPDialog(null,"Export Screenshot",
					 (Component)VisFactory.getFactory().getDisplay(),
					 "output.jpg");
		    } else if (event.getActionCommand().equals("Run Script...")) {
			runScript();
		    } else if (event.getActionCommand().equals("Run Script...")) {
			runScript();
		    } else if (event.getActionCommand().equals("Log...")) {
			logToggle();
		    }
		}
	    };
	for (int i=0; i < fileItems.length; i++) {
	    JMenuItem item = new JMenuItem(fileItems[i], fileShortcuts[i]);
	    item.setAccelerator(KeyStroke.getKeyStroke(fileShortcuts[i],
						       Toolkit.getDefaultToolkit(  ).getMenuShortcutKeyMask(  ), false));
	    
	    item.addActionListener(printListener);
	    fileMenu.add(item);
	}

	// added the log button sep. since we need to 
	// access it later
	logItem.addActionListener(printListener);
	fileMenu.add(logItem);

	// we want exit to always be last
	JMenuItem item2 = new JMenuItem("Exit");
	item2.addActionListener(printListener);
	fileMenu.add(item2);


	ActionListener layoutListener = new ActionListener(  ) {
		public void actionPerformed(ActionEvent event) {
		    String command = event.getActionCommand();
		    StatusBar.runProgressBar(true);
		    try {
			if (command.equals("GEM")) {
			    Guess.getGraph().gemLayout();
			} else if (command.equals("Radial")) {
			    String centerN = 
				getInputFromUser("Please enter a node to "+
						 "use a the center",
						 "User input","");
			    if (centerN != null) {
				Node x = 
				    Guess.getGraph().getNodeByName(centerN);
				if (x != null) {
				    Guess.getGraph().radialLayout(x);
				    StatusBar.setStatus("radialLayout("+
							centerN+")");
				} else {
				    StatusBar.setErrorStatus("Can't find node named " + centerN);
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

	for (int i=0; i < layoutItems.length; i++) {
	    JMenuItem item = new JMenuItem(layoutItems[i]);
	    item.addActionListener(layoutListener);
	    layoutMenu.add(item);
	}

	
	ActionListener helpListener = new ActionListener(  ) {
		public void actionPerformed(ActionEvent event) {
		    if (event.getActionCommand().equals("About GUESS")) {
			JOptionPane.showMessageDialog(null,
						      "Major version: " + Version.MAJOR_VERSION + "\nMinor version: " + Version.MINOR_VERSION);
		    } else if (event.getActionCommand().equals("Error Log")) {
			ExceptionWindow.getExceptionWindow(null).setVisible(true);
		    }
		}
	    };
	
	JMenuItem ver = new JMenuItem("Error Log");
	ver.addActionListener(helpListener);
	helpMenu.add(ver);

	ver = new JMenuItem("About GUESS");
	ver.addActionListener(helpListener);
	helpMenu.add(ver);

	ActionListener editListener = new ActionListener(  ) {
		public void actionPerformed(ActionEvent event) {
		    if (event.getActionCommand().equals("Modify Field...")) {
			FieldModWindow.getFieldModWindow();
		    }
		}
	    };
	ver = new JMenuItem("Modify Field...");
	ver.addActionListener(editListener);
	editMenu.add(ver);

	add(fileMenu);
	add(editMenu);
	add(displayMenu);
	add(layoutMenu);
	add(helpMenu);
    }

    File prevRun = null;
    File prevLog = null;
    SunFileFilter filter = new SunFileFilter();

    public void runScript() {
	try {
	    if (prevRun == null) {
		prevRun = new File(".");
	    }
	    JFileChooser chooser = 
		new JFileChooser(prevRun.getCanonicalPath());
	    filter.addExtension("py");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
		String fileName = 
		    chooser.getSelectedFile().getAbsolutePath();
		Guess.getInterpreter().execfile(fileName);
		prevRun = new File(fileName);
	    }
	} catch (IOException e) {
	    ExceptionWindow.getExceptionWindow(e);
	    JOptionPane.showMessageDialog(null,
					  "Error loading file " + e,
					  "Error",
					  JOptionPane.ERROR_MESSAGE);
	}
    }

    public void logToggle() {
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
	    JFileChooser chooser = 
		new JFileChooser(prevLog.getCanonicalPath());
	    filter.addExtension("py");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
		SunFileFilter filter = 
		    new SunFileFilter();
		File f = chooser.getSelectedFile();
		String fileExtension = filter.getExtension(f);
		String fileName = f.getAbsolutePath();
		if (fileExtension == null) {
		    fileName = fileName + ".py";
		    f = new File(fileName);
		}		
		if (f.exists()) {
		    int yn = 
			JOptionPane.showConfirmDialog(null, 
						      "File " + fileName + " exists, overwrite?","Exists",
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
	    JOptionPane.showMessageDialog(null,
					  "Error loading file " + e,
					  "Error",
					  JOptionPane.ERROR_MESSAGE);
	}
    }
}
