package com.hp.hpl.guess.piccolo;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import javax.naming.directory.InvalidAttributeIdentifierException;

import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.animation.GNodeAnimation;
import com.hp.hpl.guess.ui.VisFactory;

import edu.umd.cs.piccolo.PCanvas;

import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.nodes.PPath;


public class PRingGAnimation extends GNodeAnimation {

	/**
	 * piccolos canvas where the elements are painted 
	 */
	private PCanvas canvas = (PCanvas) VisFactory.getFactory().getDisplay();
	
	/**
	 * the animation objects
	 */
	private PActivity animationSizeActivity = null;
	private PActivity animationOpacityActivity = null;
	
	/**
	 * the graphical representation of the ring
	 */
	private PPath ring = null;
	
	/**
	 * the size of the ring
	 */
	private double size = 10;
	
	/**
	 * the time a step needs to complete in ms
	 */
	private int stepDuration = 1000;
	
	/**
	 * attribute for the direction of the ring. 
	 * possible values are "in" and "out".
	 */
	private String direction = "out";
	private final String ANIMATION_DIRECTION_OUT = "out";
	private final String ANIMATION_DIRECTION_IN = "in";
	
	/**
	 * Constructor for ring animation. set the 
	 * avaible attributes. 
	 */
	public PRingGAnimation() {
		super.addAvailableAttributes("direction", "out");
	}
	
	/**
	 * if the node is set create the graphical 
	 * representation of the ring
	 */
	public void setNode(Node node) {
		super.setNode(node);
		createRing();
	}

	/**
	 * create and (re)set the graphical
	 * representation of the ring
	 */
	private void createRing() {
		
		try {
			direction = super.getAttribute("direction");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InvalidAttributeIdentifierException e) {
			e.printStackTrace();
		}
		
		double x = getNode().getX();
		double y = getNode().getY();
		double width = getNode().getWidth();
		double height = getNode().getHeight();
		
		// get the max size we need for the ring
		size = Math.abs(canvas.getRoot().getX() - x);
		if (Math.abs(canvas.getRoot().getY() - y)>size) {
			size = Math.abs(canvas.getRoot().getY() - y);
		}
		if (Math.abs((canvas.getRoot().getX() + canvas.getRoot().getWidth()) - x)>size) {
			size = Math.abs((canvas.getRoot().getX() + canvas.getRoot().getWidth()) - x);
		}		
		if (Math.abs((canvas.getRoot().getY() + canvas.getRoot().getHeight()) - y)>size) {
			size = Math.abs((canvas.getRoot().getY() + canvas.getRoot().getHeight()) - y);
		}
		if (ring==null) {
			if (direction==ANIMATION_DIRECTION_OUT) {
				ring = new PPath(new Ellipse2D.Double(x,y,width,height));
			} else if (direction==ANIMATION_DIRECTION_IN) {
				ring = new PPath(new Ellipse2D.Double(getNode().getX()-size, 
						getNode().getY()-size, getNode().getWidth()+size*2, getNode().getHeight()+size*2));
			}
		} else {
			if (direction==ANIMATION_DIRECTION_OUT) {
				ring.setBounds(x,y,width,height);
			} else if (direction==ANIMATION_DIRECTION_IN) {
				ring.setBounds(ring.getX()-size, 
						ring.getY()-size, ring.getWidth()+size*2, ring.getHeight()+size*2);
			}
		}
		
		ring.setPaint(null);
		ring.setStroke(((PPath)getNode().getRep()).getStroke());
		ring.setStrokePaint(Color.WHITE);
		if (direction==ANIMATION_DIRECTION_OUT) {
			ring.setTransparency(1);
		} else if (direction==ANIMATION_DIRECTION_IN) {
			ring.setTransparency(0);
		}
		
		canvas.getLayer().addChild(ring);
		canvas.getRoot().invalidatePaint();
	}
	
	/**
	 * create the animation for the fading out effect
	 * @param loopCount
	 * @return
	 */
	private PActivity createOpacityActivity(int loopCount) {
		float opacity = 0;
		if (direction==ANIMATION_DIRECTION_OUT) {
			opacity = 0;
		} else if (direction==ANIMATION_DIRECTION_IN) {
			opacity = 1;
		}
		
		PInterpolatingActivity opacityActivity = ring.animateToTransparency(opacity, stepDuration);
		opacityActivity.setMode(PInterpolatingActivity.SOURCE_TO_DESTINATION);
		opacityActivity.setSlowInSlowOut(true);
		opacityActivity.setLoopCount(loopCount);
		opacityActivity.setDelegate(new PActivityDelegate() {
			public void activityFinished(PActivity arg0) {
				createRing();
			}
			public void activityStarted(PActivity arg0) {}
			public void activityStepped(PActivity arg0) {}
		});
		
		return opacityActivity;
	}
	
	/**
	 * create the animation for the ring to become bigger
	 * @param loopCount
	 * @return
	 */
	private PActivity createSizeAcitvity(int loopCount) {
		PInterpolatingActivity sizeActivity = null;
		
		if (direction==ANIMATION_DIRECTION_OUT) {
			sizeActivity = ring.animateToBounds(ring.getX()-size, 
				ring.getY()-size, ring.getWidth()+size*2, ring.getHeight()+size*2, stepDuration);
		} else if (direction==ANIMATION_DIRECTION_IN) {
			sizeActivity = ring.animateToBounds(getNode().getX(), 
					getNode().getY(), getNode().getWidth(), getNode().getHeight(), stepDuration);
		}
		
		sizeActivity.setMode(PInterpolatingActivity.SOURCE_TO_DESTINATION);
		sizeActivity.setSlowInSlowOut(true);
		sizeActivity.setLoopCount(loopCount);
	
		sizeActivity.setDelegate(new PActivityDelegate() {
			public void activityFinished(PActivity arg0) {
				createRing();
			}
			public void activityStarted(PActivity arg0) {}
			public void activityStepped(PActivity arg0) {}
		});
		
		return sizeActivity;
	}

	/**
	 * Start the animation
	 */
	public void start(int loops) {

		if (animationSizeActivity==null) {
			animationSizeActivity = createSizeAcitvity(loops);
		}
		// Schedule the activity.
		canvas.getRoot().addActivity(animationSizeActivity);
		

		if (animationOpacityActivity==null) {
			animationOpacityActivity = createOpacityActivity(loops);
		}
		// Schedule the activity.
		canvas.getRoot().addActivity(animationOpacityActivity);
	}

	/**
	 * Stop the animation
	 */
	public void stop() {
		if (animationSizeActivity!=null) {
			animationSizeActivity.terminate();
			animationSizeActivity = null;
		}
		if (animationOpacityActivity!=null) {
			animationOpacityActivity.terminate();
			animationOpacityActivity = null;
		}
		removeRing();
	}
	
	/**
	 * Remove the ring object from the canvas
	 */
	private void removeRing() {
		if (canvas.getLayer().isAncestorOf(ring)) {
			canvas.getLayer().removeChild(ring);
		}
	}

}

