package com.hp.hpl.guess.jfreechart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.python.core.PyFloat;
import org.python.core.PyInstance;
import org.python.core.PyInteger;
import org.python.core.PySequence;

import com.hp.hpl.guess.Edge;
import com.hp.hpl.guess.GraphElement;
import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.ui.Colors;
import com.hp.hpl.guess.ui.Dockable;
import com.hp.hpl.guess.ui.EdgeEditorPopup;
import com.hp.hpl.guess.ui.EditorPopup;
import com.hp.hpl.guess.ui.GraphElementEditorPopup;
import com.hp.hpl.guess.ui.GraphEvents;
import com.hp.hpl.guess.ui.GraphMouseListener;
import com.hp.hpl.guess.ui.GuessJFrame;
import com.hp.hpl.guess.ui.MainUIWindow;
import com.hp.hpl.guess.ui.NodeEditorPopup;
import com.hp.hpl.guess.ui.VisFactory;

public class GHistoChartFrame extends JPanel 
    implements Dockable, GraphMouseListener {

	private static final long serialVersionUID = 3383661752877842066L;

	protected HashMap<Integer, Collection<GraphElement>> idToGroup = new HashMap<Integer, Collection<GraphElement>>();

    protected HashMap<GraphElement, Integer> elemToSection = new HashMap<GraphElement, Integer>();

    private boolean docking = true;

    private boolean legend = false;

    public GHistoChartFrame(String title, PySequence groups, 
			    PySequence vals, PySequence groupNames,
			    PySequence colors) {
	this(title,groups,vals,groupNames,colors,true,false);
    }

    public GHistoChartFrame(String title, PySequence groups, 
			    PySequence vals, PySequence groupNames,
			    PySequence colors, boolean docking, 
			    boolean legend) {
	
	this.docking = docking;
	this.legend = legend;

	//CategoryTableXYDataset dset = new CategoryTableXYDataset();
	DefaultCategoryDataset dset = new DefaultCategoryDataset();

	Color[] clrs = null;
	if (colors != null) {
	    clrs = new Color[groups.size()];
	}

	for (int i = 0 ; i < groups.size() ; i++) {
	    Object group = groups.__finditem__(i);

	    Collection<GraphElement> grp = ((PySequence)group).findGraphElements();

	    if (docking)
		idToGroup.put(new Integer(i),grp);

	    Object pyval = (Object)vals.__finditem__(i);
	    double val = 0;
	    if (pyval instanceof PyInteger) {
		val = (double)(((PyInteger)pyval).getValue());
	    } else if (pyval instanceof PyFloat) {
		val = (double)(((PyFloat)pyval).getValue());
	    } else if (pyval instanceof PyInstance) {
		Double tval = 
		    (Double)((PyInstance)vals.__finditem__(i)).__tojava__(Double.class);
		val = tval.doubleValue();
	    }
	    String name = groupNames.__finditem__(i).toString();
	    //System.out.println(i + " " + name);
	    dset.addValue(val,name,new Integer(1));
	    if (colors != null) {
		String clr = colors.__finditem__(i).toString();
		clrs[i] = Colors.getColor(clr,Color.blue);
	    }

	    if (docking) {
		Iterator<GraphElement> it = grp.iterator();
		while(it.hasNext()) {
		    GraphElement ge = (GraphElement)it.next();
		    elemToSection.put(ge,new Integer(i));
		}
	    }
	}
	buildChart(title,"Histo",dset,clrs);
    }

    String title = null;

    public void buildChart(String title, 
			   String xlabel,
			   DefaultCategoryDataset dataset,
			   Color[] clr) {

	setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.weighty = 1;
	c.weightx = 1;
	c.gridy = 1;
	c.gridx = 1;

	this.title = title;

	chart = 
	    ChartFactory.createBarChart(title,
					xlabel,
					"#",
					dataset,
					PlotOrientation.VERTICAL,
					legend,
					true,
					false);
	
	plot = (CategoryPlot)chart.getPlot();
	rend = (CategoryItemRenderer)plot.getRenderer();
	if (clr != null) {
	    for (int i = 0 ; i < clr.length ; i++) {
		rend.setSeriesPaint(i,clr[i]);
	    }
	}

	cp = new ChartPanel(chart);
	if (docking)
	    cp.addChartMouseListener(new InternalMouseListener(this));
	cp.setPreferredSize(new Dimension(600,250));
	add(cp,c);
	if (docking) {
	    Guess.getMainUIWindow().dock(this);
	    GraphEvents.getGraphEvents().addGraphMouseListener(this);
	}
    }

    private JFreeChart chart = null;
    
    private ChartPanel cp = null;

    public void saveJPEG(String filename) throws IOException {
	saveChartAsJPEG(new File(filename),(float).9,
			(int)cp.getPreferredSize().getWidth(),
			(int)cp.getPreferredSize().getHeight());
    }

    public void saveJPEG(String filename, float quality) 
	throws IOException {
	saveChartAsJPEG(new File(filename),quality,
			(int)cp.getPreferredSize().getWidth(),
			(int)cp.getPreferredSize().getHeight());
    }

    public void saveJPEG(String filename, float quality, 
			 int width, int height) throws IOException {
	saveChartAsJPEG(new File(filename),quality,width,height);
    }

    private void saveChartAsJPEG(File fname, float quality, 
				 int width, int height) throws IOException {
	ChartUtilities.saveChartAsJPEG(fname,quality,chart,width,height);
    }

    public int getDirectionPreference() {
	return(MainUIWindow.HORIZONTAL_DOCK);
    }

    public void opening(boolean state) {
    }

    public void attaching(boolean state) {
    }

    private GuessJFrame myParent = null;

    public GuessJFrame getWindow() {
	return(myParent);
    }

    public void setWindow(GuessJFrame gjf) {
	myParent = gjf;
    }

    public String getTitle() {
	if (title == null) 
	    return("Charts");

	return(title);
    }

    //public Dimension getPreferredSize() {
    //return(new Dimension(600,300));
    //}

    class InternalMouseListener implements ChartMouseListener {


	private GHistoChartFrame owner = null;

	private Collection<GraphElement> prev = null;

	private JPopupMenu jpm = null;

	public InternalMouseListener(GHistoChartFrame own) {
	    this.owner = own;
	}

	public void chartMouseClicked(ChartMouseEvent event) {
	    MouseEvent ev = event.getTrigger();
	    if (ev.getButton() != MouseEvent.BUTTON1)
		return;

	    ChartEntity ce = event.getEntity();

	    if (ce == null)
		return;

	    if (ce instanceof CategoryItemEntity) {
		int ser = ((CategoryItemEntity)ce).getSeries();
		prev = (Collection<GraphElement>)owner.idToGroup.get(new Integer(ser));
		if (prev != null) {
		    VisFactory.getFactory().getDisplay().center(prev);
		}
	    }
	}

	public void chartMouseMoved(ChartMouseEvent event) {
	    if (jpm == null)
		jpm = owner.cp.getPopupMenu();

	    ChartEntity ce = event.getEntity();

	    if (prev != null) {
		Iterator<GraphElement> it = prev.iterator();
		while (it.hasNext()) {
		    GraphElement ge = (GraphElement)it.next();
		    GraphEvents.mouseLeave(ge);
		}
		prev = null;
		owner.cp.setPopupMenu(jpm);
	    }
	    
	    if (ce == null) {
		return;
	    }


	    if (ce instanceof CategoryItemEntity) {
		int ser = ((CategoryItemEntity)ce).getSeries();
		prev = (Collection<GraphElement>)owner.idToGroup.get(new Integer(ser));
		if (prev != null) {
		    boolean nodes = false;
		    boolean edges = false;
		    Iterator<GraphElement> it = prev.iterator();
		    while (it.hasNext()) {
			GraphElement ge = (GraphElement)it.next();
			if (ge instanceof Node)
			    nodes = true;
			else if (ge instanceof Edge)
			    edges = true;
			GraphEvents.mouseEnter(ge);
		    }
		    EditorPopup ep = null;
		    if (nodes && edges) {
			ep = GraphElementEditorPopup.getPopup();
		    } else if (nodes) {
			ep = NodeEditorPopup.getPopup();
		    } else if (edges) {
			ep = EdgeEditorPopup.getPopup();
		    }
		    if (ep != null) {
			ep.cacheContent(prev,prev);
			owner.cp.setPopupMenu(ep);
		    }       
		}
	    }
	}
    }

    private int section = -1;
    
    private Paint prev = null;

    private CategoryPlot plot = null;

    private CategoryItemRenderer rend = null;

    public synchronized void mouseEnterNode(Node n) {
	annotate(n);
    }
    
    public synchronized void mouseEnterEdge(Edge e) {
	annotate(e);
    }
    
    public synchronized void mouseLeaveNode(Node n) {
	if (elemToSection.containsKey(n)) {
	    if (section != -1)
		rend.setSeriesPaint(section,prev);
	    section = -1;
	    prev = null;
	}
    }
	
    public synchronized void mouseLeaveEdge(Edge e) {
	if (elemToSection.containsKey(e)) {
	    if (section != -1)
		rend.setSeriesPaint(section,prev);
	    section = -1;
	    prev = null;
	}
    }
    
    public synchronized void annotate(GraphElement ge) {
	Integer st = (Integer)elemToSection.get(ge);
	if (st != null) {
	    if (section != -1)
		rend.setSeriesPaint(section,prev);
	    prev = rend.getSeriesPaint(st.intValue());
	    section = st.intValue();
	    rend.setSeriesPaint(section,Color.yellow);
	}
    }
}
