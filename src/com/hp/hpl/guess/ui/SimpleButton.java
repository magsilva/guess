package com.hp.hpl.guess.ui;

import java.awt.*;

import javax.swing.*;


public class SimpleButton extends JButton {

	private static final long serialVersionUID = 1L;

	Dimension size = new Dimension(20,20);
    
    public int bType = 0;
    
    public SimpleButton(String s, int bType) {
	this(s,bType,null);
    }

    public SimpleButton(String s, int bType, String tt) {
	super();
	setIcon(new ImageIcon(getClass().getResource("/images/"+s)));
	this.bType = bType;
	if (tt != null)
	    setToolTipText(tt);
    }
    
    public void click(boolean state) {
	    setSelected(state);
    }

    public Dimension getMinimumSize() {
    	return(size);
    }
    public Dimension getMaximumSize() {
    	return(size);
    }
    
    public Dimension getPreferredSize() {
    	return(size);
    }
}