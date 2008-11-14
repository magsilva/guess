package com.hp.hpl.guess.ui;

import javax.swing.*;

import com.jidesoft.swing.JideButton;

import java.awt.*;
import java.util.*;

public class DWButton extends JideButton 
    implements GuessDropListener, Interesting {
    
	private static final long serialVersionUID = 1L;

	private HashSet<GuessDropListener> listeners = new HashSet<GuessDropListener>();

    private String nm = null;

    public void addGuessDropListener(GuessDropListener gdl) {
	listeners.add(gdl);
    }
    
    public void removeGuessDropListener(GuessDropListener gdl) {
	listeners.remove(gdl);
    }
    
    public DWButton(String category, String name) {
	super(name);
	this.nm = category + " / " + name;
	setButtonStyle(JideButton.HYPERLINK_STYLE);
	
	setOpaque(false);
	setPreferredSize(new Dimension(0, 20));
	setHorizontalAlignment(SwingConstants.LEADING);
	
	setRequestFocusEnabled(true);
	setFocusable(true);
	
	setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	setTransferHandler(new GuessTransferHandler());
	DragWindow.create();
	DragWindow.getDragWindow().addButton(category, this);
    }
    
    public void receiveDrop(Object o) {
	Iterator<GuessDropListener> it = listeners.iterator();
	//System.out.println(o);
	while(it.hasNext()) {
	    //System.out.println("calling...");
	    it.next().receiveDrop(o);
	}
    }

    public String toString() {
	//Thread.dumpStack();
	return(nm);
    }

    public String getStatusBarString() {
	return(nm);
    }

    public EditorPopup getPopup() {
	return(null);
    }
}
