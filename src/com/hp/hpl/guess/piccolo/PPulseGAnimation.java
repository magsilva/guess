package com.hp.hpl.guess.piccolo;

import javax.naming.directory.InvalidAttributeIdentifierException;

import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.animation.GNodeAnimation;
import com.hp.hpl.guess.ui.VisFactory;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;


public class PPulseGAnimation extends GNodeAnimation {

	/**
	 * The graphical representation of the node
	 */
	private PNode nodeRep = null;
	
	/**
	 * piccolos canvas where the elements are painted
	 */
	private PCanvas canvas = (PCanvas) VisFactory.getFactory().getDisplay();
	
	/**
	 * the piccolo animation object
	 */
	private PActivity animationActivity = null;
	
	/**
	 * Constructor, set the size attribute to 10
	 */
	public PPulseGAnimation() {
		super.addAvailableAttributes("size", "10");
	}
	
	/**
	 * if the current node is set, init the 
	 * graphical representation.
	 */
	public void setNode(Node node) {
		super.setNode(node);
		nodeRep = (PNode) node.getRep();
	}

	/**
	 * create the size animation
	 * @param loopCount
	 * @return
	 */
	private PActivity createAcitvity(int loopCount) {
		int size = 10;
		try {
			size = Integer.valueOf(super.getAttribute("size"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InvalidAttributeIdentifierException e) {
			e.printStackTrace();
		}
		
		PInterpolatingActivity pulseActivity = nodeRep.animateToBounds(nodeRep.getX()-(size/2), 
				nodeRep.getY()-(size/2), nodeRep.getWidth()+size, nodeRep.getHeight()+size, 1000);
		
		pulseActivity.setMode(PInterpolatingActivity.SOURCE_TO_DESTINATION_TO_SOURCE);
		pulseActivity.setSlowInSlowOut(true);
		pulseActivity.setLoopCount(loopCount);
	
		return pulseActivity;
	}
	
	/**
	 * Start the animation
	 */
	public void start(int loops) {
		if (animationActivity==null) {
			animationActivity = createAcitvity(loops);
		}
		// Schedule the activity.
		canvas.getRoot().addActivity(animationActivity);
	}

	/**
	 * Stop the animation
	 */
	public void stop() {
		if (animationActivity!=null) {
			animationActivity.terminate();
			animationActivity = null;
		}
	}

}
