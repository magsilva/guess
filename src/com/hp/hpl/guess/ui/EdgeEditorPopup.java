package com.hp.hpl.guess.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.InterpreterAbstraction;

public class EdgeEditorPopup extends GraphElementEditorPopup {

	private static final long serialVersionUID = 1L;
	public static EditorPopup singleton = null;

	
	private static JMenu animMenu = null;
	public static JMenuItem addItemToAnimation(String s) {
		EditorPopup ep = getPopup();

		if (!ep.sep) {
			ep.addSeparator();
			ep.sep = true;
		}

		JMenuItem jmi = ep.createJMI(s);

		if (animMenu == null) {
			animMenu = new JMenu("Animations");
			ep.add(animMenu);
		}

		animMenu.add(jmi);
		return (jmi);
	}
	
	public static JMenuItem addItem(String s) {
		EditorPopup ep = getPopup();

		if (!ep.sep) {
			ep.addSeparator();
			ep.sep = true;
		}

		JMenuItem jmi = ep.createJMI(s);
		ep.add(jmi);
		return (jmi);
	}

	public static EditorPopup getPopup() {
		if (singleton == null) {
			singleton = new EdgeEditorPopup(Guess.getInterpreter());
		}
		return (singleton);
	}

	protected EdgeEditorPopup(InterpreterAbstraction jython) {
		super(jython);
		setLabel("Edge Menu");

		// add functions here

	}
}
