package com.hp.hpl.guess.prefuse;

import com.hp.hpl.guess.ui.NodeListener;
import prefuse.data.Node;
import prefuse.visual.NodeItem;
import prefuse.data.Graph;
import prefuse.util.ColorLib;
import com.hp.hpl.guess.ui.Colors;
import java.awt.Color;

public class PrefuseNode implements NodeListener {
    
    protected prefuse.data.Node iNode = null;

    public PrefuseNode(prefuse.data.Graph g) {
	iNode = g.addNode();
	//System.err.println(iNode.getClass());
    }

    public Object get(String field) {
	if (field.equals("label")) {
	    return(iNode.get(field));
	} else if (field.equals("color")) {
	    //  return(ColorLib.getColor(iNode.getFillColor()));
	}
	return(null);
    }
    
    public void set(String field, Object value) {
	if (field.equals("label")) {
	    iNode.set(field,value);
	} else if (field.equals("color")) {
	    Color temp = null;
	    if (value instanceof Color) {
		temp = (Color)value;
	    } else {
		temp = (Colors.getColor((String)value,
					Color.blue));
	    }
	    //iNode.setFillColor(ColorLib.color(temp));
	}
    }
    
    public void highlight(boolean state) {
    }
    
    public void setLocation(double x, double y) {
    }
    
    public void setLocation(double x, double y, double width, double height) {
    }
}
