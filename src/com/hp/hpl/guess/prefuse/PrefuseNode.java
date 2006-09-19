package com.hp.hpl.guess.prefuse;

import com.hp.hpl.guess.ui.NodeListener;
import prefuse.data.Node;
import prefuse.data.Graph;

public class PrefuseNode implements NodeListener {
    
    protected prefuse.data.Node iNode = null;

    public PrefuseNode(prefuse.data.Graph g) {
	//	iNode = g.addNode();
    }

    public Object get(String field) {
	return(null);
    }
    
    public void set(String field, Object value) {
    }
    
    public void highlight(boolean state) {
    }
    
    public void setLocation(double x, double y) {
    }
    
    public void setLocation(double x, double y, double width, double height) {
    }
}
