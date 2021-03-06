
package com.hp.hpl.guess;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.applet.AppletContext;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import org.python.core.PyException;
import org.python.core.PyFunction;
import org.python.core.PySyntaxError;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import com.hp.hpl.guess.action.GActionManager;
import com.hp.hpl.guess.action.GStateAction;
import com.hp.hpl.guess.animation.AnimationFactory;
import com.hp.hpl.guess.layout.Neighbour;
import com.hp.hpl.guess.layout.Radial;
import com.hp.hpl.guess.piccolo.GFrame;
import com.hp.hpl.guess.r.R;
import com.hp.hpl.guess.storage.StorageFactory;
import com.hp.hpl.guess.ui.Colors;
import com.hp.hpl.guess.ui.ExceptionWindow;
import com.hp.hpl.guess.ui.FrameListener;
import com.hp.hpl.guess.ui.GMenuBar;
import com.hp.hpl.guess.ui.GraphEvents;
import com.hp.hpl.guess.ui.InfoWindow;
import com.hp.hpl.guess.ui.MainUIWindow;
import com.hp.hpl.guess.ui.NodeEditorPopup;
import com.hp.hpl.guess.ui.ShapeDB;
import com.hp.hpl.guess.ui.StatusBar;
import com.hp.hpl.guess.ui.SunFileFilter;
import com.hp.hpl.guess.ui.TextPaneJythonConsole;
import com.hp.hpl.guess.ui.VideoWindow;
import com.hp.hpl.guess.ui.VisFactory;
import com.hp.hpl.guess.ui.welcomeDialog;
import com.hp.hpl.guess.util.intervals.Tracker;
import com.hp.hpl.guess.util.PrefWrapper;
import com.jgoodies.looks.Options;
import com.jidesoft.plaf.LookAndFeelFactory;


/**
 * the main guess system, it contains a Main that does most of what you
 * want, but you can basically build your own application using the stuff
 * in this class.  Generally, you will want to do:
 * <PRE>
 * StorageFactory.useDBServer(...) // to set up the database you want
 * Guess.configureUI()             // unless you want your own L&F
 * Guess.init(...)
 * </PRE>
 * @pyobj Guess
 * @pyimport from com.hp.hpl.guess import Guess
 */
public class Guess
{
    /**
     * the visualization frame
     */
    private static FrameListener myF = null;

    /**
     * the current working graph, there is only 1
     */
    private static Graph g = null;

    /**
     * a wrapper around the jython interp so that 
     * we can get rid of it when people don't need/want it
     */
    private static InterpreterAbstraction interpSingleton = null;

    /**
     * use the fake interpreter?
     */
    private static boolean fakeInterp = false;

    /**
     * shortcut
     */
    private static final char sep = File.separatorChar;

    /**
     * some times we need to execute a command for loading
     * the database at a later time, so we use this
     */
    private static String doLater = null;

    /**
     * we also can take a list of files to execute
     */
    private static Vector<String> pythonToExec = null;

    /**
     * are we running inside an applet
     */
    private static boolean appletMode = false;
    
    /**
     * FDN73: do we want to shutdown on exit?
     */
    private static boolean shutdownOnExit = false;

    /**
     * are we running inside a signed applet
     */
    private static boolean signedAppletMode = false;

    /**
     * the applet context
     */
    private static AppletContext myAC = null;

    /**
     * are we running in GPL free mode
     */
    private static boolean gplFree = false;

    /**
     * enable the main ui window or not
     */
    private static boolean enableMainUI = true;

    /**
     * Internal or external consoel
     */
    public static boolean guiMode = true;   
    
    /**
     * Rendering quality
     */
    private static int renderQuality = 3;
    
    /**
     * allow multiple edges between nodes?
     */
    private static boolean multiEdge = false;

    /**
     * Object to get user preferences set by the menu
     */
    private static Preferences userPrefsMenu = PrefWrapper.userNodeForPackage(GMenuBar.class);
    
    /**
     * Object to save user preferences
     */
    private static Preferences userPrefs = PrefWrapper.userRoot().node("/com/hp/hpl/guess");    
	
    
    /**
     * Zooming or Spacing
     */
    public static final int ZOOMING_ZOOM = 1;
    public static final int ZOOMING_SPACE = 2;
    private static int zoomingMode = userPrefs.getInt("zoomingMode", Guess.ZOOMING_ZOOM);
    
    public static void setZooming(int zoom) {
	// Not a new zooming mode
	if (zoom==zoomingMode) return;
	
	// Just for Piccolo atm
	if ((zoom == ZOOMING_SPACE) && (VisFactory.getUIMode()!=VisFactory.PICCOLO)) {
	    System.err.println("Spacing is just implemented for Piccolo at the moment.");
	    return;
	}
	
	zoomingMode = zoom;
		
	// Save current mode
	userPrefs.putInt("zoomingMode", zoom);
	
	// Scale to 1
	((GFrame)myF).getCamera().setViewScale(1);
	
	// Reinit strokes, so that a PFixedWidthStroke is
	// used instead of BasicStroke if zoomingMode is 
	// ZOOMING_SPACE, or the other way around if 
	// ZOOMING_ZOOM is used.
	interpSingleton.exec("g.edges.width = g.edges.width");
	interpSingleton.exec("g.nodes.strokewidth = 1.5");
	
	// center view
	((GFrame)myF).centerFast();
    }
    
    public static int getZooming() {
	return zoomingMode;
    }

    private static boolean zoomStart = true;

    public static boolean getZoomStart() {
	return zoomStart;
    }

    public static void setZoomStart(boolean zs) {
	zoomStart = zs;
    }

    /**
     * allow multiple edges
     */
    public static boolean allowMultiEdge() {
	return(multiEdge);
    }

    /**
     * enable the main UI window?
     */
    public static void enableMainUI(boolean state) {
	enableMainUI = state;
    }

    /**
     * set the gplfree mode
     */
    public static void setGPLFreeMode(boolean state) {
	gplFree = state;
    }

    /**
     * get the gplfree mode
     */
    public static boolean getGPLFreeMode() {
	return(gplFree);
    }

    /**
     * running as a signed applet?
     */
    public static void setSignedAppletMode(boolean state) {
	signedAppletMode = state;
    }

    /**
     * running as a signed applet?
     */
    public static boolean getSignedAppletMode() {
	return(signedAppletMode);
    }
    
    /**
     * running inside an applet? true for yes, default is false
     */
    public static void setAppletMode(boolean state, 
				     AppletContext ac) {
	appletMode = state;
	myAC = ac;
    }

    /**
     * are we running inside an applet?
     */
    public static boolean getAppletMode() {
	return(appletMode);
    }

    /**
     * get the applet context
     * @pyexport
     */
    public static AppletContext getAppletContext() {
	return(myAC);
    }

    /**
     * call this first if you want to use the "fake" interpreter.
     * The fake one won't let you execute any commands through
     * jython but may be ok if you're just building a simple
     * applet
     * @param true to enable fake one, false (default) otherwise
     */
    public static void useFakeInterpreter(boolean state) {
	fakeInterp = state;
    }

    /**
     * get the interpreter (fake or real depending on
     * useFakeInterpreter setting)
     * @return a jython interpreter
     */
    public static InterpreterAbstraction getInterpreter() {
	if (interpSingleton == null) {
	    if (fakeInterp) {
		interpSingleton = new FakeInterpreter();
	    } else {
		PySystemState.initialize();
		interpSingleton = new RealInterpreter();
	    }
	}
	return(interpSingleton);
    }

    /**
     * set the frame used for synchro issues
     * @param gf the frame 
     */
    public static void setFrame(FrameListener gf) {
	myF = gf;
    }

    public static FrameListener getFrame() {
	return(myF);
    }

    private static TextPaneJythonConsole tpjc = null;

    /**
     * gets a reference to the existing GUI console object, or
     * null if it doesn't exist
     * @pyexport
     */
    public static TextPaneJythonConsole getJythonConsole() {
	return(tpjc);
    }

    /**
     * @pyexport
     */
    public static void help(String tohelp) {
	if (tpjc != null) {
	    tpjc.help(tohelp);
	}
    }

    /**
     * gets the current working graph, if the sytem
     * hasn't been inited you get back null
     * @return the working graph or null
     */
    public static Graph getGraph() {
	return(g);
    }

    private static MainUIWindow myWin = null;

    public static boolean nowarn = false;

    public static MainUIWindow getMainUIWindow() {
	return(myWin);
    }

    public static String defaultFont = null;

    public static String defaultFileFormat = null;

    public static String getDefaultFont() {
	if ((defaultFont != null) && (defaultFont.equals("GUESSFONT"))) {
	    defaultFont = null;
	}
	return(defaultFont);
    }

    public static void setDefaultFont(String font) {
	defaultFont = font;
    }

    public static String getDefaultFileFormat() {
	return(defaultFileFormat);
    }

    public static void setCacheDir() {
	Properties prop = System.getProperties();
	String tempdir = System.getProperty("java.io.tmpdir");
	if (tempdir == null) {
	    tempdir = "";
	}
	tempdir = tempdir + File.separatorChar + "cachedir";
	prop.setProperty("python.cachedir",tempdir);
    }

    /**
     * the main loop, this gets used when you're not using 
     * the applet version
     * @param argv arguments
     */
    public static void main(String[] argv)
	throws Exception {

	//Lm.verifyLicense("GUESS", "GUESS",
	//		 "kaiS04IaJ.QjUq.ZLB0OWobuNMddGb41");

	try {
		//UIManager.setLookAndFeel(Options.getSystemLookAndFeelClassName());
	    configureUI();
	} catch (Exception lnfe) { 
	}
	
	LongOpt[] longopts = new LongOpt[17];
	longopts[0] = new LongOpt("prefuse", LongOpt.NO_ARGUMENT, null, 'p');
	longopts[1] = new LongOpt("touchgraph", 
				  LongOpt.NO_ARGUMENT, null, 't'); 
	longopts[2] = new LongOpt("console", LongOpt.NO_ARGUMENT, null, 'c'); 
	longopts[3] = new LongOpt("persistent", 
				  LongOpt.REQUIRED_ARGUMENT, null, 'o'); 
	longopts[4] = new LongOpt("gplfree", LongOpt.NO_ARGUMENT, null, 'f'); 
	longopts[5] = new LongOpt("nowarn", LongOpt.NO_ARGUMENT, null, 'n'); 
	longopts[6] = new LongOpt("novis", LongOpt.NO_ARGUMENT, null, 'v'); 
	longopts[7] = new LongOpt("multiedge", 
				  LongOpt.NO_ARGUMENT, null, 'm'); 
	longopts[8] = new LongOpt("fontsize", 
				  LongOpt.REQUIRED_ARGUMENT, null, 's'); 
	longopts[9] = new LongOpt("jung", LongOpt.NO_ARGUMENT, null, 'j');
	longopts[10] = new LongOpt("consolelog", 
				  LongOpt.NO_ARGUMENT, null, 'l');
	longopts[11] = new LongOpt("font", 
				  LongOpt.REQUIRED_ARGUMENT, null, 'a'); 
	longopts[12] = new LongOpt("fileformat", 
				  LongOpt.REQUIRED_ARGUMENT, null, 'b'); 
	longopts[13] = new LongOpt("fitfont", 
				   LongOpt.NO_ARGUMENT, null, 'g');
	longopts[14] = new LongOpt("reset", LongOpt.NO_ARGUMENT, null, 'r');
	longopts[15] = new LongOpt("quality", LongOpt.REQUIRED_ARGUMENT, null, 'q'); 
	longopts[16] = new LongOpt("nocenter", LongOpt.NO_ARGUMENT, null, 'z'); 
	Getopt go = new Getopt("Guess", argv, ":ptcvmofnmslz", longopts);
	go.setOpterr(false);
	int c;

	int uiMode = VisFactory.PICCOLO;


	String persistent = null;

	while ((c = go.getopt()) != -1)
	    {
		switch(c)
		    {
		    case 'p':
			uiMode = VisFactory.PREFUSE;
			break;
		    case 'j':
			uiMode = VisFactory.JUNG;
			break;
		    case 't':
			uiMode = VisFactory.TOUCHGRAPH;
			break;
		    case 'v':
			uiMode = VisFactory.NOVIS;
			break;
		    case 'c':
			guiMode = false;
			break;
		    case 'n':
			nowarn = true;
			break;
		    case 'f':
			System.out.println("****Running in GPL Free Mode****");
			gplFree = true;
			break;
		    case 'o':
			if (go.getOptarg() == null) {
			    System.out.println("Please enter a database directory to use -o/--persistent");
			    System.exit(0);
			} else {
			    persistent = go.getOptarg();
			}
			break;
		    case 's':
			if (go.getOptarg() == null) {
			    System.out.println("Please specify a numerical font size with the -s/--fontsize option");
			    System.exit(0);
			} else {
			    int size = 10;
			    try {
				size = Integer.parseInt(go.getOptarg());
				TextPaneJythonConsole.setFontSize(size);
			    } catch (Exception ne) {
				System.out.println("Please specify a numerical font size with the -s/--fontsize option");
				System.exit(0);
			    }
			}
			break;
		    case 'm':
			System.out.println("allowing multiple edges");
			multiEdge = true;
			break;
		    case 'l':
			System.out.println("STDOUT/STDERR logged to console");
			handleOver = false;
			break;
		    case 'a':
			defaultFont = go.getOptarg();
			break;
		    case 'g':
			defaultFont = "GUESSFONT";
			System.out.println("Trying to find best font");
			break;
		    case 'b':
			defaultFileFormat = go.getOptarg();
			break;
		    case 'z':
			setZoomStart(false);
			break;
		    case 'r':
			Preferences globalPrefs = PrefWrapper.userRoot().node("/com/hp/hpl/guess");
			globalPrefs.removeNode();
			globalPrefs.flush();
			System.out.println("Settings cleared. Good Bye.");
			System.exit(0);
			break;
		    case 'q':
				try {
					renderQuality = Integer.parseInt(go.getOptarg());
				} catch (Exception ne) {
					System.out.println("Quality range is from 0 to 3 with 0 being bad and 3 the best quality. Unkown value given, setting quality to " + renderQuality + ".");
				} 
				if (renderQuality>3) {
					renderQuality = 0;
					System.out.println("Quality range is from 0 to 3 with 0 being bad and 3 the best quality. Given value to big, setting quality to " + renderQuality + ".");				
				}
				break;
		    case ':':
			System.out.print("unknown option: " + (char)c + "\n");
			break;
		    case '?':
			System.out.print("unknown option: " + (char)c + "\n");
			break;
		    default:
			System.out.print("unknown option: " + (char)c + "\n");
			System.exit(0);
		    }
	    }
	
	
	System.out.println("GUESS Version: " + Version.MAJOR_VERSION + " (" + Version.MINOR_VERSION + ")");

	setCacheDir();

	//System.out.println(uiMode);

	String database = null;
	for (int i = go.getOptind(); i < argv.length ; i++) {
	    if ((argv[i].endsWith(".py")) ||
		(argv[i].endsWith(".Py")) ||
		(argv[i].endsWith(".PY"))) {
		if (pythonToExec == null) {
		    pythonToExec = new Vector<String>();
		}
		pythonToExec.addElement(argv[i]);
	    } else {
		database = argv[i];
	    }
	}

	if (database == null) {
	    getDataBase();
	} else {

	    File f = new File(database);

	    String fileExtension = "";

	    if (f.exists()) {
		SunFileFilter filter = 
		    new SunFileFilter();
		fileExtension = filter.getExtension(f);
	    }

	    if (database.equals("null")) {
		// make "null" a special kind of database
		// for people who want to work with a dummy database
		StorageFactory.useDBServer();
		StorageFactory.createEmpty();
	    } else if (fileExtension.equalsIgnoreCase("gdf")) {
		if (persistent != null) {
		    StorageFactory.useDBServer(persistent);
		} else {
		    StorageFactory.useDBServer();
		}
		StorageFactory.loadFromFile(database);
	    } else if ((fileExtension.equalsIgnoreCase("xml")) ||
		       (fileExtension.equalsIgnoreCase("graphml"))) {
		if (persistent != null) {
		    StorageFactory.useDBServer(persistent);
		} else {
		    StorageFactory.useDBServer();
		}
		StorageFactory.createEmpty();
		
		doLater = "g.makeFromGML(\""+
		    database.replace('\\','/')+
		    "\")";
	    } else if ((fileExtension.equalsIgnoreCase("net")) ||
		       (fileExtension.equalsIgnoreCase("paj")) ||
		       (fileExtension.equalsIgnoreCase("pajek"))) {
		if (persistent != null) {
		    StorageFactory.useDBServer(persistent);
		} else {
		    StorageFactory.useDBServer();
		}
		StorageFactory.createEmpty();
		
		doLater = "g.makeFromPajek(\""+
		    database.replace('\\','/')+
		    "\")";
	    } else if (fileExtension.equalsIgnoreCase("dl")) {
		// added for Patrick
		if (persistent != null) {
		    StorageFactory.useDBServer(persistent);
		} else {
		    StorageFactory.useDBServer();
		}
		StorageFactory.createEmpty();
		
		doLater = "g.makeFromDL(\""+
		    database.replace('\\','/')+
		    "\")";
	    } else {
		System.out.println(database + " not found as file, trying to load database");
		//System.out.println("using database: " + database);
		StorageFactory.useDBServer(database);
	    }
	}

	// some extra little set up things to the interp
	InterpreterAbstraction interp = getInterpreter();
	interp.exec("from java.sql import Types");
	interp.exec("from com.hp.hpl.guess.ui import VisFactory");
	interp.exec("from com.hp.hpl.guess.ui import Colors");
	interp.exec("from com.hp.hpl.guess import Subgraph");
	interp.exec("from com.hp.hpl.guess.piccolo import Legend");
	interp.exec("from com.hp.hpl.guess.piccolo import GradientLegend");
	interp.exec("from com.hp.hpl.guess.ui import InfoWindow");
	interp.exec("from com.hp.hpl.guess.ui import DragWindow");
	interp.exec("from com.hp.hpl.guess.ui import DWButton");
	interp.exec("from com.hp.hpl.guess.ui import ExceptionWindow");
	interp.exec("from com.hp.hpl.guess.ui import DrawWindow");
	interp.exec("from com.hp.hpl.guess.util.intervals import Tracker");
	interp.exec("from com.hp.hpl.guess.ui import GraphElementEditorPopup");
	interp.exec("from com.hp.hpl.guess.ui import NodeEditorPopup");
	interp.exec("from com.hp.hpl.guess.ui import EdgeEditorPopup");

	//	((CachedJarsPackageManager)PySystemState.packageManager).addJarToPackages(new java.net.URL("http://www.hpl.hp.com/research/idl/projects/graphs/guess/lib/guess.jar"));

	// final step
	init(uiMode,guiMode,(!guiMode));


    }

    /**
     * when a database is unspecified you get walked through 
     * a few steps, this should only be run once before the init
     * process (see the main loop for an example)
     */
    public static void getDataBase() {
    	
    	final welcomeDialog wd = new welcomeDialog();
    	wd.setVisible(true);
    	
    	int wdResult = wd.getUsersChoice();

    	
    	if (wdResult==-1) {
    		getDataBase();
    		return;
    		
    	} else if ((wdResult==wd.DB_OPEN_DATABASE) && 
    		(existingChooser(wd.getFileNameOpenDatabase()))) {
    		return;
    		
    	} else if ((wdResult==wd.DB_IMPORT_GRAPH_MEMORY) && 
    			(newChooser(false, wd.getFileNameImportGraph(),
    					wd.getNameImportGraph(), wd.getDirectoryImportGraph()))) {
    		return;
    	} else if ((wdResult==wd.DB_IMPORT_GRAPH_PERSISTENT) && 
    			(newChooser(true, wd.getFileNameImportGraph(),
    					wd.getNameImportGraph(), wd.getDirectoryImportGraph())))  {
    		return;
    	} else if ((wdResult==wd.DB_CREATE_EMPTY) && (emptyChooser())) {
    		return;
    		
    	} else {
    		System.out.println("Could not create a database.");
    		System.exit(0);
    	}

   
    getDataBase();
	return;

    }


    /**
     * user seems to want to select from an existing database 
     * @return true if succeeded, false otherwise
     */
    private static boolean existingChooser(String fileName) {
	try {
		StorageFactory.useDBServer(fileName);
		return(true);
	} catch (Exception e) {
	    exceptionHandle(e);
	}
	return(false);
    }

    /**
     * user seems to want to select from new file
     * @return true if succeeded, false otherwise
     */
    private static boolean newChooser(boolean isPersistent, String fileName, 
    		String dbName, String directory) {
	try {
    
		String fileExtension = "";
		File f = new File(fileName);
		
	    if (f.exists()) {
	    	SunFileFilter filter = new SunFileFilter();
	    	fileExtension = filter.getExtension(f);
	    }
	    
		if (fileExtension.equalsIgnoreCase("dl")) {
			// added for Patrick
		    if (isPersistent) {
				StorageFactory.useDBServer(directory + sep + dbName);
			} else {
				StorageFactory.useDBServer();
			}
			StorageFactory.createEmpty();
			doLater = "g.makeFromDL(\""+ fileName.replace('\\','/')+"\")";
				
		}  else if ((fileExtension.equalsIgnoreCase("xml")) ||
			(fileExtension.equalsIgnoreCase("graphml"))) {
		    if (isPersistent) {
				StorageFactory.useDBServer(directory + sep + dbName);
			} else {
				StorageFactory.useDBServer();
			}
			StorageFactory.createEmpty();
			doLater = "g.makeFromGML(\""+fileName.replace('\\','/')+ "\")";
				
		} else if ((fileExtension.equalsIgnoreCase("net")) ||
			   (fileExtension.equalsIgnoreCase("paj")) ||
			   (fileExtension.equalsIgnoreCase("pajek"))) {
		    if (isPersistent) {
				StorageFactory.useDBServer(directory + sep + dbName);
			} else {
				StorageFactory.useDBServer();
			}
			StorageFactory.createEmpty();
			doLater = "g.makeFromPajek(\""+ fileName.replace('\\','/')+ "\")";
				
		} else {
		    if (isPersistent) {
				StorageFactory.useDBServer(directory + sep + dbName);
			} else {
				StorageFactory.useDBServer();
			}
			StorageFactory.loadFromFile(fileName);
		}
		return(true);
	} catch (Exception e) {
	    exceptionHandle(e);
	}
	return(false);
    }

    /**
     * user seems to want to start with a "blank" database
     * @return true if succeeded, false otherwise
     */
    private static boolean emptyChooser() {
	try {
	    //System.out.println("using in memory database");
	    StorageFactory.useDBServer();
	    StorageFactory.createEmpty();
	    return(true);
	} catch (Exception e) {
	    exceptionHandle(e);
	}
	return(false);
    }

    /**
     * do some initial setup to the UI look and feel
     */
    public static void configureUI() {      	   
        try {
            UIManager.setLookAndFeel(Options.getSystemLookAndFeelClassName());
            UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
            LookAndFeelFactory.setDefaultStyle(LookAndFeelFactory.VSNET_STYLE_WITHOUT_MENU);
        } catch (Error e) {
	    System.err.println("Can't set look & feel");
            exceptionHandle(e);
        } catch (Exception ex) {
	    System.err.println("Can't set look & feel");
            exceptionHandle(ex);
	}
    }

    static BufferedReader reader = null;

    public static void exceptionHandle(Throwable e2) {
	if (e2 instanceof PyException) {
	    e2.printStackTrace();
	} else {
	    System.out.println(e2.toString() + 
			       "\n\t(Use Help->Error Log for more details)");
	}
	ExceptionWindow.getExceptionWindow(e2);
    }

    /**
     * the main loop, this will get us running.  You'll usually want
     * either the guiMode or textMode enabled but you can 
     * do both or neither.
     * @param uiMode which ui to use
     * (currently VisFactory.PICCOLO,VisFactory.TOUCGRAPH, 
     * VisFactory.PREFUSE, or VisFactory.NOVIS)
     * @param guiMode run the interpeter as a seperate console
     * @param textMode run the intepreter in the original console
     */
    public static void init(int uiMode, boolean guiMode, boolean textMode) 
	throws Exception {

	initUI(uiMode,guiMode,textMode);
	initRest(uiMode,guiMode,textMode);
    }

    public static BufferedReader outHandle = null;

    public static BufferedReader errHandle = null;

    public static boolean handleOver = true;

    public static void initHandles() throws Exception {

	// override stderr/stdout handles to force them to console

	if (handleOver)
	    return;

	if (outHandle == null) {
	    PipedInputStream pis = new PipedInputStream();
	    PipedOutputStream pos = new PipedOutputStream(pis);
	    System.setOut(new PrintStream(pos));
	    outHandle = new BufferedReader(new InputStreamReader(pis)); 
	}
	if (errHandle == null) {
	    PipedInputStream pis = new PipedInputStream();
	    PipedOutputStream pos = new PipedOutputStream(pis);
	    System.setErr(new PrintStream(pos));
	    errHandle = new BufferedReader(new InputStreamReader(pis)); 
	}
    }
    
    private static boolean sync = false;

    /**
     * should layouts run in their own threads?
     * @pyexport
     */
    public static void setSynchronous(boolean state) {
	sync = state;
    }

    /**
     * thread management
     * @pyexport
     */
    public static boolean getSynchronous() {
	return(sync);
    }

    private static boolean mtf = false;

    /**
     * should objects in the visualization be moved to the front
     * when they change
     * @pyexport
     */
    public static void setMTF(boolean state) {
	mtf = state;
    }

    /**
     * Are objects being moved to the front when they change
     * @pyexport
     */
    public static boolean getMTF() {
	return(mtf);
    }

    public static void initUI(int uiMode, boolean guiMode, boolean textMode) 
	throws Exception {
	if ((textMode) && (guiMode)) {
	    guiMode = true;
	    textMode = false;
	}

	final InterpreterAbstraction interp = getInterpreter();

	final int uiMode2 = uiMode;
	
	try {
	    javax.swing.SwingUtilities.invokeAndWait(new Runnable() { 
		    public void run() {
			VisFactory.setFactory(uiMode2);
		    }
		});
	} catch (Exception e) {
	    exceptionHandle(e);
	}
	
	// Set animation factory
	try {
	    AnimationFactory.setFactory(uiMode);
	} catch (Error e) {
	    exceptionHandle(e);
	}

	final FrameListener myFrame = VisFactory.getFactory().getDisplay();
	setFrame(myFrame);
	
	// Set the background color
	int colorred = userPrefsMenu.getInt("backgroundcolor-red", Color.black.getRed());
	int colorgreen = userPrefsMenu.getInt("backgroundcolor-green", Color.black.getGreen());
	int colorblue = userPrefsMenu.getInt("backgroundcolor-blue", Color.black.getBlue());
	myF.setDisplayBackground(new Color(colorred, colorgreen, colorblue));
	myF.setQuality(renderQuality);

	try {
	    g = new Graph(myFrame, interp, multiEdge);
	    StorageFactory.getSL().refresh(g);
	} catch (Exception e) {
	    exceptionHandle(e);
	}
	
	if (enableMainUI)
	    myWin = new MainUIWindow((java.awt.Component)myFrame);
	
	VisFactory.getFactory().runNow();
	
	/*
	These get out-of-date when g is modified.  Do we still want these?
	interp.exec("nodes = g.vertices");
	interp.exec("edges = g.edges");
	*/
	Iterator<?> nodes = g.getVertices().iterator();
	while (nodes.hasNext())
	    {
		Node node = (Node)nodes.next();
		interp.setImmutable(node.getName(), node);
	    }
	Enumeration<String> en = Colors.colors.keys();
	while(en.hasMoreElements()) {
	    String key = en.nextElement();
	    Color val = (Color)Colors.colors.get(key);
	    interp.setImmutable(key,val.toString());
	}

	if (g.containsDirected()) {
	    VisFactory.getFactory().setDirected(true);
	}
	
	
	
	// set context menu layouts
	NodeEditorPopup.addLayoutItem("Neighbour (Level 1)").addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			GStateAction layoutAction = new GStateAction() {
				public void actionContent() {
					Iterator<GraphElement> nodeIterator = NodeEditorPopup.getSelected().iterator();
					while (nodeIterator.hasNext()) {
						GraphElement next = (GraphElement) nodeIterator.next();
						if (next instanceof Node) {
							Guess.getGraph().layout(new Neighbour(Guess.getGraph(), (Node) next, 1));
						}
					}
				}
			};
			GActionManager.runAction(layoutAction, "Neighbour Level 1");
			StatusBar.setStatus("Neighbour Level 1");
		}
	});
	
	NodeEditorPopup.addLayoutItem("Neighbour (Level 2)").addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			GStateAction layoutAction = new GStateAction() {
				public void actionContent() {
					Iterator<GraphElement> nodeIterator = NodeEditorPopup.getSelected().iterator();
					while (nodeIterator.hasNext()) {
						GraphElement next = (GraphElement) nodeIterator.next();
						if (next instanceof Node) {
							Guess.getGraph().layout(new Neighbour(Guess.getGraph(), (Node) next, 2));
						}
					}
				}
			};
			GActionManager.runAction(layoutAction, "Neighbour Level 2");
			StatusBar.setStatus("Neighbour Level 2");
		}
	});
	
	NodeEditorPopup.addLayoutItem("Neighbour (Level 3)").addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			GStateAction layoutAction = new GStateAction() {
				public void actionContent() {
					Iterator<GraphElement> nodeIterator = NodeEditorPopup.getSelected().iterator();
					while (nodeIterator.hasNext()) {
						GraphElement next = (GraphElement) nodeIterator.next();
						if (next instanceof Node) {
							Guess.getGraph().layout(new Neighbour(Guess.getGraph(), (Node) next, 3));
						}
					}
				}
			};
			GActionManager.runAction(layoutAction, "Neighbour Level 3");
			StatusBar.setStatus("Neighbour Level 3");
		}
	});
	
	NodeEditorPopup.addLayoutItem("Radial").addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			GStateAction layoutAction = new GStateAction() {
				public void actionContent() {
					Iterator<GraphElement> nodeIterator = NodeEditorPopup.getSelected().iterator();
					while (nodeIterator.hasNext()) {
						GraphElement next = (GraphElement) nodeIterator.next();
						if (next instanceof Node) {
							Guess.getGraph().layout(new Radial(Guess.getGraph(), (Node) next));
						}
					}
				}
			};
			GActionManager.runAction(layoutAction, "Radial Layout");
			StatusBar.setStatus("Radial Layout");
		}
	});
	
    }

    public static void initRest(int uiMode, boolean guiMode, boolean textMode) 
	throws Exception {

	if (guiMode) {
	    initHandles();
	}

	final FrameListener myFrame = VisFactory.getFactory().getDisplay();
	final InterpreterAbstraction interp = getInterpreter();

	interp.setImmutable("v", myFrame);
	interp.setImmutable("true",new Integer(1));
	interp.setImmutable("false",new Integer(0));
	
	interp.setImmutable("Node", g.getNodeSchema());
	interp.setImmutable("Edge", g.getEdgeSchema());
	interp.setImmutable("g", g);
	interp.setImmutable("db", StorageFactory.getSL());
	R myR = new R();
	interp.setImmutable("r",myR);
	interp.setImmutable("interp",interp);
	interp.setImmutable("vf",VisFactory.getFactory());
	interp.setImmutable("graphevents",GraphEvents.getGraphEvents());
	interp.setImmutable("shapeDB",ShapeDB.getShapeDB());

	if (enableMainUI) {
	    interp.setImmutable("ui",myWin);
	    
	    try {
		java.net.URL mbpy = 
		    interp.getClass().getResource("/scripts/MenuBar.py");
		//System.out.println("menu: " + mbpy);
		if (mbpy != null) {
		    interp.execfile(mbpy.openStream());
		}
	    } catch (Throwable fe) {
		exceptionHandle(fe);
	    }
	    
	    myWin.validate();
	}

	try {
	    java.net.URL mbpy = null;
	    mbpy = 
		interp.getClass().getResource("/scripts/Main.py");
	    if (mbpy != null) {
		interp.execfile(mbpy.openStream());
	    }
	} catch (Throwable fe) {
	    exceptionHandle(fe);
	    try {
		if (appletMode) {
		    java.net.URL mbpy = null;
		    mbpy = 
			interp.getClass().getResource("/scripts/Main-applet.py");
		    if (mbpy != null) {
			interp.execfile(mbpy.openStream());
		    }
		}
	    } catch (Throwable fe2) {
		exceptionHandle(fe2);
	    }
	}

	//CommandStack cs = new CommandStack(interp,"current.cs");
	//interp.set("cs",cs);
	//cs.setVisible(true);

	Iterator<?> fields = g.getEdgeSchema().fields();
	while (fields.hasNext())
	{
		Field field = (Field)fields.next();
		interp.setImmutable(field.getName(), field);
	}

	fields = g.getNodeSchema().fields();
	while (fields.hasNext())
	{
		Field field = (Field)fields.next();
		interp.setImmutable(field.getName(), field);
	}

	if (guiMode) {	
	    if (gplFree) {
		System.out.println("running in GPL free mode, unable to use UI console, reverting to text...");
		guiMode = false;
		textMode = true;
	    } else {
		if (enableMainUI) {
		    tpjc = new TextPaneJythonConsole((PythonInterpreter)interp);
		    myWin.dock(tpjc);
		    
		    // Show console?
		    if (!userPrefsMenu.getBoolean("openConsole", true)) {
			getMainUIWindow().close(tpjc);
		    }
		    // show information window?
		    if (userPrefsMenu.getBoolean("openInformationWindow", false)) {
			InfoWindow.create();
		    }
		    // show video window?
		    if (userPrefsMenu.getBoolean("openVideoWindow", false)) {
			VideoWindow.create();
		    }			
		    // run in fullscreen?
		    if (userPrefsMenu.getBoolean("openFullscreen", false)) {
			Guess.getMainUIWindow().setFullScreenMode(true);
		    }
		    
		}
	    }
	    //LabNotebook.createNotebook((PythonInterpreter)interp);
	}
	
	myFrame.repaint();

	if (doLater != null) {
	    final InterpreterAbstraction ia = interp;
	    final String doLater2 = doLater;
	    try {
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() { 
			public void run() {
			    ia.exec(doLater2);
			}
		    });
	    } catch (Exception e) {
		exceptionHandle(e);
	    }

	} 
	
	if (pythonToExec != null) {
	    Iterator<String> it = pythonToExec.iterator();
	    while(it.hasNext()) {
		final String fl = it.next();
		try {
		    javax.swing.SwingUtilities.invokeAndWait(new Runnable() { 
			    public void run() {
				interp.execfile(fl);
				StatusBar.setStatus("executed: " + fl);
			    }
			});
		} catch (Exception e) {
		    exceptionHandle(e);
		}
	    }
	}

	if (getZoomStart()) 
	    myFrame.center();

	//System.out.println(myFrame);

	//new DragWindow();

	if (textMode) {
	    reader = new BufferedReader(new InputStreamReader(System.in));
	    String s = readLine();

	    Tracker.disableTracker(); // we don't care about tracking

	    while (!s.equals("quit")) {
		if (s.equals("rmode")) {
		    myR.rmode(reader);  // let R do everything
		    s = readLine();
		    continue;
		}
		final String s2 = s;
		// because certain things aren't thread safe we're going
		// to run commands inside an invokeLater thread
		// 
		// this kind of sucks because we have to make a new thread
		// for every command, but it's the only way to make things
		// thread safe.  
		//
		// could potentially add commands to a queue in a
		// a swing thread?
		//
		try {
		    StatusBar.setStatus("");
		    javax.swing.SwingUtilities.invokeAndWait(new Runnable() { 
			    public void run() { 
				try {
				    Object value = interp.eval(s2);
				    
				    checkFrozen(interp);
				    interp.set("_", value);
				    
				    if (value instanceof PyFunction) {
					checkFrozen(interp);
					interp.set("_", 
						   interp.eval("apply(_, ())"));
				    }
				    
				    checkFrozen(interp);
				    interp.exec("if _ != None: print _");
				} catch(PySyntaxError e)
				    {
					try
					    {
						checkFrozen(interp);
						interp.exec(s2);
					    }
					catch(Throwable e2) {
					    exceptionHandle(e2);	    
					}
				    } catch(Throwable e3) {
					exceptionHandle(e3);
				    }
				myFrame.repaint();
			    } 
			}); 
		} catch (InterruptedException e) {
		    exceptionHandle(e);
		}
		checkFrozen(interp);
		s = readLine();
	    }
	    shutdown();
	}
    }
    
    public static void checkFrozen(InterpreterAbstraction interp) {
	while(interp.isFrozen()) {
	    try {
		Thread.sleep(100);
	    } catch (Exception e) {
		interp.freeze(false);
	    }
	}
    }

    /**
     * shutdown and exit
     */
    public static void shutdown() {
	if (interpSingleton != null) 
	    interpSingleton.stoplog();

	StorageFactory.shutdown();
	myF = null;
	g = null;
	interpSingleton = null;
	VisFactory.shutdown();
	//FDN73 needed for use in external apps
	AnimationFactory.shutdown();
	
	if (tpjc != null) {
	    tpjc.shutdown();
	}

	if (myWin != null)
	    myWin.dispose();

	myWin = null;
	//FDN73 check if we're not in applet mode or
	//if we want to shutdown on exit
	if (!shutdownOnExit)
		return;
	else if (!appletMode)
	    System.exit(0);
    }

    /** 
     * reads the next line from the display (blocking)
     */
    private static String readLine() {
	System.out.print("> ");
	
	try
	    {
		String s = reader.readLine();
		if (s.endsWith(":")) {
		    System.out.print(". ");
		    String t = reader.readLine();
		    while(!t.equals("")) {
			s = s + "\n" + t;
			System.out.print(". ");
			t = reader.readLine();
		    }
		}
		return(s);
	    }
	catch (IOException e)
	    {
		throw new Error(e);
	    }
    }

	public static boolean isShutdownOnExit() {
		return shutdownOnExit;
	}

	public static void setShutdownOnExit(boolean shutdownOnExit) {
		Guess.shutdownOnExit = shutdownOnExit;
	}

	
}

