package edu.umd.cs.piccolo.event;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.hp.hpl.guess.GraphElement;
import com.hp.hpl.guess.piccolo.CursorFactory;
import com.hp.hpl.guess.piccolo.GFrame;
import com.hp.hpl.guess.piccolo.GuessPEdge;
import com.hp.hpl.guess.piccolo.GuessPNode;
import com.hp.hpl.guess.ui.EdgeEditorPopup;
import com.hp.hpl.guess.ui.GraphElementEditorPopup;
import com.hp.hpl.guess.ui.NodeEditorPopup;
import com.hp.hpl.guess.ui.VisFactory;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPickPath;

public class MyZoomHandler extends PZoomEventHandler {
 
    private GFrame owner = null;

    public MyZoomHandler(GFrame owner) {
	this.owner = owner;
    }

    private boolean isNodeOrEdge(PInputEvent e) {
	PPickPath pp = e.getPath();
	PNode t = pp.getPickedNode();
	if (t instanceof GuessPNode) {
	    return(true);
	}
	if (t instanceof GuessPEdge) {
	    return(true);
	}
	return(false);
    }

    public void mousePressed(PInputEvent e) {
	if (e.isRightMouseButton()) { 

	    if (isNodeOrEdge(e))
		return;

	    e.pushCursor(CursorFactory.getCursor(CursorFactory.ZOOM));
	    super.mousePressed(e);
	}
    }
    
	/**
	 * Mousewheel is rotated
	 * @param event
	 */
	public void mouseWheelRotated(PInputEvent event) {
		double currentScale = owner.getGCamera().getViewScale();
		double scaleDelta = 1.0f - (0.17f * event.getWheelRotation());
		double newScale = currentScale * scaleDelta;
		
		// Do not zoom further if at minimum zoom level
		if (newScale < getMinScale()) {
			owner.getGCamera().setViewScale(getMinScale());
			return;
		}
		// Do not zoom further if at maximum zoom level
		if ((getMaxScale() > 0) && (newScale > getMaxScale())) {
			owner.getGCamera().setViewScale(getMaxScale());
			return;
		}
		
		// Scale camera
		Point2D pos = event.getPosition();
		owner.getGCamera().scaleViewAboutPoint(scaleDelta, pos.getX(), pos.getY());
	}

    public void mouseReleased(PInputEvent e) {
	super.mouseReleased(e);
	try {

	    if (isNodeOrEdge(e))
		return;

	    e.popCursor();
	} catch (Exception ex) {
	    e.pushCursor(CursorFactory.getCursor(CursorFactory.STANDARD));
	}
	//CursorFactory.setCursor(CursorFactory.STANDARD);
    }

    private Point2D lastClicked = null;
    
    public Point2D getLastClickedPosition() {
	return(lastClicked);
    }

    public void mouseClicked(PInputEvent e) {
	if (e.isRightMouseButton()) {
	    lastClicked = e.getPosition();
	    PPickPath pp = e.getPath();
	    PNode t = pp.getPickedNode();
	    GFrame frame = (GFrame)VisFactory.getFactory().getDisplay();
	    
	    Collection<PNode> cNodes = frame.getSelected();
	    
	    Collection<GraphElement> c = null;
	    if (cNodes.size()>0) {
	    	c = new HashSet<GraphElement>();
	    	Iterator<PNode> it1 = cNodes.iterator();
	    	while (it1.hasNext()) {
	    		PNode nextNode = it1.next();
	    		c.add(((GuessPNode)nextNode).getOwner());
	    	}
	    }
	     
	    if ((c == null) || (c.size() <= 0)) {
		if (t instanceof GuessPNode) {
		    ((GuessPNode)t).mouseClicked(e);
		    c = new HashSet<GraphElement>();
		    c.add(((GuessPNode)t).getOwner());
		    Point2D p2d = 
			e.getPositionRelativeTo(frame.getCamera());
		    NodeEditorPopup.getPopup().show((Component)frame,
						    (int)p2d.getX(),
						    (int)p2d.getY(),
						    c,((GuessPNode)t).getOwner());
		} else if (t instanceof GuessPEdge) {
		    ((GuessPEdge)t).mouseClicked(e);
		    c = new HashSet<GraphElement>();
		    c.add(((GuessPEdge)t).getOwner());
		    Point2D p2d = 
			e.getPositionRelativeTo(frame.getCamera());
		    EdgeEditorPopup.getPopup().show((Component)frame,
						    (int)p2d.getX(),
						    (int)p2d.getY(),
						    c,((GuessPEdge)t).getOwner());
		}
	    } else {
		boolean nodes = false;
		boolean edges = false;

		if (c.size() <= 0) {
		    return;
		} else {
		    HashSet<GraphElement> newhs = new HashSet<GraphElement>();
		    Iterator<GraphElement> it = c.iterator();
		    while(it.hasNext()) {
			Object o = it.next();
			if (o instanceof GuessPEdge) {
			    newhs.add(((GuessPEdge)o).getOwner());
			    edges = true;
			} else if (o instanceof GuessPNode) {
			    newhs.add(((GuessPNode)o).getOwner());
			    nodes = true;
			}
		    }
		    c = newhs;
		}
		Point2D p2d = 
		    e.getPositionRelativeTo(frame.getCamera());
		if (nodes && edges) {
		    GraphElementEditorPopup.getPopup().show((Component)frame,
							    (int)p2d.getX(),
							    (int)p2d.getY(),
							    c,c);
		} else if (nodes) {
		    NodeEditorPopup.getPopup().show((Component)frame,
						    (int)p2d.getX(),
						    (int)p2d.getY(),
						    c,c);
		} else if (edges) {
		    EdgeEditorPopup.getPopup().show((Component)frame,
						    (int)p2d.getX(),
						    (int)p2d.getY(),
						    c,c);
		}
	    }
	    super.mouseClicked(e);
	}
    }
}

