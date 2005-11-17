package com.hp.hpl.guess.jung;

import edu.uci.ics.jung.visualization.*;
import com.hp.hpl.guess.ui.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JungVisFrame extends VisualizationViewer 
    implements FrameListener {

    public JungVisFrame(Layout layout, Renderer renderer) {
	super(layout,renderer);
    }

    public JungVisFrame(Layout layout, 
			Renderer renderer, 
			Dimension preferredSize) {
	super(layout,renderer,preferredSize);
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

    public BufferedImage getFullImage() {
	return(null);
    }
}
