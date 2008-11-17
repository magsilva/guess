package com.hp.hpl.guess.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import com.hp.hpl.guess.piccolo.GFrame;
import com.hp.hpl.guess.piccolo.util.PFixedWidthStroke;
import com.hp.hpl.guess.*;


/**
 * @pyobj DrawWindow
 */
public class DrawWindow extends JPanel implements Dockable, ActionListener {

	private static final long serialVersionUID = 1L;

	private static DrawWindow singleton = null;

    public static final int RECTANGLE = 1;
    public static final int ELLIPSE = 2;
    public static final int RRECTANGLE = 3;
    public static final int TEXT = 4;
    public static final int SELECT = 5;
    public static final int LINE = 6;

    private static int tool = RECTANGLE;

    private static Color myColor = Color.gray;
    
    private static Stroke myStroke = null;

    private static int myArrow = 0;

    private static SimpleButton[] sbList = new SimpleButton[6];

    private JComboBox arrowJCB = null;
    
    private JComboBox styleJCB = null;

    private JComboBox widthJCB = null;

    private JButton colorBut = null;

    public static void setTool(int tool) {
	DrawWindow.tool = tool;
    }

    public static int getTool() {
	return(tool);
    }

    public static Color getDrawColor() {
	return(myColor);
    }

    public static Stroke getStroke() {
	return(myStroke);
    }

    public static int getArrow() {
	return(myArrow);
    }

    public static DrawWindow getDrawWindow() {
	if (singleton == null) {
	    singleton = new DrawWindow("Draw Window");
	}
	return(singleton);
    }

    /**
     * @pyexport drawwindow
     */
    public static void create() {
	Guess.getMainUIWindow().dock(getDrawWindow());
    }

    public static void uncreate() {
	Guess.getMainUIWindow().close(getDrawWindow());
    }

    public int getDirectionPreference() {
	return(MainUIWindow.VERTICAL_DOCK);
    }

    public void opening(boolean state) {
    }

    public void attaching(boolean state) {
    }

    public String getTitle() {
	return("Draw Window");
    }

    private GuessJFrame myParent = null;
  
    public Dimension getPreferredSize() {
	return(new Dimension(80,200));
    }

    public GuessJFrame getWindow() {
	return(myParent);
    }

    public void setWindow(GuessJFrame gjf) {
	myParent = gjf;
    }

    private static final float[] dashPattern = { 30, 10, 10, 10 };

    public void actionPerformed(ActionEvent e) {
	myArrow = arrowJCB.getSelectedIndex();
	double width = (double)(widthJCB.getSelectedIndex() + 1);
	int style = styleJCB.getSelectedIndex();
	if (style == 0) {
	    myStroke = new BasicStroke((float)width);
	} else {
	    myStroke = new BasicStroke((float)width, BasicStroke.CAP_BUTT,
				       BasicStroke.JOIN_MITER, 10,
				       dashPattern, 0);
	}
    }

        
    private DrawWindow(String title) {

    	if (Guess.getZooming()==Guess.ZOOMING_SPACE) {
    		myStroke = new PFixedWidthStroke(1.0f);
    	} else {
    		myStroke = new BasicStroke(1.0f);
    	}

	setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();

	setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
	setOpaque(false);
	
	JPanel stuff = new JPanel();
	stuff.setOpaque(false);
	
	stuff.setLayout(new GridLayout(2,1));
	MouseAdapter ma = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    for (int i = 0 ; i < sbList.length ; i++) {
			//System.out.println(i + " " + sbList[i]);
			//System.out.println(sbList[i]);
			sbList[i].click(false);
		    }
		    SimpleButton sb = (SimpleButton)e.getSource();
		    sb.click(true);
		    setTool(sb.bType);
		    FrameListener fl = 
			VisFactory.getFactory().getDisplay();
		    if (fl instanceof GFrame) {
			if (sb.bType == SELECT) {
			    ((GFrame)fl).switchHandler(GFrame.DRAW_EDIT_MODE);
			    styleJCB.setEnabled(false);
			    widthJCB.setEnabled(false);
			    arrowJCB.setEnabled(false);
			    colorBut.setEnabled(false);
			} else {
			    ((GFrame)fl).switchHandler(GFrame.DRAW_CREATE_MODE);
			    styleJCB.setEnabled(true);
			    widthJCB.setEnabled(true);

			    if (sb.bType == LINE) {
				arrowJCB.setEnabled(true);
			    } else {
				arrowJCB.setEnabled(false);
			    }

			    colorBut.setEnabled(true);
			}
		    }
		}
	    };

	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new GridLayout(3,2));
	buttonPanel.setOpaque(false);
	
	SimpleButton sb = 
	    new SimpleButton("drawsel.gif",SELECT,"Select objects");
	sb.addMouseListener(ma);
	buttonPanel.add(sb);
	sbList[0] = sb;

	sb = new SimpleButton("s1.gif",RECTANGLE,"Draw a square");
	sb.addMouseListener(ma);
	buttonPanel.add(sb);
	sbList[1] = sb;
	sb.click(true);

	sb = new SimpleButton("e1.gif",ELLIPSE,"Draw an ellipse");
	sb.addMouseListener(ma);
	buttonPanel.add(sb);
	sbList[2] = sb;

	sb = new SimpleButton("rr1.gif",RRECTANGLE,"Draw a rounded rectangle");
	sb.addMouseListener(ma);
	buttonPanel.add(sb);
	sbList[3] = sb;

	sb = new SimpleButton("line.gif",LINE,"Draw a line");
	sb.addMouseListener(ma);
	buttonPanel.add(sb);
	sbList[4] = sb;

	sb = new SimpleButton("a.gif",TEXT,"Add Text");
	sb.addMouseListener(ma);
	buttonPanel.add(sb);
	sbList[5] = sb;

	stuff.add(buttonPanel,c);

	JPanel selectionPanel = new JPanel();
	selectionPanel.setLayout(new GridLayout(4,1));
	selectionPanel.setOpaque(false);
	
	styleJCB = new JComboBox();
	styleJCB.addItem(new ImageIcon(getClass().getResource("/images/linesolid.gif")));
	styleJCB.addItem(new ImageIcon(getClass().getResource("/images/linedash.gif")));
	selectionPanel.add(styleJCB);
	styleJCB.setToolTipText("Pick a line style");
	styleJCB.addActionListener(this);

	arrowJCB = new JComboBox();
	arrowJCB.addItem(new ImageIcon(getClass().getResource("/images/linenoarrow.gif")));
	arrowJCB.addItem(new ImageIcon(getClass().getResource("/images/line1arrow.gif")));
	arrowJCB.addItem(new ImageIcon(getClass().getResource("/images/line2arrow.gif")));
	arrowJCB.setToolTipText("Pick an arrow style");
	selectionPanel.add(arrowJCB);
	arrowJCB.setEnabled(false);
	arrowJCB.addActionListener(this);

	widthJCB = new JComboBox();
	widthJCB.addItem("1 px");
	widthJCB.addItem("2 px");
	widthJCB.addItem("3 px");
	widthJCB.addItem("4 px");
	widthJCB.addItem("5 px");
	widthJCB.addItem("6 px");
	widthJCB.setToolTipText("Pick a line width");
	selectionPanel.add(widthJCB);
	widthJCB.addActionListener(this);

	colorBut = new JButton(" ");
	colorBut.setBackground(myColor);
	colorBut.setToolTipText("Pick a color");
	selectionPanel.add(colorBut);
	colorBut.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    JButton cb = (JButton)e.getSource();
		    Color newCol = 
			JColorChooser.showDialog(null,
						 "Please pick a color - GUESS",
						 cb.getBackground());
		    cb.setBackground(newCol);
		    myColor = newCol;
		}
	    });

	stuff.add(selectionPanel);
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weighty = 0;
	c.weightx = 0;
	c.gridx = 0;
	c.gridy = 0;
	add(stuff,c);
	c.gridy = 2;
	c.fill = GridBagConstraints.BOTH;
	//c.weighty = 1;
	c.weighty = 1;
	//add(new JPanel(),c);
    }

    public Rectangle getDefaultFrameBounds() {
	return new Rectangle(50, 50, 80, 200);
    }
}
