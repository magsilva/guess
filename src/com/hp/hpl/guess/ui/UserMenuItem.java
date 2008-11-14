package com.hp.hpl.guess.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenuItem;

public class UserMenuItem extends JMenuItem implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Vector<MenuListener> menuListeners = new Vector<MenuListener>();

    public UserMenuItem(String s, EditorPopup ep) {
		super(s);
		addActionListener(this);
    }

    public void addUIListener(MenuListener al) {
    	menuListeners.addElement(al);
    }
    
    public void notifyEvent() {
		for (int i = 0 ; i < menuListeners.size() ; i++) {
		    ((MenuListener)menuListeners.elementAt(i)).menuEvent(EditorPopup.selected);
		}
    }

    public void actionPerformed(ActionEvent e) {
    	notifyEvent();
    }
}
