package com.hp.hpl.guess.piccolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.Iterator;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.ui.Colors;
import com.hp.hpl.guess.ui.ExceptionWindow;
import com.hp.hpl.guess.ui.GraphEvents;
import com.hp.hpl.guess.ui.NodeListener;
import com.hp.hpl.guess.ui.StatusBar;
import com.hp.hpl.guess.ui.VisFactory;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class GuessTextNode extends PText implements GuessPNode {
    
	private static final long serialVersionUID = 7439869860603599643L;

	protected Node owner = null;
    
    protected GFrame frame = null;

    protected int style = VisFactory.TEXT_ROUNDRECTANGLE;
    
    public Node getOwner() {
	return(owner);
    }

    public int getStyle() {
	return(style);
    }

    protected RectangularShape borderShape = null;

    public void setStyle(int s) {

	if ((style == s) && (borderShape != null)) {
	    return;
	}

	if ((s == VisFactory.ROUNDRECTANGLE) ||
	    (s == VisFactory.RECTANGLE) ||
	    (s == VisFactory.ELLIPSE) ||
	    (s == VisFactory.IMAGE) ||
	    (s > 100)) {
	    NodeListener rep = 
		((PFactory)VisFactory.getFactory()).convertNode(this,s,owner);
	    owner.setRep(rep);
	    owner.readjustEdges();
	} else {
	    if (s == VisFactory.TEXT_RECTANGLE) {
		borderShape = 
		    new Rectangle2D.Double(getX()-1,
					   getY()-1,
					   getWidth()+2,
					   getHeight()+2);
	    } else if (s == VisFactory.TEXT_ELLIPSE) {
		borderShape = 
		    new Ellipse2D.Double(getX()-4,
					 getY()-4,
					 getWidth()+8,
					 getHeight()+8);
	    } else {
		borderShape = 
		    new RoundRectangle2D.Double(getX()-4,
						getY()-4,
						getWidth()+8,
						getHeight()+8,
						Math.max(8,(getWidth()+8)/5),
						Math.max(8,(getHeight()+8)/5));
	    }
	}
	style = s;
    }

    public GuessTextNode(Node owner, 
			 double x, 
			 double y, 
			 GFrame frame, 
			 int style) {
	super(owner.getName());
	this.owner = owner;
	this.frame = frame;
	this.setX(x);
	this.setY(y);
	this.style = style;
	if (Guess.getDefaultFont() != null) {
	    Font f = getFont();
	    if (!f.getFamily().equals(Guess.getDefaultFont())) {
		f = new Font(Guess.getDefaultFont(),
			     f.getStyle(),
			     f.getSize());
		setFont(f);
	    }
	}
    }


    public boolean setBounds(java.awt.geom.Rectangle2D newBounds) {
	// notify Node that we're resizing
	boolean toRet = super.setBounds(newBounds);
	owner.readjustEdges();
	notifyHullListeners();
	return(toRet);
    }

    Color curcolor = Color.red;
    Color strokePaint = Color.black;
    
    public void setPaint(Color c) {
	Color oldcolor = (Color)getPaint();
	super.setPaint(c);
	curcolor = c;
	if ((oldcolor != null) && 
	    (oldcolor.getAlpha() != curcolor.getAlpha())) {
	    // change in transparency, we should change
	    // the color of the line we use
	    Color oldSP = strokePaint;
	    strokePaint = Colors.getColor(oldSP.getRed()+","+
					  oldSP.getGreen()+","+
					  oldSP.getBlue()+","+
					  curcolor.getAlpha(),
					  oldSP);
	}

	oldcolor = (Color)getTextPaint();

	double lum = 
	    (299 * c.getRed() + 587 * c.getGreen() + 114 * c.getBlue()) / 1000;
	if (lum < 127) {
	    setTextPaint(Colors.getColor("255,255,255,"+
					 curcolor.getAlpha(),
					 oldcolor));
	} else {
	    setTextPaint(Colors.getColor("0,0,0,"+
					 curcolor.getAlpha(),
					 oldcolor));
	}
    }

    public void setColor(Color c)
    {

	setPaint(c);
    }
    
    public Paint getPaint() {
	return(curcolor);
    }


    public void setShape(Shape shp, int style) {
    }

    public String getLabel()
    {
	return getText();
    }
    
    public void setLabel(String label) {
	setText(label);
	owner.readjustEdges();
    }

    public void setLocation(double x1, double y1, 
			    double width, double height) {

	setBounds(x1,
		  y1,
		  width,
		  height);
	owner.readjustEdges();
	notifyHullListeners();
    }

    public void setLocation(double x1, double y1) {
	
	double w = super.getWidth();
	double h = super.getHeight();

	//System.out.println("f: " + x1 + " " + y1);

	setBounds(x1,
		  y1,
		  w,
		  h);

	owner.readjustEdges();
	notifyHullListeners();
    }


    public void setSize(double width, double height) {
	
	double x = super.getX();
	double y = super.getY();

	setBounds(x-width,
		  y-height,
		  width,
		  height);
    }


    public void mouseEntered(PInputEvent aEvent) {
	if (getVisible()) {
	    GraphEvents.mouseEnter(owner);
	}
    }

    public void mouseExited(PInputEvent aEvent) {
	if (getVisible()) {
	    GraphEvents.mouseLeave(owner);
	}
    }
    
    public void highlight(boolean state) {
	if (!getVisible()) 
	    return;
	
	highlightMode = state;
	if (state) {
	    String label = getLabel();
	    if ((label != null) && (label.equals(owner.getName()))) {
		StatusBar.setStatus(label);
	    } else if (label == null) {
		StatusBar.setStatus(owner.getName());
	    } else {
		StatusBar.setStatus(owner.getName() + " (" + label + ")");
	    }
	} else {
	    StatusBar.setStatus("");
	}
	this.repaint();
    }

    public double getDrawWidth() {
	return(getWidth());
    }

    public double getDrawHeight() {
	return(getHeight());
    }

    protected boolean highlightMode = false;
    protected boolean labelMode = false;

    public void label(boolean state) {
	labelMode = state;
    }

    protected static BasicStroke linestroke = new BasicStroke((float).3);
    

    public void paintInternal(PPaintContext apc) {
	Graphics2D g2 = apc.getGraphics();
	if (highlightMode) {
	    g2.setPaint(Color.yellow);
	} else {
	    g2.setPaint(curcolor);
	}

	if (borderShape == null) {
	    // if no one's ever told us what shape to use
	    // go with the default
	    setStyle(style);
	}

	if (Guess.getZooming() == Guess.ZOOMING_SPACE) {
		borderShape.setFrame(getX(),
			     getY(),
			     getWidth(),
			     getHeight());
		
	} else if (Guess.getZooming() == Guess.ZOOMING_ZOOM) {
		borderShape.setFrame(getX()-4,
			     getY()-4,
			     getWidth() + 8,
			     getHeight() + 8);
	}
		
	g2.fill(borderShape);
	g2.setPaint(strokePaint);
	g2.setStroke(linestroke);
	g2.draw(borderShape);
    }

    float oldScaling = 0f;
    
	public void paint(PPaintContext context) {

		paintInternal(context);
		if (Guess.getZooming() == Guess.ZOOMING_SPACE) {

			Graphics2D g2 = context.getGraphics();
			float scaling = (float)(1/((GFrame)VisFactory.getFactory().getDisplay()).getGCamera().getViewScale());
			Font f = g2.getFont();
			f = f.deriveFont((float)(10*scaling));
			g2.setFont(f);
			g2.drawString(getLabel(),(float)(getX() + (8 * scaling)), (float)(getY() + (10 * scaling)));
			


			if (scaling!=oldScaling) {
				oldScaling = scaling;
				owner.readjustEdges();
			}
			

			
		} else if (Guess.getZooming() == Guess.ZOOMING_ZOOM) {
			paintInternal(context);
			super.paint(context);
		}
		
		
    }	
	
	
	public PBounds getBoundsReference() {
		if ((Guess.getZooming() == Guess.ZOOMING_SPACE) &&
				(borderShape != null)) {
			
			PBounds newBounds = (PBounds) super.getBoundsReference().clone();
			float scaling = (float)(1/((GFrame)VisFactory.getFactory().getDisplay()).getGCamera().getViewScale());
			
			
			newBounds.setRect(
					newBounds.getX() + ((newBounds.getWidth() - (newBounds.getWidth() * scaling))/2), 
					newBounds.getY() + ((newBounds.getHeight() - (newBounds.getHeight() * scaling))/2),
					newBounds.getWidth() * scaling,  
					newBounds.getHeight() * scaling
					);
	
			
			edu.umd.cs.piccolox.handles.PHandle.DEFAULT_HANDLE_SHAPE = new Ellipse2D.Float(0f, 0f, 6 * scaling, 6 * scaling);
			return newBounds;
		}
		return super.getBoundsReference();
	}
	
    public static int ZOOM_SIZE = 40;


    public void moveDone(double x, double y) {
	owner.endMove(x,y);
	notifyHullListeners();
    }
    
    public void inTransition(double x, double y) {
	owner.move(x,y);
	hideHullListeners();
    }

    public void keyTyped(PInputEvent aEvent) {
	System.out.println(""+aEvent.getKeyChar());
    }

    public void mouseClicked(PInputEvent aEvent) {
	
	if (aEvent.isLeftMouseButton()) {
	    if (frame.getMode() == GFrame.BROWSE_MODE) {
		if (aEvent.isShiftDown()) {
		    GraphEvents.shiftClick(owner);
		} else {
		    GraphEvents.click(owner);
		}
	    }
	}
    }
  
    public void centerDisplay() {
	frame.centerOn(this);
    }

    public void set(String field, Object o) {
	try {
	    if (field.equals("style")) {
		setStyle(((Integer)o).intValue());
	    } else if (field.equals("x")) {
		setLocation(((Double)o).doubleValue(),getY());
	    } else if (field.equals("y")) {
		setLocation(getX(),((Double)o).doubleValue());
	    } else if (field.equals("width")) {
		setLocation(getX(), getY(),((Double)o).doubleValue(), 
			    getHeight());
	    } else if (field.equals("height")) {
		setLocation(getX(), getY(),getWidth(),
			    ((Double)o).doubleValue()); 
	    } else if (field.equals("label")) {
		setLabel((String)o);
	    } else if (field.equals("color")) {
		if (o instanceof Color) {
		    setPaint((Color)o);
		} else {
		    setPaint((Colors.getColor((String)o,(Color)getPaint())));
		}
	    } else if (field.equals("strokecolor")) {
		if (o instanceof Color) {
		    strokePaint = (Color)o;
		} else {
		    strokePaint = 
			(Colors.getColor((String)o,(Color)getPaint()));
		}
	    } else if (field.equals("visible")) {
		setVisible(((Boolean)o).booleanValue());
	    } else if (field.equals("labelsize")) {
		setLabelSize(((Integer)o).intValue());
	    } else if (field.equals("opacity")) {
			if (o instanceof Float) {
				setTransparency((Float)o);
			}
		}
	    
	    if (Guess.getMTF()) 
		moveToFront();
	} catch (Exception e) {
	    ExceptionWindow.getExceptionWindow(e);
	    throw new Error("Problem with setting rep attribute: " + 
			    e.toString());
	}
    }

    private void setLabelSize(int newsz) {
	Font f = getFont().deriveFont((float)newsz);
	setFont(f);
    }

    private int getLabelSize() {
	return(getFont().getSize());
    }

    public Object get(String field) {
	try {
	    if (field.equals("style")) {
		return new Integer(style);
	    } else if (field.equals("x")) {
		return(new Double(getX()));
	    } else if (field.equals("y")) {
		return(new Double(getY()));
	    } else if (field.equals("width")) {
		return(new Double(getWidth()));
	    } else if (field.equals("height")) {
		return(new Double(getHeight()));
	    } else if (field.equals("label")) {
		return getText();
	    } else if (field.equals("color")) {
		return(Colors.toString((Color)getPaint()));
	    } else if (field.equals("visible")) {
		return(new Boolean(getVisible()));
	    } else if (field.equals("labelvisible")) {
		return(new Boolean(true));
	    } else if (field.equals("strokecolor")) {
		return(strokePaint);
	    } else if (field.equals("labelsize")) {
		return(new Integer(getLabelSize()));
	    } else {
		return(null);
	    }
	} catch (Exception e) {
	    throw new Error("Problem with getting rep attribute: " + field + 
			    " " + e.toString());
	}
    }

    public HashSet<ConvexHullNode> hulls = null;

    public void addHullListener(ConvexHullNode chn) {
	if (hulls == null) {
	    hulls = new HashSet<ConvexHullNode>();
	}
	hulls.add(chn);
    }
    
    public void removeHullListener(ConvexHullNode chn) {
	if (hulls != null) {
	    hulls.remove(chn);
	}
    }

    private void notifyHullListeners() {
	if (hulls != null) {
	    Iterator<ConvexHullNode> it = hulls.iterator();
	    while(it.hasNext()) {
		ConvexHullNode chn = (ConvexHullNode)it.next();
		chn.nodeMoved(this);
	    }
	}
    }

    private void hideHullListeners() {
	if (hulls != null) {
	    Iterator<ConvexHullNode> it = hulls.iterator();
	    while(it.hasNext()) {
		ConvexHullNode chn = (ConvexHullNode)it.next();
		chn.setVisible(false);
	    }
	}
    }

    public Point2D[] getAllPorts() {
	Point2D[] toRet = new Point2D[8];
	toRet[0] = new Point2D.Double(getX()+getWidth()/2,
				      getY());
	toRet[1] = new Point2D.Double(getX()+getWidth()/2,
				      getY()+getHeight());
	toRet[2] = new Point2D.Double(getX(),
				      getY()+getHeight()/2);
	toRet[3] = new Point2D.Double(getX()+getWidth(),
				      getY()+getHeight()/2);
	toRet[4] = new Point2D.Double(getX(),getY());
	toRet[5] = new Point2D.Double(getX()+getWidth(),getY());
	toRet[6] = new Point2D.Double(getX(),getY()+getHeight());
	toRet[7] = new Point2D.Double(getX()+getWidth(),
				      getY()+getHeight());
	return(toRet);
    }

    public Point2D[] getPrefPorts() {
	Point2D[] toRet = new Point2D[4];
	toRet[0] = new Point2D.Double(getX()+getWidth()/2,
				      getY());
	toRet[1] = new Point2D.Double(getX()+getWidth()/2,
				      getY()+getHeight());
	toRet[2] = new Point2D.Double(getX(),
				      getY()+getHeight()/2);
	toRet[3] = new Point2D.Double(getX()+getWidth(),
				      getY()+getHeight()/2);
	return(toRet);
    }
    
	public void addFieldToLabel(String field) {
		System.out.println("Not implemented yet");
	}

	public void removeFieldFromLabel(String field) {
		System.out.println("Not implemented yet");
	}
	
	public GFrame getFrame() {
		return frame;
	}

}
