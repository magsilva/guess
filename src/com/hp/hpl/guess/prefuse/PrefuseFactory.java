package com.hp.hpl.guess.prefuse;

import com.hp.hpl.guess.ui.*;
import com.hp.hpl.guess.*;
import java.awt.Color;

public class PrefuseFactory extends VisFactory {

    private PrefuseDisplay curFrame = null;

    public PrefuseFactory() {
	init();
    }

    private PrefuseDisplay init() {
	curFrame = new PrefuseDisplay();
	return(curFrame);
    }

    public FrameListener getDisplay() {
	return(curFrame);
    }

    public void runNow() {
	curFrame.runNow();
    }

    public NodeListener generateNode(int type, double x, double y,
				     double width, double height,
				     Color clr, Node n) {
	return(generateNode(n));
    }

    public NodeListener generateNode(Node n) {
	//m_actionList.setEnabled(false);	
	PrefuseNode pn = new PrefuseNode();
	//System.out.println(n.getName());
	pn.setAttribute("label",n.getName());
	curFrame.g.addNode(pn);
	//m_actionList.setEnabled(true);	
	return(pn);
    }

    public void remove(Node n)
    {
	curFrame.g.removeNode((PrefuseNode)n.getRep());
    }
    
    //removes the given edge rep from the collection of edges to draw
    public void remove(Edge e)
    {
	curFrame.g.removeEdge((PrefuseEdge)e.getRep());
    }

    public void add(Node n) {
    }

    public void add(Edge e) {
    }

    public EdgeListener generateEdge(Edge n) {
	//m_actionList.setEnabled(false);	
	PrefuseEdge pe = new PrefuseEdge((PrefuseNode)n.getNode1().getRep(),
					 (PrefuseNode)n.getNode2().getRep());
	curFrame.g.addEdge(pe);
	pe.setAttribute("WEIGHT",n.__getattr__("weight").toString());
	//m_actionList.setEnabled(true);	
	return(pe);
    }
        
}
