package com.hp.hpl.guess.ui;

import javax.swing.*;

public abstract class DockableAdapter extends JPanel implements Dockable {
    
	private static final long serialVersionUID = 1L;

	public int getDirectionPreference() {
	return(MainUIWindow.HORIZONTAL_DOCK);
    }

    public void opening(boolean state) {
	//System.out.println("opening: " + state);
    }

    public void attaching(boolean state) {
	//System.out.println("attaching: " + state);
	if ((state == true) && (myParent != null))
	    myParent.setVisible(false);	
    }

    public String getTitle() {
	return("");
    }

    public String toString() {
	return(getTitle());
    }

    private GuessJFrame myParent = null;

    public GuessJFrame getWindow() {
	return(myParent);
    }

    public void setWindow(GuessJFrame gjf) {
	myParent = gjf;
    }
}
