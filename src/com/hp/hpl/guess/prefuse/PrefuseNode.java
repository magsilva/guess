package com.hp.hpl.guess.prefuse;

import edu.berkeley.guir.prefuse.graph.DefaultNode;
import com.hp.hpl.guess.ui.NodeListener;

public class PrefuseNode extends DefaultNode implements NodeListener {
    
    public PrefuseNode() {
	super();
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
    
    public void setLocation(double x, double y) {
    }
    
    public void setLocation(double x, double y, double width, double height) {
    }
}
