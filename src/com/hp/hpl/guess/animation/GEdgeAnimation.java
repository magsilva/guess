package com.hp.hpl.guess.animation;

import com.hp.hpl.guess.Edge;

public abstract class GEdgeAnimation extends GAnimation {
	private Edge edge = null;
	
	/**
	 * Set the edge for the animation
	 */
	public void setEdge(Edge aEdge) {
		edge = aEdge;
	}
	
	/**
	 * Return the edge for the animation
	 * @return
	 */
	public Edge getEdge() {
		return edge;
	}
}
