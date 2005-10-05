package com.hp.hpl.guess.prefuse;

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import com.hp.hpl.guess.ui.EdgeListener;

public class PrefuseEdge extends DefaultEdge implements EdgeListener {

    public PrefuseEdge(PrefuseNode n1, PrefuseNode n2) {
	super((edu.berkeley.guir.prefuse.graph.Node)n1,
	      (edu.berkeley.guir.prefuse.graph.Node)n2);
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
