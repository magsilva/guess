package com.hp.hpl.guess.piccolo.util;

import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

public class SerEllipse extends PPath implements SerInterface {
    
	private static final long serialVersionUID = 900226582714669823L;
	private double sx,sy,sw,sh;

    public SerEllipse() {
	super();
    }

    public SerEllipse(double x, 
		   double y,
		   double w,
		   double h) {
	setPathTo(new Ellipse2D.Double(x,y,w,h));
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
	setPathTo(new Ellipse2D.Double(sx,sy,sw,sh));
	setPaint(null);
	setStrokePaint((Paint)ois.readObject());
	setBounds((PBounds)ois.readObject());
	setStroke(Util.readBasicStroke(ois));
    }

}
