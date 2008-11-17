package com.hp.hpl.guess.piccolo;

import edu.umd.cs.piccolo.nodes.PPath;

public class ConvexHullLegendNode extends PPath {

	private static final long serialVersionUID = 3494764565347586675L;

	public ConvexHullLegendNode(ConvexHullNode chn) {
	float[] x3Points = chn.getX3();
	float[] y3Points = chn.getY3();
	if ((x3Points != null) && (x3Points.length > 0)) {
	    setPathToPolyline(x3Points,y3Points);
	    setPaint(chn.getPaint());
	    setStrokePaint(null);
	}
	setBounds(0,0,15,15);
    }
}
