package com.hp.hpl.guess.prefuse;

import com.hp.hpl.guess.ui.FrameListener;
import com.hp.hpl.guess.freehep.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.force.ForceSimulator;
import prefuse.util.io.IOLib;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PrefuseDisplay extends Display implements FrameListener {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
      
    protected Visualization m_vis = null;;
    protected ForceSimulator fsim = null;
    protected GraphDistanceFilter filter = null;

    private Graph m_graph = null;
    protected int hops = 30;

    public PrefuseDisplay(Graph m_graph) {
        // create a new, empty visualization for our data
        m_vis = new Visualization();
	this.m_graph = m_graph;
        setVisualization(m_vis);
        setSize(700,700);
        pan(350, 350);
        setForeground(Color.GRAY);
        setBackground(Color.WHITE);
        
        // main display controls
        addControlListener(new FocusControl(1));
        addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new ZoomToFitControl());
        addControlListener(new NeighborHighlightControl());

        // overview display
//        Display overview = new Display(vis);
//        overview.setSize(290,290);
//        overview.addItemBoundsListener(new FitOverviewListener());
        
        setForeground(Color.GRAY);
        setBackground(Color.WHITE);

    }

    public void runNow() {	
	System.err.println("running...");
        // --------------------------------------------------------------------
        // set up the renderers
        
        LabelRenderer tr = new LabelRenderer();
        tr.setRoundedCorner(8, 8);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // --------------------------------------------------------------------
        // register the data with a visualization
 

        // adds graph to visualization and sets renderer label field
        setGraph(m_graph, "label");

        // fix selected focus nodes
        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS); 
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
            {
                for ( int i=0; i<rem.length; ++i )
                    ((VisualItem)rem[i]).setFixed(false);
                for ( int i=0; i<add.length; ++i ) {
                    ((VisualItem)add[i]).setFixed(false);
                    ((VisualItem)add[i]).setFixed(true);
                }
                if ( ts.getTupleCount() == 0 ) {
                    ts.addTuple(rem[0]);
                    ((VisualItem)rem[0]).setFixed(false);
                }
                m_vis.run("draw");
            }
        });
    

       
        // --------------------------------------------------------------------
        // create actions to process the visual data

        filter = new GraphDistanceFilter(graph, hops);

        ColorAction fill = new ColorAction(nodes, 
                VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
        fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));
        
        ActionList draw = new ActionList();
        //draw.add(filter);
        draw.add(fill);
        draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));
        
        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(new ForceDirectedLayout(graph));
        animate.add(fill);
        animate.add(new RepaintAction());
        
        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        m_vis.putAction("draw", draw);
        m_vis.putAction("layout", animate);

        m_vis.runAfter("draw", "layout");
        
        
        // --------------------------------------------------------------------
        // set up a display to show the visualization
        
        
        // --------------------------------------------------------------------        
        // launch the visualization
        
        // create a panel for editing force values
        fsim = ((ForceDirectedLayout)animate.get(0)).getForceSimulator();

        // now we run our action list
        m_vis.run("draw");
        
        //add(split);
    }
    
    public void setGraph(Graph g, String label) {
        // update labeling
        DefaultRendererFactory drf = (DefaultRendererFactory)
	    m_vis.getRendererFactory();
        ((LabelRenderer)drf.getDefaultRenderer()).setTextField(label);
        
        // update graph
        m_vis.removeGroup(graph);
        VisualGraph vg = m_vis.addGraph(graph, g);
        m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);
        VisualItem f = (VisualItem)vg.getNode(0);
        m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
        f.setFixed(false);
    }
    
    public void center() {
    }

    public void center(Object o) {
    }


    public void setFrozen(boolean state){}

    public void exportGIF(String filename) {
	HEPWriter.export(filename,this,HEPWriter.GIF);
    }

    public void exportJPG(String filename){
	HEPWriter.export(filename,this,HEPWriter.JPG);
    }

    public void exportPDF(String filename){
	HEPWriter.export(filename,this,HEPWriter.PDF);
    }

    public void exportPS(String filename){
	HEPWriter.export(filename,this,HEPWriter.PS);
    }

    public void exportEPS(String filename){
	HEPWriter.export(filename,this,HEPWriter.EPS);
    }

    public void exportSVG(String filename){
	HEPWriter.export(filename,this,HEPWriter.SVG);
    }

    public void exportSWF(String filename){
	HEPWriter.export(filename,this,HEPWriter.SWF);
    }

    public void exportJAVA(String filename){
	HEPWriter.export(filename,this,HEPWriter.JAVA);
    }

    public void exportCGM(String filename){
	HEPWriter.export(filename,this,HEPWriter.CGM);
    }

    public void exportEMF(String filename){
	HEPWriter.export(filename,this,HEPWriter.EMF);
    }

    public void exportPNG(String filename){
	HEPWriter.export(filename,this,HEPWriter.PNG);
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
