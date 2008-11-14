/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.hp.hpl.guess.piccolo.util;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

import java.io.Serializable;

import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * <b>PFixedWidthStroke</b> is the same as {@link java.awt.BasicStroke} except
 * that PFixedWidthStroke has a fixed width on the screen so that even when the
 * canvas view is zooming its width stays the same in canvas coordinates. Note
 * that this stroke draws in the inside of the stroked shape, instead of the
 * normal draw on center behavior.
 * <P>
 * 
 * @see edu.umd.cs.piccolo.nodes.PPath
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PFixedWidthStroke implements Stroke, Serializable {

	private static final long serialVersionUID = -7852350520291227780L;
	
    final static int JOIN_MITER = BasicStroke.JOIN_MITER;
    final static int JOIN_ROUND = BasicStroke.JOIN_ROUND;
    final static int JOIN_BEVEL = BasicStroke.JOIN_BEVEL;
    final static int CAP_BUTT = BasicStroke.CAP_BUTT;
    final static int CAP_ROUND = BasicStroke.CAP_ROUND;
    final static int CAP_SQUARE = BasicStroke.CAP_SQUARE;

    private float width;
    private int join;
    private int cap;
    private float miterlimit;
    private float dash[];
    private float dash_phase;
    
    private BasicStroke bs = null;
    
    public PFixedWidthStroke() {
        this(1.0f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f);
    }

    public PFixedWidthStroke(float width) {
        this(width, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f);
    }

    public PFixedWidthStroke(float width, int cap, int join) {
        this(width, cap, join, 10.0f, null, 0.0f);
    }

    public PFixedWidthStroke(float width, int cap, int join, float miterlimit) {
        this(width, cap, join, miterlimit, null, 0.0f);
    }

    public PFixedWidthStroke(float width, int cap, int join, float miterlimit, float dash[], float dash_phase) {
        if (width < 0.0f) {
            throw new IllegalArgumentException("negative width");
        }
        if (cap != CAP_BUTT && cap != CAP_ROUND && cap != CAP_SQUARE) {
            throw new IllegalArgumentException("illegal end cap value");
        }
        if (join == JOIN_MITER) {
            if (miterlimit < 1.0f) {
                throw new IllegalArgumentException("miter limit < 1");
            }
        }
        else if (join != JOIN_ROUND && join != JOIN_BEVEL) {
            throw new IllegalArgumentException("illegal line join value");
        }
        if (dash != null) {
            if (dash_phase < 0.0f) {
                throw new IllegalArgumentException("negative dash phase");
            }
            boolean allzero = true;
            for (int i = 0; i < dash.length; i++) {
                float d = dash[i];
                if (d > 0.0) {
                    allzero = false;
                }
                else if (d < 0.0) {
                    throw new IllegalArgumentException("negative dash length");
                }
            }

            if (allzero) {
                throw new IllegalArgumentException("dash lengths all zero");
            }
        }
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterlimit = miterlimit;
        if (dash != null) {
            this.dash = (float[]) dash.clone();
        }
        this.dash_phase = dash_phase;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Shape createStrokedShape(Shape s) {
        float fixedScale = 1.0f;

        if (PDebug.getProcessingOutput()) {
            if (PPaintContext.CURRENT_PAINT_CONTEXT != null) {
                fixedScale = 1.0f / (float) PPaintContext.CURRENT_PAINT_CONTEXT.getScale();
            }
        } else {
            if (PPickPath.CURRENT_PICK_PATH != null) {
                fixedScale = 1.0f / (float) PPickPath.CURRENT_PICK_PATH.getScale();
            }
        }
        
        
        float fixedWidth = width * fixedScale;

        bs = new BasicStroke(fixedWidth);
        return bs.createStrokedShape(s);
    }


    public boolean equals(Object obj) {
        if (!(obj instanceof PFixedWidthStroke)) {
            return false;
        }

        PFixedWidthStroke bs = (PFixedWidthStroke) obj;
        if (width != bs.width) {
            return false;
        }

        if (join != bs.join) {
            return false;
        }

        if (cap != bs.cap) {
            return false;
        }

        if (miterlimit != bs.miterlimit) {
            return false;
        }

        if (dash != null) {
            if (dash_phase != bs.dash_phase) {
                return false;
            }

            if (!java.util.Arrays.equals(dash, bs.dash)) {
                return false;
            }
        }
        else if (bs.dash != null) {
            return false;
        }

        return true;
    }

    public float[] getDashArray() {
        if (dash == null) {
            return null;
        }

        return (float[]) dash.clone();
    }

    public float getDashPhase() {
        return dash_phase;
    }

    public int getEndCap() {
        return cap;
    }

    public int getLineJoin() {
        return join;
    }

    public float getLineWidth() {
        return width;
    }

    public float getMiterLimit() {
        return miterlimit;
    }

    public int hashCode() {
        int hash = Float.floatToIntBits(width);
        hash = hash * 31 + join;
        hash = hash * 31 + cap;
        hash = hash * 31 + Float.floatToIntBits(miterlimit);
        if (dash != null) {
            hash = hash * 31 + Float.floatToIntBits(dash_phase);
            for (int i = 0; i < dash.length; i++) {
                hash = hash * 31 + Float.floatToIntBits(dash[i]);
            }
        }
        return hash;
    }
}
