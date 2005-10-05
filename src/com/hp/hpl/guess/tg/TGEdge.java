package com.hp.hpl.guess.tg;

import com.hp.hpl.guess.ui.EdgeListener;

public class TGEdge extends com.touchgraph.graphlayout.Edge 
    implements EdgeListener {
    
    public TGEdge(TGNode n1, TGNode n2) {
	super((com.touchgraph.graphlayout.Node)n1,
	      (com.touchgraph.graphlayout.Node)n2);
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
