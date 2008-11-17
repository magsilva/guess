package com.hp.hpl.guess.piccolo.util;

import java.awt.Paint;
import java.awt.geom.Line2D;
import java.io.IOException;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

public class SerLine extends PPath implements SerInterface {
    
	private static final long serialVersionUID = 5034801442583809337L;
	private double sx,sy,sw,sh;

    public SerLine() {
	super();
    }

    public SerLine(double x, 
		   double y,
		   double w,
		   double h) {
	setPathTo(new Line2D.Double(x,y,w,h));
	sx = x;
	sy = y;
	sw = w;
	sh = h;
    }

    public void setPathTo(double x, 
		   double y,
		   double w,
		   double h) {
	setPathTo(new Line2D.Double(x,y,w,h));
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
	setPathTo(new Line2D.Double(sx,sy,sw,sh));
	setPaint(null);
	setStrokePaint((Paint)ois.readObject());
	setBounds((PBounds)ois.readObject());
	setStroke(Util.readBasicStroke(ois));
    }

}
