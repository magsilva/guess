package com.hp.hpl.guess.piccolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.net.URL;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.ui.Colors;
import com.hp.hpl.guess.ui.ExceptionWindow;
import com.hp.hpl.guess.ui.GraphEvents;
import com.hp.hpl.guess.ui.NodeListener;
import com.hp.hpl.guess.ui.StatusBar;
import com.hp.hpl.guess.ui.VisFactory;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

public class GuessImageNode extends PImage implements GuessPNode {
    
	private static final long serialVersionUID = -9135760707966682381L;

	private Node owner = null;
    
    private GFrame frame = null;

    private int style = VisFactory.RECTANGLE;

    private String image = "";

    public Node getOwner() {
	return(owner);
    }

    private static Hashtable<String, Image> imageCache = new Hashtable<String, Image>();
    
    public void set(String field, Object o) {
	/*try {*/
	if (field.equals("style")) {
	    setStyle(((Integer)o).intValue());
	} else if (field.equals("x")) {
	    _xCache = ((Double)o).doubleValue();
	    setLocation(((Double)o).doubleValue(),getY());
	} else if (field.equals("y")) {
	    _yCache = ((Double)o).doubleValue();
	    setLocation(getX(),((Double)o).doubleValue());
	} else if (field.equals("width")) {
	    _widthCache = ((Double)o).doubleValue();
	    setLocation(getX(), getY(),((Double)o).doubleValue(), 
			getHeight());
	} else if (field.equals("height")) {
	    _heightCache = ((Double)o).doubleValue();
	    setLocation(getX(), getY(),getWidth(),
			((Double)o).doubleValue()); 
	} else if (field.equals("label")) {
			setLabel((String)o);
	} else if (field.equals("labelvisible")) {
	    setLabelVisible(((Boolean)o).booleanValue());
	} else if (field.equals("visible")) {
	    setVisible(((Boolean)o).booleanValue());
	} else if (field.equals("image")) {
	    if (o instanceof Image) {
		setImage((Image)o);
	    } else {
		setImage((String)o);
	    }
	} else if (field.equals("labelcolor")) {
	    if (o instanceof Color) {
		setLabelPaint((Color)o);
	    } else {
		setLabelPaint((Colors.getColor((String)o,(Color)getPaint())));
	    }
	}  else if (field.equals("opacity")) {
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

    public void setImage(Image img) {
	if (img == null) 
	    return;

	super.setImage(img);
	if (_xCache == -1) {
	    _xCache = getX();
	}
	if (_yCache == -1) {
	    _yCache = getY();
	}
	if (_widthCache == -1) {
	    _widthCache = getWidth();
	}
	if (_heightCache == -1) {
	    _heightCache = getHeight();
	}
	setLocation(_xCache,_yCache,_widthCache,_heightCache);
    }

    private static void createErrorImage() {
	try {
	    if (imageCache.containsKey("___ERROR")) {
		return;
	    }
	    BufferedImage bi = 
		new BufferedImage(30,30,BufferedImage.TYPE_INT_RGB);
	    Graphics g = bi.getGraphics();
	    g.setColor(Color.red);
	    g.drawLine(0,0,30,30);
	    g.drawLine(0,30,30,0);
	    imageCache.put("___ERROR",bi);
	} catch (Exception ex) {
	}
    }


    public void setImage(String filename) {
	if (filename == null) {
	    createErrorImage();
	    setImage("___ERROR");
	    return;
	}
	
	if (filename.equals("")) {
	    createErrorImage();
	    setImage("___ERROR");
	    return;
	}

	//System.out.println("setting image...");
	this.image = filename;
	if (imageCache.containsKey(filename)) {
	    //	    System.out.println("getting from cache...");
	    super.setImage((java.awt.Image)imageCache.get(filename));
	} else {
	    try {
		if (filename.startsWith("http")) {
		    URL url = new URL(filename);
		    
		    // Get the image
		    java.awt.Image fim = 
			java.awt.Toolkit.getDefaultToolkit().getDefaultToolkit().createImage(url);
		    super.setImage(fim);
		    imageCache.put(filename,getImage());
		} else {
		    super.setImage(filename);
		    imageCache.put(filename,getImage());
		}
	    } catch (Exception ex) {
		ExceptionWindow.getExceptionWindow(ex);
		StatusBar.setErrorStatus("Error loading image: " + filename);
		if (!filename.equals("___ERROR")) {
		    createErrorImage();
		    setImage("___ERROR");
		}
	    }
	}
	//System.out.println(_xCache + " " + _yCache);
	//System.out.println(_widthCache + " " + _heightCache);
	if (_xCache == -1) {
	    _xCache = getX();
	}
	if (_yCache == -1) {
	    _yCache = getY();
	}
	if (_widthCache == -1) {
	    _widthCache = getWidth();
	}
	if (_heightCache == -1) {
	    _heightCache = getHeight();
	}
	setLocation(_xCache,_yCache,_widthCache,_heightCache);
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
	    } else if (field.equals("visible")) {
		return(new Boolean(getVisible()));
	    } else if (field.equals("image")) {
		return(image);
	    } else if (field.equals("labelcolor")) {
		if (labelColor != null) {
		    return(Colors.toString(labelColor));
		} else {
		    return(Colors.toString(curcolor));
		}
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
	if (s != style) {
	    NodeListener rep = 
		((PFactory)VisFactory.getFactory()).convertNode(this,s,owner);
	    owner.setRep(rep);
	    owner.readjustEdges();
	}
    }

    public GuessImageNode(Node owner, GFrame frame, int style,
			  double x, double y,
			  double width, double height) {
	super();
	this.owner = owner;
	this.frame = frame;
	this.style = style;
	this.label = owner.getName();
	_xCache = x;
	_yCache = y;
	_widthCache = width;
	_heightCache = height;
    }

    private double _xCache = -1;
    private double _yCache = -1;
    private double _widthCache = -1;
    private double _heightCache = -1;

    public boolean setBounds(java.awt.geom.Rectangle2D newBounds) {
	// notify Node that we're resizing
	boolean toRet = super.setBounds(newBounds);
	owner.readjustEdges();
	return(toRet);
    }
    
    Color curcolor = null;

    public void setColor(Color c)
    {
	curcolor = c;
    }

    public void setShape(int type) {
    }

    public void setShape(Shape shp, int type) {
    }

    private String label = "";
    private String[] multiLineLabel = null;

    public void setLabel(String str) {
	//System.out.println("label: " + label);
	this.label = str;
	if (label != null) {
	    if (label.indexOf("\n") >= 0) {
		multiLineLabel = breakupLines(str);
	    } else {
		multiLineLabel = null;
	    }
	}
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

	setBounds(x,
		  y,
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
    
    private LabelText labelText = null;

    public void highlight(boolean state) {
	if (!getVisible()) 
	    return;

	if (labelText == null) {
	    labelText = new LabelText(this);
	}

	if (state) {
	    super.setPaint(Color.yellow);
	    labelText.setText(label);
	    labelText.setPaint(new Color(100,100,100,210));
	    labelText.setTextPaint(Color.yellow);
	    float scaling = (float)(1/frame.getGCamera().getViewScale());
	    labelText.setX(getX() + getWidth()+1*scaling);
	    labelText.setY(getY() + getHeight());
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
    }

    private boolean labelMode = false;

    public void setLabelVisible(boolean state) {
	labelMode = state;
	if (state) {
	    if ((label == null) || (label.equals(""))) {
		label = owner.getName();
	    }
	}
    }
    
    private static BasicStroke linestroke = new BasicStroke((float).3);
    
    public static String[] breakupLines(String text) { 
	String[] toRet = null;
	StringTokenizer st = new StringTokenizer(text,"\n");
	Vector<String> v = new Vector<String>();
	while (st.hasMoreTokens()) {
	    v.addElement(st.nextToken());
	}
	toRet = new String[v.size()];
	for (int i = 0 ; i < v.size() ; i++) {
	    toRet[i] = (String)v.elementAt(i);
	}
	return(toRet);
    }

    private Color labelColor = null;
    
    public void setLabelPaint(Color c) {
	labelColor = c;
    }

    public void paintLabel(Graphics2D g, 
			   float labelX, 
			   float labelY,
			   Font font) { 

	if (labelColor != null) {
	    g.setPaint(labelColor);
	}

	if (multiLineLabel == null) {
	    g.drawString(label,(float)labelX,(float)labelY);
	    return;
	}
	

	FontRenderContext context = g.getFontRenderContext();
	LineMetrics fontMetrics = font.getLineMetrics(label, context);
	
	float fontHeight = fontMetrics.getHeight(); 
	
	int num_lines = multiLineLabel.length; 
	float height; 
	int i; 
	
	for (i=0, height=labelY; i<num_lines; i++, height+=fontHeight) { 
	    g.drawString(multiLineLabel[i], labelX, height); 
	} 
    }

    public void paint(PPaintContext apc) {
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
	g2.setStroke(linestroke);
	super.paint(apc);
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
		chn.setVisible(true);
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
