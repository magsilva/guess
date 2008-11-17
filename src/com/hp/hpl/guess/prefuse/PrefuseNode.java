package com.hp.hpl.guess.prefuse;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

import com.hp.hpl.guess.ui.Colors;
import com.hp.hpl.guess.ui.NodeListener;

public class PrefuseNode implements NodeListener {
    
    protected prefuse.data.Node iNode = null;
    
    public PrefuseNode(prefuse.data.Graph g) {
	iNode = g.addNode();
    }

    public Rectangle2D getBounds() {
	return(PrefuseFactory.m_vis.getVisualItem("graph.nodes",
						  iNode).getBounds());
    }

    public Object get(String field) {
	VisualItem vi = PrefuseFactory.m_vis.getVisualItem("graph.nodes",
							   iNode);
	if (field.equals("label")) {
	    return(iNode.get(field));
	} else if (field.equals("color")) {
	    return(ColorLib.getColor(vi.getFillColor()));
	} else if (field.equals("strokecolor")) {
	    return(ColorLib.getColor(vi.getStrokeColor()));
	} else if (field.equals("x")) {
	    return(new Double(vi.getX()));
	} else if (field.equals("y")) {
	    return(new Double(vi.getY()));
	}
	return(null);
    }
    
    public void set(String field, Object value) {
	VisualItem vi = 
	    PrefuseFactory.m_vis.getVisualItem("graph.nodes",
					       iNode);
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
	    vi.setFillColor(ColorLib.color(temp));
	} else if (field.equals("x")) {
	    vi.setX(((Double)value).doubleValue());
	    vi.setStartX(((Double)value).doubleValue());
	    vi.setEndX(((Double)value).doubleValue());
	} else if (field.equals("y")) {
	    vi.setY(((Double)value).doubleValue());
	    vi.setStartY(((Double)value).doubleValue());
	    vi.setEndY(((Double)value).doubleValue());
	} else if (field.equals("strokecolor")) {
	    Color temp = null;
	    if (value instanceof Color) {
		temp = (Color)value;
	    } else {
		temp = (Colors.getColor((String)value,
					Color.blue));
	    }
	    vi.setStrokeColor(ColorLib.color(temp));
	}
    }
    
    public void highlight(boolean state) {
    }
    
    public void setLocation(double x, double y) {
    }
    
    public void setLocation(double x, double y, double width, double height) {
    }
    
	public void addFieldToLabel(String field) {
		System.out.println("Not implemented yet");
	}

	public void removeFieldFromLabel(String field) {
		System.out.println("Not implemented yet");
	}
}
