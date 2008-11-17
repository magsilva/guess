package com.hp.hpl.guess.jung;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.hp.hpl.guess.ui.FrameListener;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class JungVisFrame extends GraphZoomScrollPane
    implements FrameListener {
    
	private static final long serialVersionUID = 5200270267094103051L;

	public JungVisFrame(VisualizationViewer v) {
	super(v);
    }
    
    public void center() {
    }
    
    public void center(Object o) {
    }

    public void setFrozen(boolean state) {
    }
   
    public void exportGIF(String filename) {
    }
    
    public void exportJPG(String filename) {
    }
    
    public void exportPDF(String filename) {
    }
    
    public void exportPS(String filename) {
    }
    
    public void exportEPS(String filename) {
    }

    public void exportSVG(String filename) {
    }

    public void exportSWF(String filename) {
    }
    
    public void exportJAVA(String filename) {
    }

    public void exportCGM(String filename) {
    }

    public void exportEMF(String filename) {
    }

    public void exportPNG(String filename) {
    }

    public Color getDisplayBackground() {
	return(null);
    }

    public void setDisplayBackground(Color c) {
    }

    public void setBackgroundImage(String filename) {
    }


    public void setBackgroundImage(String filename, double x, double y) {
    }

    public void removeBackgroundImage() {
    }

    public void setQuality(int requestedQuality) {	
    }
    
    public BufferedImage getFullImage() {
	return(null);
    }
}
