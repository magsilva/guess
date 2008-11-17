package com.hp.hpl.guess.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.hp.hpl.guess.Edge;
import com.hp.hpl.guess.GraphElement;
import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.InterpreterAbstraction;
import com.hp.hpl.guess.Node;

public class GraphElementEditorPopup extends EditorPopup {

	private static final long serialVersionUID = -4928702752674521095L;

	String[ ] menuItems = new String[ ] { "Center On",
					  "Color...",
					  "Remove",
					  "Add",
					  "Modify Field...",
					  "Copy as Variable..."};

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

    public static EditorPopup getPopup() {
	if (singleton == null) {
	    singleton = new GraphElementEditorPopup(Guess.getInterpreter());
	}
	return(singleton);
    }

    protected GraphElementEditorPopup(InterpreterAbstraction jython) {
	super(jython);
	setLabel("Graph Element Menu");

	ActionListener al = new ActionListener(  ) {
		public void actionPerformed(ActionEvent event) {
		    String ac = event.getActionCommand();
		    if (ac.equals("Center On")) {
			centerOn();
		    } else if (ac.equals("Color...")) {
			colorSelected();
		    } else if (ac.equals("Copy as Variable...")) {
			copyAsVariable();
		    } else if (ac.equals("Add")) {
			addSelected();
		    } else if (ac.equals("Remove")) {
			removeSelected();
		    } else if (ac.equals("Modify Field...")) {
			modifyWindow();
		    }
		} 
	    };

	for (int i = 0 ; i < menuItems.length ; i++) {
	    JMenuItem jmi1 = add(menuItems[i]);
	    jmi1.addActionListener(al);
	    
	    // Makes the first item the default menuitem
	    // see http://java.sun.com/products/jlf/at/book/Menus9.html
	    if (i==0) {
	    	Font DefaultFont = jmi1.getFont();
	    	jmi1.setFont(DefaultFont.deriveFont(Font.BOLD));
	    	addSeparator();
	    }
	}
    }

    public void modifyWindow() {
	HashSet<GraphElement> nodes = new HashSet<GraphElement>();
	HashSet<GraphElement> edges = new HashSet<GraphElement>();
	Iterator<GraphElement> it = selected.iterator();
	while(it.hasNext()) {
	    GraphElement ge = (GraphElement)it.next();
	    if (ge instanceof Node)
		nodes.add(ge);
	    else if (ge instanceof Edge)
		edges.add(ge);
	}
	FieldModWindow.getFieldModWindow(nodes,edges);
    }

    public void addSelected() {
	if (selected != null) {
	    Iterator<GraphElement> it = selected.iterator();
	    while(it.hasNext()) {
		GraphElement ge = (GraphElement)it.next();
		if (ge instanceof Node)
		    Guess.getGraph().addNode((Node)ge);
		else if (ge instanceof Edge)
		    Guess.getGraph().addEdge((Edge)ge);
	    }
	}
    }

    public void removeSelected() {
	if (selected != null) {
	    Iterator<GraphElement> it = selected.iterator();
	    while(it.hasNext()) {
		GraphElement ge = (GraphElement)it.next();
		if (ge instanceof Node)
		    Guess.getGraph().removeNode((Node)ge);
		else if (ge instanceof Edge)
		    Guess.getGraph().removeEdge((Edge)ge);
	    }
	}
    }

    public void copyAsVariable() {

	if (currentH == null)
	    return;

	String ch = currentH.toString();
	if (ch.length() > 60) {
	    ch = ch.substring(0,60)+"...";
	}

	String s = 
	    (String)JOptionPane.showInputDialog(null,
						"Please enter a variable name for:\n"
						+ ch,
						"Customized Dialog",
						JOptionPane.PLAIN_MESSAGE,
						UIManager.getIcon("OptionPane.questionIcon"),
						null,
						"");
	
	if ((s != null) && (s.length() > 0)) {
	    jython.set(s,currentH);
	    StatusBar.setStatus(s + " = " + currentH);
	}
    }


    public void colorSelected() {
	Color c = 
	    JColorChooser.showDialog(null,
				     "Please pick a color - GUESS",
				     Color.yellow);
	if (selected == null)
	    return;

	if (c != null) {
	    Object col = c.getRed()+","+
		c.getGreen()+","+
		c.getBlue();
	    Iterator<GraphElement> it = selected.iterator();
	    while(it.hasNext()) {
		GraphElement ge = (GraphElement)it.next();
		ge.__setattr__("color",col);
	    }
	}
    }

    public void centerOn() {
	if (selected == null)
	    return;

	VisFactory.getFactory().getDisplay().center(selected);
    }
}
