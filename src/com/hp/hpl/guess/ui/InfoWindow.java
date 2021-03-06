package com.hp.hpl.guess.ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelEvent;

import com.hp.hpl.guess.action.GActionManager;
import com.hp.hpl.guess.action.GStateAction;
import com.hp.hpl.guess.*;

import java.sql.Types;

/**
 * @pyobj InfoWindow
 */
public class InfoWindow extends JPanel implements Dockable, GraphMouseListener {

	private static final long serialVersionUID = -8315664637762043212L;

	private static InfoWindow singleton = null;

	private GuessTableModel gtm = null;

	private JLabel jl = new JLabel("Element Properties",
			JLabel.LEFT);
	protected boolean visible = false;

	public void mouseEnterNode(Node n) {
		details(n);
	}

	public void mouseEnterEdge(Edge e) {
		details(e);
	}

	public void mouseLeaveNode(Node n) {
	}

	public void mouseLeaveEdge(Edge e) {
	}

	public static boolean isIWVisible() {
		if (singleton == null)
			return(false);

		return(singleton.visible);
	}

	public static InfoWindow getInfoWindow() {
		if (singleton == null) {
			singleton = new InfoWindow("Information Window");
		}
		return(singleton);
	}

	/**
	 * @pyexport infowindow
	 */
	public static void create() {
		if ((getInfoWindow().getWindow() == null) || 
				(!getInfoWindow().getWindow().isVisible())) {
			Guess.getMainUIWindow().dock(getInfoWindow());
		}
	}

	public static void details(GraphElement n) {
		getInfoWindow().updateInt(n);
	}

	private void updateInt(GraphElement n) {
		if (visible) {
			if (n instanceof Node) {
				gtm.details((Node)n);
			} else {
				gtm.details((Edge)n);
			}
		}
	}


	public Dimension getPreferredSize() {
		return(new Dimension(200,600));
	}

	public int getDirectionPreference() {
		return(MainUIWindow.VERTICAL_DOCK);
	}

	public void opening(boolean state) {
		visible = state;
	}

	public void attaching(boolean state) {
	}

	public String getTitle() {
		return("Information Window");
	}

	private GuessJFrame myParent = null;

	public GuessJFrame getWindow() {
		return(myParent);
	}

	public void setWindow(GuessJFrame gjf) {
		myParent = gjf;
	}

	private InfoWindow(String title) 
	{
		GraphEvents.getGraphEvents().addGraphMouseListener(this);

		setOpaque(false);

		gtm = new GuessTableModel();
		JTable table = new JTable(gtm);
		table.setBackground(Guess.getMainUIWindow().getBgColor());
		// removed for backwards compatibility
		//table.setFillsViewportHeight(true);

		JScrollPane scrollpane = new JScrollPane(table);

		scrollpane.setOpaque(false);
		scrollpane.setBorder(BorderFactory.createEmptyBorder(5,8,8,4));

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		jl.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		add(jl,c);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.gridy = 1;
		add(scrollpane,c);
		setBounds(getDefaultFrameBounds());
	}

	public Rectangle getDefaultFrameBounds() {
		return new Rectangle(50, 50, 300, 600);
	}

	class GuessTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 4935849351227106915L;

		private int nRowCount = 0;

		private Vector<String> nFields = new Vector<String>();

		private int eRowCount = 0;

		private Vector<String> eFields = new Vector<String>();

		private GraphElement lastSel = null;

		public int getColumnCount() { 
			return 2; 
		}

		public void details(Node n) {
			Schema schema = Guess.getGraph().getNodeSchema();
			int trc = schema.fieldCount();
			if (trc != nRowCount) {
				// schema has changed, kill the cache
				nRowCount = trc;
				nFields.clear();
				Enumeration<Field> en = schema.getFields();
				while(en.hasMoreElements()) {
					Field f = (Field)en.nextElement();
					nFields.add(f.getName());
				}
				Collections.sort(nFields);
				lastSel = null; // cause full change to fire
			}
			GraphElement temp = lastSel;
			lastSel = n;
			if (temp instanceof Node) {
				// node to node, only need to 
				// update second column
				fireTableChanged(new TableModelEvent(this,0,nRowCount,1,TableModelEvent.UPDATE));
			} else {
				fireTableDataChanged();
			}
		}

		public void details(Edge n) {
			Schema schema = Guess.getGraph().getEdgeSchema();
			int trc = schema.fieldCount();
			if (trc != eRowCount) {
				// schema has changed, kill the cache
				eRowCount = trc;
				eFields.clear();
				Enumeration<Field> en = schema.getFields();
				while(en.hasMoreElements()) {
					Field f = (Field)en.nextElement();
					eFields.add(f.getName());
				}
				Collections.sort(eFields);
				lastSel = null; // cause full change to fire
			}
			GraphElement temp = lastSel;
			lastSel = n;
			if (temp instanceof Edge) {
				// edge to edge, only need to 
				// update second column
				fireTableChanged(new TableModelEvent(this,0,eRowCount,1,TableModelEvent.UPDATE));
			} else {
				fireTableDataChanged();
			}
		}

		public int getRowCount() {
			if (lastSel == null) {
				return(0);
			} else if (lastSel instanceof Node) {
				return(nRowCount);
			} else {
				return(eRowCount);
			}
		}

		public Object getValueAt(int row, int col) { 
			if (lastSel == null) {
				return("");
			} else if (lastSel instanceof Node) {
				if (row <= nRowCount) {
					String fld = (String)nFields.elementAt(row);
					if (col == 0) {
						return(fld);
					} else if (col == 1) {
						return(lastSel.__getattr__(fld));
					}
				}
			} else {
				if (row <= eRowCount) {
					String fld = (String)eFields.elementAt(row);
					if (col == 0) {
						return(fld);
					} else if (col == 1) {
						return(lastSel.__getattr__(fld));
					}
				}
			}
			return("");
		}

		public String getColumnName(int col) {
			if (col == 0) {
				return("Field");
			} else {
				return("Value");
			}
		}

		public boolean isCellEditable(int row, int col) { 
			if (col == 0) {
				return false; 
			} else {
				return true;
			}
		}

		public void setValueAt(final Object value, final int row, final int col) {
			GStateAction infoWindowAction = new GStateAction() {
				public void actionContent() {
					String f = null;
					Field fld = null;
					if (lastSel != null) {
						if (lastSel instanceof Node) {
							f = (String)nFields.elementAt(row);
							fld = Guess.getGraph().getNodeSchema().getField(f);
						} else {
							f = (String)eFields.elementAt(row);
							fld = Guess.getGraph().getEdgeSchema().getField(f);
						}
						try {
							if ((fld.getSQLType() == Types.INTEGER) ||
									(fld.getSQLType() == Types.TINYINT) ||
									(fld.getSQLType() == Types.SMALLINT) ||
									(fld.getSQLType() == Types.BIGINT)) {
								lastSel.__setattr__(f,new Integer((String)value));
							} else if (fld.getSQLType() == Types.BOOLEAN) {
								lastSel.__setattr__(f,new Boolean((String)value));
							} else if (fld.isNumeric()) {
								lastSel.__setattr__(f,new Double((String)value));
							} else {
								lastSel.__setattr__(f,value);
							}
						} catch (Exception e) {
							ExceptionWindow.getExceptionWindow(e);
						}
						fireTableCellUpdated(row, col);
					}
				}
			};
			GActionManager.runAction(infoWindowAction, "Edit Properties");
		}

	}
}
