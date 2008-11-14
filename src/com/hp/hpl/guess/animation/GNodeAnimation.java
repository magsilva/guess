package com.hp.hpl.guess.animation;

import com.hp.hpl.guess.Node;

public abstract class GNodeAnimation extends GAnimation {
	private Node node = null;
	
	/**
	 * Set the node for the animation
	 */
	public void setNode(Node aNode) {
		node = aNode;
	}
	
	/**
	 * Return the node for the animation
	 * @return
	 */
	public Node getNode() {
		return node;
	}
}
