package com.hp.hpl.guess.piccolo.util;

import java.awt.Paint;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

public class SerRoundRectangle extends PPath implements SerInterface {
    
	private static final long serialVersionUID = -6003180267424732929L;
	private double sx,sy,sw,sh,saw,sah;

    public SerRoundRectangle() {
	super();
    }
    
    public SerRoundRectangle(double x, 
			     double y,
			     double w,
			     double h,
			     double aw,
			     double ah) {
	setPathTo(new RoundRectangle2D.Double(x,y,w,h,aw,ah));
	sx = x;
	sy = y;
	sw = w;
	sh = h;
	saw = aw;
	sah = ah;
    }

    public void writeObject(java.io.ObjectOutputStream ois)
	throws IOException {
	ois.writeDouble(sx);
	ois.writeDouble(sy);
	ois.writeDouble(sw);
	ois.writeDouble(sh);
	ois.writeDouble(saw);
	ois.writeDouble(sah);
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
	saw = ois.readDouble();
	sah = ois.readDouble();
	setPathTo(new RoundRectangle2D.Double(sx,sy,sw,sh,saw,sah));
	setPaint(null);
	setStrokePaint((Paint)ois.readObject());
	setBounds((PBounds)ois.readObject());
	setStroke(Util.readBasicStroke(ois));
    }

}
