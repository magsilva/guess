package com.hp.hpl.guess.prefuse;

import com.hp.hpl.guess.ui.*;
import com.hp.hpl.guess.*;
import java.awt.Color;
import prefuse.data.Table;
import prefuse.data.Schema;

public class PrefuseFactory extends VisFactory {

    private PrefuseDisplay curFrame = null;
    
    protected Table m_edges;
    protected Table m_nodes;
    protected Schema m_nsch = new Schema();
    protected Schema m_esch = new Schema();

    protected prefuse.data.Graph m_graph = null;
    
    protected static final String ID         = "id";
    protected static final String SRC = prefuse.data.Graph.DEFAULT_SOURCE_KEY;
    protected static final String TRG = prefuse.data.Graph.DEFAULT_TARGET_KEY;
    protected static final String SRCID = SRC+'_'+ID;
    protected static final String TRGID = TRG+'_'+ID;

    public PrefuseFactory() {
	init();
    }

    private PrefuseDisplay init() {
	m_esch.addColumn(SRC, int.class);
	m_esch.addColumn(TRG, int.class);
	m_esch.addColumn(SRCID, String.class);
	m_esch.addColumn(TRGID, String.class);
	m_nsch.lockSchema();
	m_esch.lockSchema();
	m_nodes = m_nsch.instantiate();
	m_edges = m_esch.instantiate();
	m_graph = new prefuse.data.Graph(m_nodes,m_edges,false);
	curFrame = new PrefuseDisplay(m_graph);
	return(curFrame);
    }

    public FrameListener getDisplay() {
	return(curFrame);
    }

    public void runNow() {
	//curFrame.runNow();
    }

    public NodeListener generateNode(int type, double x, double y,
				     double width, double height,
				     Color clr, Node n) {
	return(generateNode(n));
    }

    public NodeListener generateNode(Node n) {
	//m_actionList.setEnabled(false);	
	PrefuseNode pn = new PrefuseNode(m_graph);
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
