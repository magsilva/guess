package edu.umd.cs.piccolo.event;

import java.awt.geom.Point2D;

import com.hp.hpl.guess.piccolo.CursorFactory;
import com.hp.hpl.guess.piccolo.GFrame;
import com.hp.hpl.guess.piccolo.GuessPEdge;
import com.hp.hpl.guess.piccolo.GuessPNode;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPickPath;

public class MyPanHandler extends PPanEventHandler {
 
    public MyPanHandler(GFrame owner) {
    }

    private static Class<?> nodeClass = null;
    private static Class<?> edgeClass = null;
    private static Class<?> serClass = null;
    static {
	try {
	    nodeClass = 
		Class.forName("com.hp.hpl.guess.piccolo.GuessPNode");
	    edgeClass = 
		Class.forName("com.hp.hpl.guess.piccolo.GuessPEdge");
	    serClass = 
		Class.forName("com.hp.hpl.guess.piccolo.util.SerInterface");
	} catch (Exception e) {e.printStackTrace();}
    }


    private boolean isNodeOrEdge(PInputEvent e) {
	PPickPath pp = e.getPath();
	PNode t = pp.getPickedNode();
	if (nodeClass.isAssignableFrom(t.getClass())) {
	    return(true);
	}
	if (edgeClass.isAssignableFrom(t.getClass())) {
	    return(true);
	}
	if (serClass.isAssignableFrom(t.getClass())) {
	    return(true);
	}
	return(false);
    }

    protected void drag(PInputEvent e) {
	if (!e.isShiftDown()) {
    
	    if (isNodeOrEdge(e))
		return;
	    
	    super.drag(e);
	}
    }

    protected void pan(PInputEvent e) {
	if (!e.isShiftDown()) {

	    if (isNodeOrEdge(e))
		return;

	    super.pan(e);
	}
    }

    protected void dragActivityStep(PInputEvent e) {
	if (!e.isShiftDown()) {

	    if (isNodeOrEdge(e))
		return;

	    super.dragActivityStep(e);
	}
    }

    public void mousePressed(PInputEvent e) {
	if (!e.isShiftDown()) {

	    if (isNodeOrEdge(e))
		return;

	    e.pushCursor(CursorFactory.getCursor(CursorFactory.MOVE));
	    super.mousePressed(e);
	}
    }

    public void mouseReleased(PInputEvent e) {
	if (isNodeOrEdge(e))
	    return;

	super.mouseReleased(e);
	try {
	    e.popCursor();
	} catch (Exception ex) {
	    e.pushCursor(CursorFactory.getCursor(CursorFactory.STANDARD));
	}
    }

    public void mouseEntered(PInputEvent e) {
	PPickPath pp = e.getPath();
	PNode t = pp.getPickedNode();
	if (nodeClass.isAssignableFrom(t.getClass())) {
	    ((GuessPNode)t).mouseEntered(e);
	}
	if (edgeClass.isAssignableFrom(t.getClass())) {
	    ((GuessPEdge)t).mouseEntered(e);
	}
	super.mouseEntered(e);
    }

    public void mouseExited(PInputEvent e) {
	PPickPath pp = e.getPath();
	PNode t = pp.getPickedNode();
	if (nodeClass.isAssignableFrom(t.getClass())) {
	    ((GuessPNode)t).mouseExited(e);
	}
	if (edgeClass.isAssignableFrom(t.getClass())) {
	    ((GuessPEdge)t).mouseExited(e);
	}
	super.mouseExited(e);
    }

    private Point2D lastClicked = null;
    
    public Point2D getLastClickedPosition() {
	return(lastClicked);
    }

    public void mouseClicked(PInputEvent e) {
	lastClicked = e.getPosition();
	PPickPath pp = e.getPath();
	PNode t = pp.getPickedNode();
	if (nodeClass.isAssignableFrom(t.getClass())) {
	    ((GuessPNode)t).mouseClicked(e);
	}
	if (edgeClass.isAssignableFrom(t.getClass())) {
	    ((GuessPEdge)t).mouseClicked(e);
	}
	super.mouseClicked(e);
    }
}
