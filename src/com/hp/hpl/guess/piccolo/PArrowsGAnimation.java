package com.hp.hpl.guess.piccolo;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.guess.animation.GEdgeAnimation;
import com.hp.hpl.guess.ui.VisFactory;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;


public class PArrowsGAnimation extends GEdgeAnimation {

	/**
	 * piccolos canvas where the elements are painted
	 */
	private PCanvas canvas = (PCanvas) VisFactory.getFactory().getDisplay();
	

	/**
	 * unit vectors for the graphical representation of the edge
	 */
	private double unitVectorX = 0;
	private double unitVectorY = 0;
	
	/**
	 * some visual attributes for the arrows
	 */
	private double arrowLength = 6;
	private double arrowWidth = 4;
	private double arrowSpace = 5;

	/**
	 * list of all activities (one for each arrow) 
	 */
	private Set<PActivity> activitySet = new HashSet<PActivity>();
	
	/**
	 * List of the graphical representation of the nodes
	 */
	private Set<PNode> arrowList = new HashSet<PNode>();


	/**
	 * create the animation
	 * @param loopCount
	 * @return
	 */
	private void createActivities(int loopCount) {
		arrowSpace = getEdge().getNode1().getWidth() / 2;
		
		double x1 = getEdge().getNode1().getX() + (getEdge().getNode1().getWidth()/2);
		double x2 = getEdge().getNode2().getX() + (getEdge().getNode2().getWidth()/2);
		double y1 = getEdge().getNode1().getY() + (getEdge().getNode1().getHeight()/2);
		double y2 = getEdge().getNode2().getY() + (getEdge().getNode2().getHeight()/2);
	
		// get length of edge
		double edgeLength = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1-y2));
		
		unitVectorX = ((x1-x2)/edgeLength);
		unitVectorY = ((y1-y2)/edgeLength);
		
		final List<PBounds> oldBounds = new ArrayList<PBounds>();
		int i = 0;
		
		// Place the arrows behind each other
		double curY = 4*arrowSpace;
		while ((curY + 2*arrowSpace) < edgeLength) {
			
			AffineTransform at = new AffineTransform();
			double thetaRadians = Math.atan2(( y1 - y2), (x1 - x2)) + Math.PI;
			
			// Set position
			at.translate(x2 - (-1)*curY*unitVectorX, y2 - (-1)*curY*unitVectorY);
			at.rotate(thetaRadians);
			
			// create a arrow prototype
			Shape arrowShape = at.createTransformedShape(Arrow.getSleekArrow((int)arrowWidth, (int)arrowLength));
			final PPath arrow = new PPath(arrowShape);
			
			arrowList.add(arrow);
			
			// set paint and stroke
			arrow.setPaint(Color.RED);
			arrow.setStroke(null);
			
			// Add to canvas
			canvas.getLayer().addChild(arrow);

			curY += arrowLength + arrowSpace;
			
			oldBounds.add(i, arrow.getBounds());
			
			// Create new activity
			PInterpolatingActivity arrowActivity = arrow.animateToBounds(arrow.getX() - (arrowLength + arrowSpace)*unitVectorX, 
					arrow.getY() - (arrowLength + arrowSpace)*unitVectorY, arrow.getWidth(), arrow.getHeight(), 400);

			final int finali = i;
			arrowActivity.setDelegate(new PActivityDelegate() {
				public void activityFinished(PActivity arg0) {
					arrow.setBounds(oldBounds.get(finali));
				}
				public void activityStarted(PActivity arg0) {}
				public void activityStepped(PActivity arg0) {}
			});
			
			arrowActivity.setLoopCount(loopCount);

			// Schedule the activity.
			canvas.getRoot().addActivity(arrowActivity);
			activitySet.add(arrowActivity);
			
			i++;
		}
			
	}

	
	/**
	 * Start the animation
	 */
	public void start(int loops) {
		createActivities(loops);
	}

	/**
	 * Stop the animation
	 */
	public void stop() {

		Iterator<PActivity> actIterator = activitySet.iterator();
		while (actIterator.hasNext()) {
			actIterator.next().terminate();
		}
		
		Iterator<PNode> arrowsIterator = arrowList.iterator();
		while (arrowsIterator.hasNext()) {
			PNode nextNode = arrowsIterator.next();
			if (canvas.getLayer().isAncestorOf(nextNode)) {
				canvas.getLayer().removeChild(nextNode);
			}
		}
	}

}
