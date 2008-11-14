package com.hp.hpl.guess.piccolo;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.ui.*;
import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.animation.AnimationFactory;
import com.hp.hpl.guess.animation.GAnimation;
import com.hp.hpl.guess.piccolo.GFrame;
import com.hp.hpl.guess.piccolo.util.PFixedWidthStroke;

public class GuessShapeNode extends PPath implements GuessPNode {
    
    protected Node owner = null;
    
    protected GFrame frame = null;

    private Double strokeWidth = 1.0;
    
    protected int style = VisFactory.RECTANGLE;

    public Node getOwner() {
	return(owner);
    }
    
    public GFrame getFrame() {
    	return(frame);
    }

    public void set(String field, Object o) {

    	/*try {*/
    	if (field.equals("style")) {
    	    setStyle(((Integer)o).intValue());
    	} else if (field.equals("x")) {	
    	    setLocation(((Double)o).doubleValue(),getY());
    	} else if (field.equals("y")) {
    	    setLocation(getX(),((Double)o).doubleValue());
     	} else if (field.equals("width")) {
    		// If we are in ZOOMING_SPACE, then
    		// we need to recalculate the size
    		if (Guess.getZooming() == Guess.ZOOMING_SPACE) {
    			setLocation(getX(), getY(),((Double)o).doubleValue() * 
    					(1/frame.getGCamera().getViewScale()), getHeight());
    		} else if (Guess.getZooming() == Guess.ZOOMING_ZOOM) {
    			setLocation(getX(), getY(),((Double)o).doubleValue(), 
    					getHeight());
    		}
    	} else if (field.equals("height")) {
    		// If we are in ZOOMING_SPACE, then
    		// we need to recalculate the size    		
    		if (Guess.getZooming() == Guess.ZOOMING_SPACE) {
    		    setLocation(getX(), getY(),getWidth(),
    					((Double)o).doubleValue()* (1/frame.getGCamera().getViewScale())); 
    		} else if (Guess.getZooming() == Guess.ZOOMING_ZOOM) {
    		    setLocation(getX(), getY(),getWidth(),
    					((Double)o).doubleValue()); 
    		}
    	} else if (field.equals("label")) {
    	    //if (o == null) {
    	    //Thread.dumpStack();
    	    //}
    	    //System.out.println("class: " + o.getClass());
    	    setLabel((String)o);
    	} else if (field.equals("labelvisible")) {
    	    setLabelVisible(((Boolean)o).booleanValue());
    	} else if (field.equals("color")) {
    	    if (o instanceof Color) {
    		setPaint((Color)o);
    	    } else {
    		setPaint((Colors.getColor((String)o,(Color)getPaint())));
    	    }
    	} else if (field.equals("labelcolor")) {
    	    if (o instanceof Color) {
    		setLabelPaint((Color)o);
    	    } else {
    		setLabelPaint((Colors.getColor((String)o,(Color)getPaint())));
    	    }
    	} else if (field.equals("visible")) {
    	    setVisible(((Boolean)o).booleanValue());
    	} else if (field.equals("strokewidth")) {
    		setStrokeWidth(((Double)o).doubleValue());
    	} else if (field.equals("strokecolor")) {
    	    if (o instanceof Color) {
    		setStrokePaint((Color)o);
    	    } else {
    		setStrokePaint((Colors.getColor((String)o,(Color)getPaint())));
    	    }
    	} else if (field.equals("opacity")) {
    		if (o instanceof Float) {
    			setTransparency((Float)o);
    		}
    	}

	if (Guess.getMTF()) 
	    moveToFront();
    
	/*} catch (Exception e) {
	    throw new Error("Problem with setting rep attribute: " + 
			    e.toString());
	}*/
    }

    protected Color labelColor = null;
    
    public void setLabelPaint(Color c) {
	labelColor = c;
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
		return(label);
	    } else if (field.equals("labelvisible")) {
		return(new Boolean(labelMode));
	    } else if (field.equals("color")) {
		return(Colors.toString(curcolor));
	    } else if (field.equals("labelcolor")) {
		if (labelColor != null) {
		    return(Colors.toString(labelColor));
		} else {
		    return(Colors.toString(curcolor));
		}
	    } else if (field.equals("strokewidth")) {
	    return(getStrokeWidth().toString());
	    } else if (field.equals("strokecolor")) {
		return(Colors.toString((Color)getStrokePaint()));
	    } else if (field.equals("visible")) {
		return(new Boolean(getVisible()));
	    } else {
		return(null);
	    }
	} catch (Exception e) {
	    throw new Error("Problem with getting rep attribute: " + field + 
			    " " + e.toString());
	}
    }

    public int getStyle() {
	return(style);
    }

    public void setStyle(int s) {
	if ((s > 100) || (s != style)) {
	    NodeListener rep = 
		((PFactory)VisFactory.getFactory()).convertNode(this,s,owner);
	    owner.setRep(rep);
	    owner.readjustEdges();
	}
    }

    public GuessShapeNode(Shape aShape, Node owner, GFrame frame, int style) {
		super(aShape);
		this.owner = owner;
		this.frame = frame;
		this.style = style;
		this.label = owner.getName();
		
	    if (Guess.getZooming() == Guess.ZOOMING_SPACE) {
	    	setStroke(new PFixedWidthStroke(strokeWidth.floatValue()));
	    } else {
	    	setStroke(new BasicStroke(strokeWidth.floatValue()));
	    }
    }
        
    /**
     * Sets the width of the border
     * @param width stroke width
     */
    public void setStrokeWidth(Double width) {
    	strokeWidth = width;
        if (Guess.getZooming() == Guess.ZOOMING_SPACE) {
        	setStroke(new PFixedWidthStroke((float)width.floatValue()));
        } else {
        	setStroke(new BasicStroke((float)width.floatValue()));
        }
    }
    
    /**
     * Returns the width of the stroke
     * @return stroke width
     */
    public Double getStrokeWidth() {
    	return strokeWidth;
    }


    public boolean setBounds(java.awt.geom.Rectangle2D newBounds) {
	// notify Node that we're resizing
	boolean toRet = super.setBounds(newBounds);
	owner.readjustEdges();
	return(toRet);
    }
    
    Color curcolor = null;

    public void setPaint(Color c) {
	Color oldcolor = (Color)getPaint();
	super.setPaint(c);
	curcolor = c;
	if ((oldcolor != null) && 
	    (oldcolor.getAlpha() != curcolor.getAlpha())) {
	    // change in transparency, we should change
	    // the color of the line we use
	    Color oldSP = (Color)getStrokePaint();
	    setStrokePaint(Colors.getColor(oldSP.getRed()+","+
					   oldSP.getGreen()+","+
					   oldSP.getBlue()+","+
					   curcolor.getAlpha(),
					   oldSP));
	}
    }
    
    public void setColor(Color c)
    {
	setPaint(c);
    }

    public void setShape(int type) {
    }

    public void setShape(Shape shp, int type) {
	this.style = type;
	setPathTo(shp);
    }

    protected String label = "";
    protected String[] multiLineLabel = null;

    public void setLabel(String str) {
	this.label = str;
	if (label != null) {
	    if (label.indexOf("\n") >= 0) {
		multiLineLabel = breakupLines(str);
	    } else {
		multiLineLabel = null;
	    }
	}
	//System.out.println("Thread 2: " + Thread.currentThread().getName() + " " + owner + " " + label + " " + multiLineLabel + " " + str);
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

	setBounds(x1, y1, w, h);

	owner.readjustEdges();
	notifyHullListeners();
    }
    
    public boolean setBounds(double x1, double y1, double w, double h) {
		double neww = w;
		double newh = h;
    	if (Guess.getZooming()==Guess.ZOOMING_SPACE) {
	    	if (frame!=null) {
	    	neww = w * frame.getGCamera().getViewScale();
	    	newh = h * frame.getGCamera().getViewScale();
	    	x1 = x1 - (neww - w)/2;
	    	y1 = y1 - (newh - h)/2;
	    	}
    	}
    	
		return super.setBounds(x1, y1, neww, newh);
    }


    public void setSize(double width, double height) {
	double x = super.getX();
	double y = super.getY();

	setBounds(x,
		  y,
		  width,
		  height);
    }


    //private PInterpolatingActivity act = null;
    //private PBounds pb = null;

    public void mouseEntered(PInputEvent aEvent) {
	if (getVisible()) {
	    GraphEvents.mouseEnter(owner);
	    // if (act != null) {
	    //act.terminate();
	    //act = null;
	    //setBounds(pb);
	    //pb = null;
	    //}
	    //pb = getBounds();
	    //act = animateToBounds(super.getX()-super.getWidth(),
	    //		  super.getY()-super.getHeight(),
	    //		  super.getWidth()*2,
	    //		  super.getHeight()*2,
	    //		  500);
	}
    }

    public void mouseExited(PInputEvent aEvent) {
	if (getVisible()) {
	    GraphEvents.mouseLeave(owner);
	    //    if (act != null) {
	    //act.terminate();
	    //act = null;
	    //setBounds(pb);
	    //pb = null;
	    //}
	}
    }
    
    protected LabelText labelText = new LabelText(this);
    
    public void addFieldToLabel(String aField) {
    	labelText.addField(aField);
    }
    
    public void removeFieldFromLabel(String aField) {
    	labelText.removeField(aField);
    } 

    public void highlight(boolean state) {

	//System.out.println(owner.getName() + " " + state);
	//Thread.dumpStack();

	if (!getVisible()) 
	    return;

	if (state) {
	    //System.out.println("**** " + label);
	    super.setPaint(Color.yellow);
	    labelText.setText(label);
	    labelText.addField("name");
	    labelText.setPaint(new Color(100,100,100,210));
	    labelText.setTextPaint(Color.yellow);
	    float scaling = (float)(1/frame.getGCamera().getViewScale());
	    labelText.setX(getX() + getWidth()+1*scaling);
	    labelText.setY(getY() + getHeight());
	    labelText.recalculateLocation();
	    frame.labels.addChild(labelText);
	    if ((label != null) && (label.equals(owner.getName()))) {
		StatusBar.setStatus(label);
	    } else if (label == null) {
		StatusBar.setStatus(owner.getName());
	    } else {
		StatusBar.setStatus(owner.getName() + " (" + label + ")");
	    }
	} else {
	    super.setPaint(curcolor);
	    labelText.removeFromParent();
	    StatusBar.setStatus("");
	}
	highlightMode = state;
    }

    protected boolean highlightMode = false;
    protected boolean labelMode = false;

    public void setLabelVisible(boolean state) {
	labelMode = state;
	if (state) {
	    if ((label == null) || (label.equals(""))) {
		label = owner.getName();
	    }
	}
    }
    
    public static String[] breakupLines(String text) { 
	String[] toRet = null;
	StringTokenizer st = new StringTokenizer(text,"\n");
	Vector v = new Vector();
	while (st.hasMoreTokens()) {
	    v.addElement(st.nextToken());
	}
	toRet = new String[v.size()];
	for (int i = 0 ; i < v.size() ; i++) {
	    toRet[i] = (String)v.elementAt(i);
	}
	return(toRet);
    }

    public void paintLabel(Graphics2D g, 
			   float labelX, 
			   float labelY,
			   Font font) { 

	//	System.out.println(labelColor);
	if (labelColor != null) {
	    g.setPaint(labelColor);
	}

	if (multiLineLabel == null) {
	    g.drawString(label,(float)labelX,(float)labelY);
	    return;
	}
	
	FontMetrics fontMetrics = 
	    Toolkit.getDefaultToolkit().getFontMetrics(font); 
	
	int fontHeight = fontMetrics.getHeight(); 
	int fontAscent = fontMetrics.getAscent(); 
	
	int num_lines = multiLineLabel.length; 
	float height; 
	int i; 
	
	for (i=0, height=labelY; i<num_lines; i++, height+=fontHeight) { 
	    g.drawString(multiLineLabel[i], labelX, height); 
	} 
    }


    public void paintOverload(PPaintContext apc) {
	Graphics2D g2 = apc.getGraphics();
	if (labelMode) {
	    float scaling = (float)(1/frame.getGCamera().getViewScale());
	    Font f = g2.getFont();
	    f = f.deriveFont((float)(12*scaling));
	    if (Guess.getDefaultFont() != null) {
		if (!f.getFamily().equals(Guess.getDefaultFont())) {
		    f = new Font(Guess.getDefaultFont(),
				 f.getStyle(),
				 f.getSize());
		}
	    }
	    g2.setFont(f);
	    g2.setPaint(curcolor);
	    paintLabel(g2,(float)(getX() + getWidth()+2),
		       (float)(getY() + getHeight()),f);
	} 
	g2.setStroke(getStroke());
    }

    Shape oldta = null;
    AffineTransform atTrans = null;
    Shape ta = null;
    
	public void paint(PPaintContext context) {
		paintOverload(context);

		if (Guess.getZooming() == Guess.ZOOMING_SPACE) {
			double contextScale = context.getScale();
			Paint p = getPaint();
			Graphics2D g2 = context.getGraphics();
	
	        ta = getPathReference();
	        
	        // Save old coordinates
	        Rectangle2D oldRect = ta.getBounds2D();
	
	        // Translate to 0
	        atTrans = AffineTransform.getTranslateInstance(-oldRect.getCenterX(), -oldRect.getCenterY());
			ta = atTrans.createTransformedShape(ta);
	
	        // do scaling
			atTrans = AffineTransform.getScaleInstance(1 / contextScale, 1 / contextScale);
			ta = atTrans.createTransformedShape(ta);
	
	        // Translate to old coordinates
			atTrans = AffineTransform.getTranslateInstance(oldRect.getCenterX(), oldRect.getCenterY());
			ta = atTrans.createTransformedShape(ta);
	
			if (p != null) {
				g2.setPaint(p);
				g2.fill(ta);
			}
	
			if (getStroke() != null && getStrokePaint() != null) {
				g2.setPaint(getStrokePaint());
				g2.setStroke(getStroke());
				g2.draw(ta);
			}
			
			
			if ((oldta==null) || 
					((oldta.getBounds2D().getHeight()-ta.getBounds2D().getHeight())*contextScale)>1.5 ||
					((oldta.getBounds2D().getHeight()-ta.getBounds2D().getHeight())*contextScale)<-1.5) {
				oldta = ta;
				owner.readjustEdges();
			}
		
		} else if (Guess.getZooming() == Guess.ZOOMING_ZOOM) {
			super.paint(context);
		}

	}
	

	public PBounds getBoundsReference() {
		if (Guess.getZooming() == Guess.ZOOMING_SPACE) {
		PBounds newBounds = (PBounds) super.getBoundsReference().clone();
		Double contextScale = ((GFrame)VisFactory.getFactory().getDisplay()).getGCamera().getViewScale();

		newBounds.setRect(
				newBounds.getX() + ((newBounds.getWidth() - (newBounds.getWidth() * (1 / contextScale)))/2), 
				newBounds.getY() + ((newBounds.getHeight() - (newBounds.getHeight() * (1 / contextScale)))/2),
				newBounds.getWidth() * (1 / contextScale),  
				newBounds.getHeight() * (1 / contextScale)
				);
		
		edu.umd.cs.piccolox.handles.PHandle.DEFAULT_HANDLE_SHAPE = new Ellipse2D.Float(0f, 0f, 6 * (float)(1/contextScale), 6 * (float)(1/contextScale));
	
		return newBounds;
		} else {
			return super.getBoundsReference();
		}
	}

    public double getDrawWidth() {
	return(getWidth());
    }

    public double getDrawHeight() {
	return(getHeight());
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
	//Thread.dumpStack();
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
	    
    public HashSet hulls = null;

    public void addHullListener(ConvexHullNode chn) {
	if (hulls == null) {
	    hulls = new HashSet();
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
	    Iterator it = hulls.iterator();
	    while(it.hasNext()) {
		ConvexHullNode chn = (ConvexHullNode)it.next();
		chn.setVisible(true);
		chn.nodeMoved(this);
	    }
	}
    }

    private void hideHullListeners() {
	if (hulls != null) {
	    Iterator it = hulls.iterator();
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
}
