package com.hp.hpl.guess.util;

import java.util.HashSet;

public class AnnoHashSet extends HashSet {

	private static final long serialVersionUID = 2585083528203824531L;
	
	public String annotation = null;

    public AnnoHashSet() {
    }

    public AnnoHashSet(String annotation) {
	this.annotation = annotation;
    }
}
