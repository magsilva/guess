package com.hp.hpl.guess.piccolo.util;

import java.awt.Paint;
import java.io.IOException;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

public class SerRectangle extends PPath implements SerInterface {

	private static final long serialVersionUID = 3525195859503862301L;
	private double sx,sy,sw,sh;
 
    public SerRectangle() {
	super();
    }
   
    public SerRectangle(double x, 
		      double y,
		      double w,
		      double h) {
	setPathToRectangle((float)x,
			   (float)y,
			   (float)w,
			   (float)h);
	sx = x;
	sy = y;
	sw = w;
	sh = h;
    }

    public void writeObject(java.io.ObjectOutputStream ois)
	throws IOException {
	ois.writeDouble(sx);
	ois.writeDouble(sy);
	ois.writeDouble(sw);
	ois.writeDouble(sh);
	ois.writeObject(getStrokePaint());
	ois.writeObject(getBounds());
	Util.writeBasicStroke(ois,getStroke());
    }

    public void readObject(java.io.ObjectInputStream ois)
	throws IOException, ClassNotFoundException {
	sx = ois.readDouble();
	sy = ois.readDouble();
	sw = ois.readDouble();
	sh = ois.readDouble();
	setPathToRectangle((float)sx,
			   (float)sy,
			   (float)sw,
			   (float)sh);
	setPaint(null);
	setStrokePaint((Paint)ois.readObject());
	setBounds((PBounds)ois.readObject());
	setStroke(Util.readBasicStroke(ois));
    }

}
