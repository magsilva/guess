package com.hp.hpl.guess.prefuse;

import com.hp.hpl.guess.ui.*;
import com.hp.hpl.guess.*;
import java.awt.Color;
import prefuse.data.Table;
import prefuse.data.Schema;
import prefuse.visual.VisualItem;

public class PrefuseFactory extends VisFactory {

    private PrefuseDisplay curFrame = null;
    
    protected prefuse.data.Graph m_graph = null;
    
    public PrefuseFactory() {
	init();
    }

    private PrefuseDisplay init() {
	m_graph = new prefuse.data.Graph(false);
	prefuse.data.Node n1 = m_graph.addNode();
	m_graph.getNodeTable().addColumn("label",String.class);
	//m_graph.getNodeTable().addColumn(VisualItem.FILLED,int.class);
	curFrame = new PrefuseDisplay(m_graph);
	m_graph.removeNode(n1);
	return(curFrame);
    }

    public FrameListener getDisplay() {
	return(curFrame);
    }

    public void runNow() {
	curFrame.runNow();
	System.out.println(m_graph.getNodeTable());
    }

    public NodeListener generateNode(int type, double x, double y,
				     double width, double height,
				     Color clr, Node n) {
	return(generateNode(n));
    }

    public NodeListener generateNode(Node n) {
	//m_actionList.setEnabled(false);	
	PrefuseNode pn = new PrefuseNode(m_graph);
	pn.set("label",n.getName());
	//System.out.println(n.getName());
	//pn.setAttribute("label",n.getName());
	return(pn);
    }

    public void remove(Node n)
    {
	//curFrame.g.removeNode((PrefuseNode)n.getRep());
    }
    
    //removes the given edge rep from the collection of edges to draw
    public void remove(Edge e)
    {
	//curFrame.g.removeEdge((PrefuseEdge)e.getRep());
    }

    public void add(Node n) {
    }

    public void add(Edge e) {
    }

    public EdgeListener generateEdge(Edge n) {
	//m_actionList.setEnabled(false);	
	PrefuseEdge pe = new PrefuseEdge(m_graph,
					 (PrefuseNode)n.getNode1().getRep(),
					 (PrefuseNode)n.getNode2().getRep());
	//pe.setAttribute("WEIGHT",n.__getattr__("weight").toString());
	//m_actionList.setEnabled(true);	
	return(pe);
    }
        
}
