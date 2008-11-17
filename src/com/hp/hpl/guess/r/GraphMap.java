package com.hp.hpl.guess.r;

import com.hp.hpl.guess.*;
import java.util.*;

public class GraphMap extends HashMap<Node, Integer> {

	private static final long serialVersionUID = -3825463270363145102L;

	private double[] conn = null;

    private Node[] graphMap = null;

    public double[] getConn() {
	return(conn);
    }

    public Node getNode(int index) {
	return(graphMap[index]);
    }

    public int getNodeCount() {
	return(graphMap.length);
    }

    public GraphMap(Graph g) {

	// keep a map of index -> node
	Set<Node> nodes = g.getNodes();
	graphMap = new Node[nodes.size()];
    	nodes.toArray(graphMap);
	
	// keep node -> index
	clear();
	for (int i = 0 ; i < graphMap.length ; i++) {
	    put(graphMap[i],new Integer(i));
	}
	
	// next step, make connectivity graph
	conn = new double[nodes.size() * nodes.size()];
	
	Iterator<Edge> it = g.getEdges().iterator();
	Node n1,n2;

	while(it.hasNext()) {
	    Edge e = (Edge)it.next();
	    if (e instanceof UndirectedEdge) {
		n1 = (Node)e.getNode1();
		n2 = (Node)e.getNode2();
		int i = ((Integer)get(n1)).intValue();
		int j = ((Integer)get(n2)).intValue();
		int loc = nodes.size() * i + j;
		conn[loc] = 1;
		loc = nodes.size() * j + i;
		conn[loc] = 1;
	    } else if (e instanceof DirectedEdge) {
		n1 = (Node)((DirectedEdge)e).getSource();
		n2 = (Node)((DirectedEdge)e).getDest();
		int i = ((Integer)get(n1)).intValue();
		int j = ((Integer)get(n2)).intValue();
		int loc = nodes.size() * i + j;
		conn[loc] = 1;
	    }
	}
    }
}
