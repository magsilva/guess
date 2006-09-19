package com.hp.hpl.guess.prefuse;

import com.hp.hpl.guess.ui.EdgeListener;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.data.Graph;

public class PrefuseEdge implements EdgeListener {

    protected prefuse.data.Edge iEdge = null;

    public PrefuseEdge(Graph g, PrefuseNode n1, PrefuseNode n2) {
//	iEdge = g.addEdge(n1.iNode,n2.iNode);
    }

    public Object get(String field) {
	//return(getAttribute(field));
	return(null);
    }
    
    public void set(String field, Object value) {
	//setAttribute(field,value.toString());
    }

    public void highlight(boolean state) {
    }

    public void readjust() {
    }

    public void readjust(boolean moveToF) {
    }
}
