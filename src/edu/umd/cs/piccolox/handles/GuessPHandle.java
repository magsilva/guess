package edu.umd.cs.piccolox.handles;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.piccolo.util.PFixedWidthStroke;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.util.PBoundsLocator;

public class GuessPHandle extends PBoundsHandle {
    
	private static final long serialVersionUID = -1953027248957788148L;

	public static void addBoundsHandlesTo(PNode aNode) {
	aNode.addChild(new GuessPHandle(PBoundsLocator.createEastLocator(aNode))); 
	aNode.addChild(new GuessPHandle(PBoundsLocator.createWestLocator(aNode))); 
	aNode.addChild(new GuessPHandle(PBoundsLocator.createNorthLocator(aNode))); 
	aNode.addChild(new GuessPHandle(PBoundsLocator.createSouthLocator(aNode)));
	aNode.addChild(new GuessPHandle(PBoundsLocator.createNorthEastLocator(aNode))); 
	aNode.addChild(new GuessPHandle(PBoundsLocator.createNorthWestLocator(aNode))); 
	aNode.addChild(new GuessPHandle(PBoundsLocator.createSouthEastLocator(aNode))); 
	aNode.addChild(new GuessPHandle(PBoundsLocator.createSouthWestLocator(aNode))); 	
    }
    
    public static void addStickyBoundsHandlesTo(PNode aNode, PCamera camera) {
	camera.addChild(new GuessPHandle(PBoundsLocator.createEastLocator(aNode)));
	camera.addChild(new GuessPHandle(PBoundsLocator.createWestLocator(aNode)));
	camera.addChild(new GuessPHandle(PBoundsLocator.createNorthLocator(aNode)));
	camera.addChild(new GuessPHandle(PBoundsLocator.createSouthLocator(aNode)));
	camera.addChild(new GuessPHandle(PBoundsLocator.createNorthEastLocator(aNode)));
	camera.addChild(new GuessPHandle(PBoundsLocator.createNorthWestLocator(aNode)));
	camera.addChild(new GuessPHandle(PBoundsLocator.createSouthEastLocator(aNode)));
	camera.addChild(new GuessPHandle(PBoundsLocator.createSouthWestLocator(aNode)));
    }

    private static boolean resizeable = true;

    public static void setResizeable(boolean state) {
	resizeable = state;
    }
						    
    public GuessPHandle(PBoundsLocator aLocator) {
		super(aLocator);
	    if (Guess.getZooming() == Guess.ZOOMING_SPACE) {
	    	setStroke(new PFixedWidthStroke(0.5f));
	    } else {
	    	setStroke(new BasicStroke(0.5f));
	    }
    }
    
    public void startHandleDrag(Point2D aLocalPoint, PInputEvent aEvent) {
	if (resizeable)
	    super.startHandleDrag(aLocalPoint,aEvent);
    }
    
    public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent) {
	if (resizeable)
	    super.dragHandle(aLocalDimension,aEvent);
    }
	
    public void endHandleDrag(Point2D aLocalPoint, PInputEvent aEvent) {
	if (resizeable)
	    super.endHandleDrag(aLocalPoint,aEvent);
    }	

}
