package com.hp.hpl.guess.ui;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.prefs.Preferences;
import java.awt.GridBagConstraints;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import java.awt.*;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.piccolo.GFrame;

public class MainUIWindow extends JFrame {

    public static final int HORIZONTAL_DOCK = 1;
    public static final int VERTICAL_DOCK = 2;

    private Component canvas;
    private GraphicsDevice graphicsDevice;
    private DisplayMode originalDisplayMode;
    private EventListener escapeFullScreenModeListener;

    private JTabbedPane tabbedPaneH = null;
    private JTabbedPane tabbedPaneV = null;

    private JSplitPane splitPaneH = null;
    private JSplitPane splitPaneV = null;

    private GMenuBar myMenu = null;

    private final JPopupMenu jpop = new JPopupMenu("Dock Menu");

    private JTabbedPane selected = null;
    
    /**
	* Object to save user preferences
	*/
	private Preferences userPrefs = Preferences.userNodeForPackage(getClass());

    public JTabbedPane getHorizontalTabbedPane() {
	return(tabbedPaneH);
    }

    public JTabbedPane getVerticalTabbedPane() {
	return(tabbedPaneV);
    }

    public JSplitPane getHorizontalSplitPane() {
	return(splitPaneH);
    }

    public JSplitPane getVerticalSplitPane() {
	return(splitPaneV);
    }

    public Set getVerticalDockedComponents() {
	HashSet s = new HashSet();
	for (int i = 0 ; i < tabbedPaneV.getTabCount() ; i++) {
	    s.add(tabbedPaneV.getComponentAt(i));
	}
	return(s);
    }

    public Set getHorizontalDockedComponents() {
	HashSet s = new HashSet();
	for (int i = 0 ; i < tabbedPaneH.getTabCount() ; i++) {
	    s.add(tabbedPaneH.getComponentAt(i));
	}
	return(s);
    }

    public void enableButtons(boolean state) {
	StatusBar.enableButtons(state);
    }

    public MainUIWindow(Component aCanvas) {
	this(false, aCanvas);
    }
    
    public GMenuBar getGMenuBar() {
	return(myMenu);
    }

    public MainUIWindow(boolean fullScreenMode, Component aCanvas) {
	this(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(), 
	     fullScreenMode, aCanvas);
    }

    public MainUIWindow(GraphicsDevice aDevice, final boolean fullScreenMode, final Component aCanvas) {
	super(aDevice.getDefaultConfiguration());
	getContentPane().setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
		
	graphicsDevice = aDevice;
	
	// Set Window Icon
	ImageIcon imageIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("images/guess-icon.png"))); 
    setIconImage(imageIcon.getImage());
	
	try {
	    originalDisplayMode = graphicsDevice.getDisplayMode();
	} catch (InternalError e) {
	    //ExceptionWindow.getExceptionWindow(e);
	}
	
	setBounds(getDefaultFrameBounds());
	setBackground(null);
	
	try {
	    addWindowListener
		(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {			
				// Save position of dividers
				userPrefs.putInt("HDividerLocation", getHDividerLocation());
				userPrefs.putInt("VDividerLocation", getVDividerLocation());
				
				// Save window size
				userPrefs.putInt("MainWindowWidth", getWidth());
				userPrefs.putInt("MainWindowHeight", getHeight());

			    com.hp.hpl.guess.Guess.shutdown();
			}

		    }
		    );
	    //setDefaultCloseOperation(EXIT_ON_CLOSE);
	} catch (SecurityException e) {} // expected from applets
	
	canvas = aCanvas;
    	
	if (canvas == null) {
	    System.err.println("null canvas");
	}
	
	
	tabbedPaneH = new JTabbedPane();
	//tabbedPaneH.setUI(new PSTabbedPaneUI());
	tabbedPaneH.setTabPlacement(JTabbedPane.BOTTOM);
	tabbedPaneV = createTabbedPane(JTabbedPane.LEFT);
	//new JTabbedPane();
	//tabbedPaneV.setUI(new PSTabbedPaneUI());

	splitPaneH = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					      canvas,
					      tabbedPaneH);

	splitPaneV = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				    tabbedPaneV,
				    splitPaneH);

	c.fill = GridBagConstraints.BOTH;
	c.weighty = 1;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = 0;
	getContentPane().add(splitPaneV,c);

	StatusBar jp = new StatusBar();

	c.fill = GridBagConstraints.HORIZONTAL;
	c.weighty = 0;
	c.gridy = 1;
	getContentPane().add(jp,c);

	myMenu = new GMenuBar();
	setJMenuBar(myMenu);
	validate(); 	
	setFullScreenMode(fullScreenMode);
	canvas.requestFocus();
	setTitle("Visualization - GUESS");

	JMenuItem jmi1 = jpop.add("Undock");
	JMenuItem jmi2 = jpop.add("Close");
	ActionListener al = new ActionListener(  ) {
		public void actionPerformed(ActionEvent event) {
		    if (event.getActionCommand().equals("Undock")) {
			if (selected == tabbedPaneH) {
			    Component c = tabbedPaneH.getSelectedComponent();
			    if ((c != null) && (c instanceof Dockable)) {
				undock((Dockable)c);
			    }
			} else {
			    Component c = tabbedPaneV.getSelectedComponent();
			    if ((c != null) && (c instanceof Dockable)) {
				undock((Dockable)c);
			    }
			}
		    } else if (event.getActionCommand().equals("Close")) {
			if (selected == tabbedPaneH) {
			    Component c = tabbedPaneH.getSelectedComponent();
			    if ((c != null) && (c instanceof Dockable)) {
				selected.remove((Component)c);
				((Dockable)c).opening(false);
			    }
			} else {
			    Component c = tabbedPaneV.getSelectedComponent();
			    if ((c != null) && (c instanceof Dockable)) {
				selected.remove((Component)c);
				((Dockable)c).opening(false);
			    }
			}
			if (selected.getTabCount() == 0) {
			    if (selected == tabbedPaneV) {
			    	hideDividers(splitPaneV);
			    } else {
			    	hideDividers(splitPaneH);
			    }
			}
		    }
		}
	    };
	
	jmi1.addActionListener(al);
	jmi2.addActionListener(al);

	MouseAdapter ma = new MouseAdapter(){
		public void mousePressed(MouseEvent e) {
		    if ((e.getButton() == MouseEvent.BUTTON2) ||
			(e.getButton() == MouseEvent.BUTTON3)) {
			//System.out.println(tabbedPane.getSelectedIndex());
			//System.out.println("popup");
			selected = (JTabbedPane)e.getComponent();
			jpop.show(e.getComponent(),
				  e.getX(),
				  e.getY());
		    }
		}
	    };
	tabbedPaneH.addMouseListener(ma);
	tabbedPaneV.addMouseListener(ma);
    }

    /**
     * set the location of the horizontal panel
     * @param loc the integer location, distance from top
     */
    public void setHDividerLocation(int loc) {
	splitPaneH.setDividerLocation(loc);
	if (loc>=getHeight()) {
		hideDividers(splitPaneH);
	}
    }

    /**
     * set the location of the vertical panel
     * @param loc the integer location, distance from left
     */
    public void setVDividerLocation(int loc) {
	splitPaneV.setDividerLocation(loc);
	if (loc==0) {
		hideDividers(splitPaneV);
	}	
    }

    /**
     * get the location of the horizontal panel
     * @return loc the integer location, distance from top
     */
    public int getHDividerLocation() {
		BasicSplitPaneUI bspui = (BasicSplitPaneUI)splitPaneH.getUI();
    	if (bspui.getDivider().isVisible()) {
    		return splitPaneH.getDividerLocation();
    	} else {
    		return splitPaneH.getMaximumDividerLocation();
    	}
    }

    /**
     * get the location of the vertical panel
     * @param loc the integer location, distance from left
     */
    public int getVDividerLocation() {
		BasicSplitPaneUI bspui = (BasicSplitPaneUI)splitPaneV.getUI();
    	if (bspui.getDivider().isVisible()) {
    		return splitPaneV.getDividerLocation();
    	} else {
    		return 0;
    	}
    }

    public static JTabbedPane createTabbedPane(int tabPlacement){ 
	switch(tabPlacement){ 
        case JTabbedPane.LEFT: 
        case JTabbedPane.RIGHT: 
            Object textIconGap = UIManager.get("TabbedPane.textIconGap"); 
            Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets"); 
            UIManager.put("TabbedPane.textIconGap", new Integer(5)); 
            UIManager.put("TabbedPane.tabInsets", new Insets(tabInsets.left, tabInsets.top, tabInsets.right, tabInsets.bottom)); 
            JTabbedPane tabPane = new JTabbedPane(tabPlacement); 
            UIManager.put("TabbedPane.textIconGap", textIconGap); 
            UIManager.put("TabbedPane.tabInsets", tabInsets); 
            return tabPane; 
        default: 
            return new JTabbedPane(tabPlacement); 
	} 
    }  
    

    public void dock(Dockable d) {
	try {
	    if (d.getDirectionPreference() == VERTICAL_DOCK) {
	    	double m1 = getVDividerLocation();
			double m2 = ((Component)d).getPreferredSize().getWidth();
			int m3 = (int)Math.max(m1,m2);
			
	    	showDividers(splitPaneV, m3);
		tabbedPaneV.addTab(null, 
				   new VerticalTextIcon(d.getTitle(),
							false),
				   (Component)d);
		tabbedPaneV.setBackgroundAt(0,Color.darkGray);
		//tabbedPaneV.addTab(d.getTitle(),(Component)d);
		tabbedPaneV.setSelectedComponent((Component)d);
	    } else {
		
		double m1 = splitPaneH.getDividerLocation();
		double m2 = ((Component)d).getPreferredSize().getHeight();
		int m3 = (int)Math.max(m1,m2);

		
		showDividers(splitPaneH, m3);
		tabbedPaneH.addTab(d.getTitle(),(Component)d);
		tabbedPaneH.setSelectedComponent((Component)d);
	    }
	    d.opening(true);
	    d.attaching(true);
	    splitPaneH.setOneTouchExpandable(true);
	    splitPaneV.setOneTouchExpandable(true);
	} catch (Exception e) {
	    ExceptionWindow.getExceptionWindow(e);
	}
    }

    public void undock(Dockable d) {
	GuessJFrame gjf = d.getWindow();
	if (gjf == null) {
	    gjf = new GuessJFrame(d);
	} else {
	    gjf.getContentPane().removeAll();
	    gjf.getContentPane().add((Component)d);
	    //gjf.setSize(((Component)d).getPreferredSize());
	}

	//System.out.println(((Component)d).getPreferredSize());
	d.opening(true);
	d.attaching(false);
	gjf.setBounds(20,20,
		      (int)((Component)d).getPreferredSize().getWidth(),
		      (int)((Component)d).getPreferredSize().getHeight()+50);
	gjf.setVisible(true);

	if (selected == null) {
	    if (d.getDirectionPreference() == HORIZONTAL_DOCK) {
		selected = tabbedPaneH;
	    } else {
		selected = tabbedPaneV;
	    }
	}

	if (selected != null) {
	    selected.remove((Component)d);

	    if (selected.getTabCount() == 0) {
			if (selected == tabbedPaneV) {
			    hideDividers(splitPaneV);
			} else {
				hideDividers(splitPaneH);
			}
	    }
	}
    }

    /**
     * Hides the Dividers from the JSplitpane
     * @param splitpane
     */
    private void hideDividers(JSplitPane splitpane) {
		BasicSplitPaneUI bspui = (BasicSplitPaneUI)splitpane.getUI();
		bspui.getDivider().setVisible(false);
		splitpane.setEnabled(false);
		
		
		if (splitpane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			tabbedPaneV.setVisible(false);
			splitpane.setDividerLocation(0);
			splitpane.setLastDividerLocation(0);
			userPrefs.putInt("VDividerLocation", 0);
		} else {
			tabbedPaneH.setVisible(false);
			splitpane.setDividerLocation(splitPaneH.getMaximumDividerLocation());
			splitpane.setLastDividerLocation(splitPaneH.getMaximumDividerLocation());
			userPrefs.putInt("HDividerLocation", splitPaneH.getMaximumDividerLocation());
		}
		
    }
    
    /**
     * Shows the Dividers from the JSplitpane
     * @param splitpane
     */
    private void showDividers(JSplitPane splitpane, int dividerLoction) {
		BasicSplitPaneUI bspui = (BasicSplitPaneUI)splitpane.getUI();
		bspui.getDivider().setVisible(true);
		splitpane.setEnabled(true);
		if (splitpane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			tabbedPaneV.setVisible(true);
			setVDividerLocation(dividerLoction);
		} else {
			tabbedPaneH.setVisible(true);
			setHDividerLocation(dividerLoction);
		}
    }    
    
    public void close(Dockable d) {
	try {
	    JTabbedPane selected = null;
	    if (d.getDirectionPreference() == VERTICAL_DOCK) {
		selected = tabbedPaneV;
	    } else {
		selected = tabbedPaneH;
	    }
	    d.opening(false);
	    selected.remove((Component)d);

	    if (selected.getTabCount() == 0) {
			if (selected == tabbedPaneV) {
			    hideDividers(splitPaneV);
			} else {
				hideDividers(splitPaneH);
			}
	    }
	} catch (Exception e) {
	    ExceptionWindow.getExceptionWindow(e);
	}
    }


    public Component getCanvas() {
	return canvas;
    }
    
    public Rectangle getDefaultFrameBounds() {
	return new Rectangle(100, 100, 800, 600);
	//return new Rectangle(100, 100, 1024,768);
    }		
    
    //****************************************************************
    // Full Screen Display Mode
    //****************************************************************
    
    public void setFullScreenMode(boolean fullScreenMode) {
	if (fullScreenMode) {
	    addEscapeFullScreenModeListener();
	    
	    if (isDisplayable()) {
		dispose();
	    }
	    
	    setUndecorated(true);
	    setResizable(false);
	    graphicsDevice.setFullScreenWindow(this);			 
	    
	    if (graphicsDevice.isDisplayChangeSupported()) {
		chooseBestDisplayMode(graphicsDevice);
	    }		 
	    validate();
	} else {
	    removeEscapeFullScreenModeListener();
	    
	    if (isDisplayable()) {
		dispose();
	    }
	    
	    setUndecorated(false);
	    setResizable(true);
	    graphicsDevice.setFullScreenWindow(null);	
	    
	    // Restore size
		setSize(userPrefs.getInt("MainWindowWidth", getWidth()), 
				userPrefs.getInt("MainWindowHeight", getHeight()));
		
	    validate();
	    setVisible(true);
	}
	
	// Restore position of dividers
	setHDividerLocation(userPrefs.getInt("HDividerLocation", 2*getHeight() / 4));
	setVDividerLocation(userPrefs.getInt("VDividerLocation", 0));
	
    }
    
    protected void chooseBestDisplayMode(GraphicsDevice device) {
	DisplayMode best = getBestDisplayMode(device);
	if (best != null) {
	    device.setDisplayMode(best);
	}
    }
    
    protected DisplayMode getBestDisplayMode(GraphicsDevice device) {
	try {
	    Iterator itr = getPreferredDisplayModes(device).iterator();
	    while (itr.hasNext()) {
		DisplayMode each = (DisplayMode) itr.next();
		DisplayMode[] modes = device.getDisplayModes();
		for (int i = 0; i < modes.length; i++) {
		    if (modes[i].getWidth() == each.getWidth() && 
			modes[i].getHeight() == each.getHeight() && 
			modes[i].getBitDepth() == each.getBitDepth()) {
			return each;
		    }
		}			
	    }
	} catch (Exception e) {
	}
	
	return null;
    }
    
    protected Collection getPreferredDisplayModes(GraphicsDevice device) {
	ArrayList result = new ArrayList();
	
	result.add(device.getDisplayMode());
	return result;
    };	 

    public void addEscapeFullScreenModeListener() {
	removeEscapeFullScreenModeListener();
	escapeFullScreenModeListener = new KeyAdapter() {
		public void keyPressed(KeyEvent aEvent) {
		    if (aEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			setFullScreenMode(false);
		    }
		}
	    };	
	canvas.addKeyListener((KeyListener)escapeFullScreenModeListener);
    }
    
    /**
     * This method removes the escape full screen mode key listener. It will be
     * called for you automatically when full screen mode exits, but the method
     * has been made public for applications that wish to use other methods for
     * exiting full screen mode.
     */
    public void removeEscapeFullScreenModeListener() {
	if (escapeFullScreenModeListener != null) {
	    canvas.removeKeyListener((KeyListener)escapeFullScreenModeListener);
	    escapeFullScreenModeListener = null;
	}
    }
}
