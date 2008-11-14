package com.hp.hpl.guess.piccolo;

import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.nodes.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.guess.ui.VisFactory;
import com.hp.hpl.guess.Guess;

public class LabelText extends PText {

	private static final long serialVersionUID = 6683597768233421276L;
	private GuessPNode owner = null;
	private float prevScale = -1;
	
	private Set<String> fieldSet = new HashSet<String>();
	
	
	public LabelText(GuessPNode owner) {
		this.owner = owner;
	}

	public LabelText() {
	}
	

	public void addField(String aField) {
		fieldSet.add(aField);
	}
	
	public void removeField(String aField) {
		fieldSet.remove(aField);
	}
	
	private String getTextFromFields() {
		String labelText = "";
		Iterator<String> fieldIterator = fieldSet.iterator();
	    while ( fieldIterator.hasNext() ){
	    	String nextField = fieldIterator.next();
	    	labelText = labelText + owner.getOwner().__getattr__(nextField) + "\n";
	    }
		
		return labelText;
	}
	
	
	public void recalculateLocation() {
		// Set X
		double w1 = owner.getFrame().getCamera().getViewBounds().getX() - getX() - getWidth();
		double w2 = owner.getFrame().getCamera().getViewBounds().getWidth();
		
		w1 = -1*w1;
		
		if (w1 > w2) {
			setX(getX() - (w1-w2));
		}
		
		// Set Y
		double h1 = owner.getFrame().getCamera().getViewBounds().getY() -20 - getY() - getHeight();
		double h2 = owner.getFrame().getCamera().getViewBounds().getHeight();
		
		h1 = -1*h1;
		
		if (h1 > h2) {
			setY(getY() - (h1-h2));
		}
	}
	
	public void setLocation(Point2D location) {
		setX(location.getX());
		setY(location.getY() - 20);
	}

	public void paint(PPaintContext apc) {
		setText(getTextFromFields());
		
		float scaling = (float) (1 / ((GFrame) VisFactory.getFactory()
				.getDisplay()).getGCamera().getViewScale());

		if (scaling != prevScale) {
			Font f = getFont();
			f = f.deriveFont((float) (12 * scaling));
			prevScale = scaling;
			if (scaling <= .3) {
				scaling = (float) .3;
			}
			// System.out.println(scaling);
			if (owner != null) {
				setX(owner.getX() + owner.getWidth() + 1 * scaling);
				setY(owner.getY() + owner.getHeight());
			}
			if (Guess.getDefaultFont() != null) {
				if (!f.getFamily().equals(Guess.getDefaultFont())) {
					f = new Font(Guess.getDefaultFont(), f.getStyle(), f
							.getSize());
				}
			}
			setFont(f);
		}
		recalculateLocation();
		super.paint(apc);
	}
}
