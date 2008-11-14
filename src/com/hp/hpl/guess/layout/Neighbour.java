package com.hp.hpl.guess.layout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.guess.Edge;
import com.hp.hpl.guess.GraphElement;
import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.Node;

import com.hp.hpl.guess.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.AbstractLayout;

public class Neighbour extends AbstractLayout implements SubGraphLayout {

	private Map<GraphElement, Integer> elementLevel = new HashMap<GraphElement, Integer>();
	private int maxDept = 1;
	private Node startNode;
	private Graph workingGraph;
	
	public Neighbour(Graph g, Node center, int dept) {
		super(g);
		elementLevel.put(center, 0);
		maxDept = dept;
		startNode = center;
		workingGraph = g;
	}

	public boolean incrementsAreDone() {
		return false;
	}

	public boolean isIncremental() {
		return false;
	}
	
	/**
	 * Runs recursivly, as a result we get the groups map.
	 * @param startNode
	 * @param deptLevel
	 */
	private void getNeighbours(Node startNode, int deptLevel) {	
		Set<Node> nextNodes = new HashSet<Node>();
		Iterator<GraphElement> directNeighbours = startNode.getNeighbors().iterator();
		while (directNeighbours.hasNext()) {
			GraphElement next = directNeighbours.next();
			if (!elementLevel.containsKey(next)) {
				elementLevel.put(next, deptLevel);
				if (next instanceof Node) {
					nextNodes.add((Node)next);
				}
			}
		}
		
		Iterator<GraphElement> directEdges = startNode.getIncidentEdges().iterator();
		while (directEdges.hasNext()) {
			GraphElement next = directEdges.next();
			if (!elementLevel.containsKey(next)) {
				Node node1 = ((Edge)next).getNode1();
				Node node2 = ((Edge)next).getNode2();
				// Do not add edges between nodes without the startNode
				if ((!node1.equals(startNode) && elementLevel.get(node1)!=elementLevel.get(startNode)) ||
						(!node2.equals(startNode) && elementLevel.get(node2)!=elementLevel.get(startNode))) {
					elementLevel.put(next, deptLevel);
				}
			}
		}
		
		// Get the further neighbours
		if (deptLevel==maxDept) {
			return;
		} else {
			Iterator<Node> nodeIterator = nextNodes.iterator();
			while (nodeIterator.hasNext()) {
				getNeighbours(nodeIterator.next(), deptLevel + 1);
			}
		}
	}

	private void colorizeGraphElements(Graph tree) {
		Iterator<GraphElement> nodeIterator = tree.getNodes().iterator();
		while(nodeIterator.hasNext()) {
			GraphElement next = nodeIterator.next();
			if (!elementLevel.containsKey(next)) {
				next.hide();
			} else {
				next.show();
				float level = 1.0f;
				if ((elementLevel.get(next)!=0) && (elementLevel.get(next)>1)) {
					level = 1.0f / (elementLevel.get(next)*2);
				}
				next.getRep().set("opacity", level);
			}
		}
		
		Iterator<GraphElement> edgeIterator = Guess.getGraph().getEdges().iterator();
		while(edgeIterator.hasNext()) {
			GraphElement next = edgeIterator.next();
			if (elementLevel.containsKey(next)) {
				next.show();
				float level = 1.0f;
				if (elementLevel.get(next)>1) {
					level = 1.0f / (elementLevel.get(next)*2);
				}
				next.getRep().set("opacity", level);
			} else {
				next.hide();
			}
		}	
	}

	/**
	 * Start layout
	 */
	public void advancePositions() {
		getNeighbours(startNode, 1);
		colorizeGraphElements(workingGraph);
	}
	
	/**
	 * Do not change x position of node
	 * @param node
	 * @return
	 */
	public double getX(Vertex node) {
		return ((Double)((Node)node).__getattr__("x")).doubleValue();
	}
	
	/**
	 * Do not change y position of node
	 * @param node
	 * @return
	 */
	public double getY(Vertex node) {
		return ((Double)((Node)node).__getattr__("y")).doubleValue();
	}

	/**
	 * unused
	 */
	protected void initialize_local_vertex(Vertex arg0) {}

}
