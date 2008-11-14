package com.hp.hpl.guess.ui;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.hp.hpl.guess.GraphElement;
import com.hp.hpl.guess.InterpreterAbstraction;

public abstract class EditorPopup extends JPopupMenu {
    
	private static final long serialVersionUID = 1L;
	
	InterpreterAbstraction jython = null;
    protected static Collection<GraphElement> selected = null;
    protected Object currentH = null;

    protected boolean sep = false;
   
    public static EditorPopup getPopup() {
    	return(null);
    }

    public static Collection<GraphElement> getSelected() {
    	return selected;
    }
    
    protected EditorPopup(InterpreterAbstraction jython) {
		super("Options");
		this.jython = jython;
    }

    public void show(Component inv, int x, int y, 
		     Collection<GraphElement> selected, Object currentH) {
		EditorPopup.selected = selected;
		this.currentH = currentH;
		show(inv,x,y);
    }

    public JMenuItem createJMI(String s) {
    	return(new UserMenuItem(s,this));
    }
    
    public void cacheContent(Collection<GraphElement> selected, Object currentH) {
		EditorPopup.selected = selected;
		this.currentH = currentH;
    }
}
