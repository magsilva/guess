package com.hp.hpl.guess.prefuse;

import com.hp.hpl.guess.ui.NodeListener;
import prefuse.data.Node;
import prefuse.visual.NodeItem;
import prefuse.data.Graph;
import prefuse.util.ColorLib;
import com.hp.hpl.guess.ui.Colors;
import java.awt.Color;
import prefuse.visual.VisualItem;

public class PrefuseNode implements NodeListener {
    
    protected prefuse.data.Node iNode = null;
    
    public PrefuseNode(prefuse.data.Graph g) {
	iNode = g.addNode();
	//System.out.println();
	//System.err.println(iNode.getClass());
    }

    public Object get(String field) {
	if (field.equals("label")) {
	    return(iNode.get(field));
	} else if (field.equals("color")) {
	    VisualItem vi = PrefuseFactory.m_vis.getVisualItem("graph.nodes",
							       iNode);
	    return(ColorLib.getColor(vi.getFillColor()));
	}
	return(null);
    }
    
    public void set(String field, Object value) {
	if (field.equals("label")) {
	    iNode.set(field,value);
	} else if (field.equals("color")) {
	    Color temp = null;
	    VisualItem vi = PrefuseFactory.m_vis.getVisualItem("graph.nodes",
							       iNode);
	    if (value instanceof Color) {
		temp = (Color)value;
	    } else {
		temp = (Colors.getColor((String)value,
					Color.blue));
	    }
	    vi.setFillColor(ColorLib.color(temp));
	}
    }
    
    public void highlight(boolean state) {
    }
    
    public void setLocation(double x, double y) {
    }
    
    public void setLocation(double x, double y, double width, double height) {
    }
}
