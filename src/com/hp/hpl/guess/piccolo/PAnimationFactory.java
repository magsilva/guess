package com.hp.hpl.guess.piccolo;

import javax.naming.directory.InvalidAttributeIdentifierException;

import com.hp.hpl.guess.Edge;
import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.animation.AnimationFactory;
import com.hp.hpl.guess.animation.GEdgeAnimation;
import com.hp.hpl.guess.animation.GNodeAnimation;

public class PAnimationFactory extends AnimationFactory {

	
	public PAnimationFactory() {
		
		// Register simple node animation
		registerNodeAnimation("pulse", new PPulseGAnimation());
		
		// Register node animation and set some parameters
		try {
			GNodeAnimation ringOut = new PRingGAnimation();
			ringOut.setAttribute("direction", "out");
			registerNodeAnimation("ring-out", ringOut);
		} catch (InvalidAttributeIdentifierException e) {
			e.printStackTrace();
		}
		
		try {
			GNodeAnimation ringIn = new PRingGAnimation();
			ringIn.setAttribute("direction", "in");
			registerNodeAnimation("ring-in", ringIn);
		} catch (InvalidAttributeIdentifierException e) {
			e.printStackTrace();
		}		

		// Register simple edge animation
		registerEdgeAnimation("arrows", new PArrowsGAnimation());
		
		
	}
	
	
	public GEdgeAnimation generateEdgeAnimation(String animationName, Edge edge) {
		GEdgeAnimation anim = null;
		try {
			anim = (GEdgeAnimation) getEdgeAnimation(animationName).clone();
			anim.setEdge(edge);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return anim;
	}

	public GNodeAnimation generateNodeAnimation(String animationName, Node node) {
		GNodeAnimation anim = null;
		try {
			anim = (GNodeAnimation) getNodeAnimation(animationName).clone();
			anim.setNode(node);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return anim;
	}


}
