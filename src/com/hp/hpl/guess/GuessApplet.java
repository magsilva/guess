package com.hp.hpl.guess;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import com.hp.hpl.guess.*;
import org.python.core.*;
import org.python.util.*;
import edu.uci.ics.jung.graph.*;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import com.hp.hpl.guess.db.*;
import com.hp.hpl.guess.storage.*;
import com.hp.hpl.guess.ui.*;
import java.applet.*;
import java.lang.reflect.Constructor;

public class GuessApplet extends Applet
{
    public void init() {
	try {
	    PySystemState.initializeApplet();
	    Guess.setAppletMode(true,getAppletContext());
	    Guess.configureUI();
	    Guess.useFakeInterpreter(true);
	    System.out.println("init...");
	    String applet_db = getParameter("DB");
	    applet_db = applet_db.replace(';', '\n');
	    StorageFactory.useDBServer();
	    StorageFactory.loadFromText(applet_db);
	    Guess.init(VisFactory.PICCOLO,false,false);
	    String toolbar = getParameter("TOOLBAR");
	    //System.out.println("tb: " + toolbar);
	    if ((toolbar != null) && (!toolbar.equals(""))) {
		Class tb = Class.forName(toolbar);
		Constructor tcon = tb.getConstructor(null);
		Object o = tcon.newInstance(null);
		//System.out.println(o.getClass());
		//Guess.getMainUIWindow().dock((DockableAdapter)o);
	    }
	} catch (Exception e) {
	    ExceptionWindow.getExceptionWindow(e);
	    e.printStackTrace();
	}
    }

    public void stop() {
	//System.out.println("stop...");
	destory();
	super.stop();
    }

    public void destory() {
	//System.out.println("destroy...");
	Guess.shutdown();
	super.destroy();
    }
}
