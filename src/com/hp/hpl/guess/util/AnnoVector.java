package com.hp.hpl.guess.util;

import java.util.Vector;

public class AnnoVector extends Vector {

	private static final long serialVersionUID = -6150979137610718573L;
	public String annotation = "";

    public AnnoVector(String annotation) {
	super();
	this.annotation = annotation;
    }
}
