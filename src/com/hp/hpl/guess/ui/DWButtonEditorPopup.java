package com.hp.hpl.guess.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.InterpreterAbstraction;

public class DWButtonEditorPopup extends EditorPopup {
    
	private static final long serialVersionUID = 436045880042956238L;

	private String[ ] menuItems = new String[ ] {"View Exception Log"};

    public static EditorPopup singleton = null;

    public static JMenuItem addItem(String s) {
	EditorPopup ep = getPopup();

	if (!ep.sep) {
	    ep.addSeparator();
	    ep.sep = true;
	}

	JMenuItem jmi = ep.createJMI(s);
	ep.add(jmi);
	return(jmi);
    }

    public String getHeader() {
	return("Exception Options");
    }

    public static EditorPopup getPopup() {
	if (singleton == null) {
	    singleton = new DWButtonEditorPopup(Guess.getInterpreter());
	}
	return(singleton);
    }

    protected DWButtonEditorPopup(InterpreterAbstraction jython) {
	super(jython);
	setLabel("Exception/Error");

	ActionListener al = new ActionListener(  ) {
		public void actionPerformed(ActionEvent event) {
		    String ac = event.getActionCommand();
		    if (ac.equals("View Exception Log")) {
			ExceptionWindow.getExceptionWindow().setVisible(true);
		    } 
		} 
	    };
	
	for (int i = 0 ; i < menuItems.length ; i++) {
	    JMenuItem jmi1 = add(menuItems[i]);
	    jmi1.addActionListener(al);
	}
    }
}
