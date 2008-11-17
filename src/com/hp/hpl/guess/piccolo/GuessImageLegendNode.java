package com.hp.hpl.guess.piccolo;

import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.ui.VisFactory;

import edu.umd.cs.piccolo.nodes.PImage;

public class GuessImageLegendNode extends PImage {
    

	private static final long serialVersionUID = -1534850045228643401L;
	private int style = VisFactory.IMAGE;

    public int getStyle() {
	return(style);
    }

    public GuessImageLegendNode(Node owner) {
	super();
	GuessImageNode gsn = (GuessImageNode)owner.getRep();
	double width = gsn.getWidth();
	double height = gsn.getHeight();
	setImage(gsn.getImage());
	setBounds(0,0,width,height);
    }


    public boolean setBounds(java.awt.geom.Rectangle2D newBounds) {
	// notify Node that we're resizing
	boolean toRet = super.setBounds(newBounds);
	return(toRet);
    }
    
    public void setLocation(double x1, double y1, 
			    double width, double height) {

	setBounds(x1,
		  y1,
		  width,
		  height);
    }

    public void setLocation(double x1, double y1) {
	
	double w = super.getWidth();
	double h = super.getHeight();

	//System.out.println("f: " + x1 + " " + y1);

	setBounds(x1,
		  y1,
		  w,
		  h);

    }


    public void setSize(double width, double height) {
	
	double x = super.getX();
	double y = super.getY();

	setBounds(x,
		  y,
		  width,
		  height);
    }

    public double getDrawWidth() {
	return(getWidth());
    }

    public double getDrawHeight() {
	return(getHeight());
    }
}
