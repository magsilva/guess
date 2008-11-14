package com.hp.hpl.guess.ui;

import java.awt.event.*;

import javax.swing.*;
import java.util.*;
import com.hp.hpl.guess.*;

import java.sql.Types;

public class FieldModWindow extends JDialog {

	private static final long serialVersionUID = 6283357632411659577L;

	/**
	 * Labels
	 */
	private JLabel edgesFieldsLabel = new JLabel();
	private JLabel newValueEdgesLabel = new JLabel();
	private JLabel applyToEdgesLabel = new JLabel();
	private JLabel nodesFieldsLabel = new JLabel();
	private JLabel newValueNodeLabel = new JLabel();
	private JLabel applyToNodeLabel = new JLabel();

	/**
	 * Textfields, ComboBoxes and Lists
	 */

	private JTextField newValueEdgesText = new JTextField();
	private JTextField newValueNodesText = new JTextField();
	private JComboBox edgesFieldsComboBox = new JComboBox();
	private JComboBox nodesFieldsComboBox = new JComboBox();
	private DefaultListModel modelEdges = new DefaultListModel();
	private DefaultListModel modelNodes = new DefaultListModel();
	private JList applyEdgesList = new JList(modelEdges);
	private JList applyNodesList = new JList(modelNodes);
	


	/**
	 * Other GUI stuff
	 */
	private JPanel panelEdges = new JPanel();
	private JTabbedPane tabbedpane = new JTabbedPane();
	private JPanel panelNodes = new JPanel();
	private JScrollPane nodesItemsScrollpane = new JScrollPane();
	private JScrollPane edgesItemsScrollpane = new JScrollPane();

	/**
	 * Buttons
	 */
	private JButton selectAllNodesButton = new JButton();
	private JButton selectAllEdgesButton = new JButton();
	private JButton applyButton = new JButton();
	private JButton closeButton = new JButton();

	Collection<?> nodes = null;
	Collection<?> edges = null;

	boolean showingNodes = false;
	boolean showingEdges = false;

	Field selectedFieldEdges = null;
	Field selectedFieldNodes = null;

	private static FieldModWindow singleton = null;

	public static FieldModWindow getFieldModWindow() {
		if (singleton == null) {
			singleton = new FieldModWindow();
		} else {
			singleton.setNE(Guess.getGraph().getNodes(), Guess.getGraph()
					.getEdges());
		}
		return (singleton);
	}

	public static FieldModWindow getFieldModWindow(Collection<?> n, Collection<?> e) {
		if (singleton == null) {
			singleton = new FieldModWindow(n, e);
		} else {
			singleton.setNE(n, e);
		}
		return (singleton);
	}

	public void setNE(Collection<?> nodes, Collection<?> edges) {
		tabbedpane.setEnabledAt(0, true);
		tabbedpane.setEnabledAt(1, true);
		
		this.nodes = nodes;
		setNodesList();
		loadNodesData(nodes);
		if (nodes.size()==0) {
			tabbedpane.setSelectedIndex(0);
			tabbedpane.setEnabledAt(1, false);
		}
		
		this.edges = edges;
		setEdgesList();
		loadEdgesData(edges);
		if (edges.size()==0) {
			tabbedpane.setSelectedIndex(1);
			tabbedpane.setEnabledAt(0, false);
		}		
	}

	private FieldModWindow() {
		this(Guess.getGraph().getNodes(), Guess.getGraph().getEdges());
	}

	/**
	 * Default constructor
	 */
	private FieldModWindow(Collection<?> nodes, Collection<?> edges) {

		createPanel();

		setNE(nodes, edges);
		setSize(344, 307);
		setLocation(100, 100);

		setVisible(true);
	}

	public Object newValueEdgesData() {
		if ((selectedFieldEdges.getSQLType() == Types.INTEGER)
				|| (selectedFieldEdges.getSQLType() == Types.TINYINT)
				|| (selectedFieldEdges.getSQLType() == Types.SMALLINT)
				|| (selectedFieldEdges.getSQLType() == Types.BIGINT)) {
			return (new Integer((String) newValueEdgesText.getText()));
		} else if (selectedFieldEdges.getSQLType() == Types.BOOLEAN) {
			return (new Boolean((String) newValueEdgesText.getText()));
		} else if (selectedFieldEdges.isNumeric()) {
			return (new Double((String) newValueEdgesText.getText()));
		} else {
			return (newValueEdgesText.getText());
		}
	}

	public Object newValueNodesData() {
		if ((selectedFieldNodes.getSQLType() == Types.INTEGER)
				|| (selectedFieldNodes.getSQLType() == Types.TINYINT)
				|| (selectedFieldNodes.getSQLType() == Types.SMALLINT)
				|| (selectedFieldNodes.getSQLType() == Types.BIGINT)) {
			return (new Integer((String) newValueNodesText.getText()));
		} else if (selectedFieldNodes.getSQLType() == Types.BOOLEAN) {
			return (new Boolean((String) newValueNodesText.getText()));
		} else if (selectedFieldNodes.isNumeric()) {
			return (new Double((String) newValueNodesText.getText()));
		} else {
			return (newValueNodesText.getText());
		}
	}
	
	
	/**
	 * Set the value to all elements in the nodes list
	 */
	public void applyToNodesList() {
		int start = -1;
		int end = -1;
		try {
			start = applyNodesList.getSelectionModel().getMinSelectionIndex();
			end = applyNodesList.getSelectionModel().getMaxSelectionIndex();
			if (end >= modelNodes.getSize()) {
				end = modelNodes.getSize() - 1;
			}
			if ((start == -1) || (end == -1)) {
				return;
			}
			for (int i = start; i <= end; i++) {
				if (applyNodesList.isSelectedIndex(i)) {
					GraphElement o = (GraphElement) modelNodes.getElementAt(i);
					o.__setattr__(selectedFieldNodes.getName(), newValueNodesData());
				}
			}
			VisFactory.getFactory().getDisplay().repaint();
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(this, "Error setting value "
					+ e.toString() + " range: (" + start + "-" + end + ")",
					"Error", JOptionPane.ERROR_MESSAGE);

			ExceptionWindow.getExceptionWindow(e);
		}
	}
	
	/**
	 * Set the value to all elements in the edges list
	 */
	public void applyToEdgesList() {
		int start = -1;
		int end = -1;
		try {
			start = applyEdgesList.getSelectionModel().getMinSelectionIndex();
			end = applyEdgesList.getSelectionModel().getMaxSelectionIndex();
			if (end >= modelEdges.getSize()) {
				end = modelEdges.getSize() - 1;
			}
			if ((start == -1) || (end == -1)) {
				return;
			}
			for (int i = start; i <= end; i++) {
				if (applyEdgesList.isSelectedIndex(i)) {
					GraphElement o = (GraphElement) modelEdges.getElementAt(i);
					o.__setattr__(selectedFieldEdges.getName(), newValueEdgesData());
				}
			}
			VisFactory.getFactory().getDisplay().repaint();
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(this, "Error setting value "
					+ e.toString() + " range: (" + start + "-" + end + ")",
					"Error", JOptionPane.ERROR_MESSAGE);

			ExceptionWindow.getExceptionWindow(e);
		}
	}


	/**
	 * Fills the list of Nodes with data
	 * @param Nodes
	 */
	public void loadNodesData(Collection<?> c) {
		modelNodes.removeAllElements();
		Iterator<?> it = c.iterator();
		while (it.hasNext()) {
			modelNodes.addElement(it.next());
		}
		applyNodesList.getSelectionModel().setSelectionInterval(0, modelNodes.getSize());
		applyNodesList.repaint();
	}
	
	
	/**
	 * Fills the list of Edges with data
	 * @param Edges
	 */
	public void loadEdgesData(Collection<?> c) {
		modelEdges.removeAllElements();
		Iterator<?> it = c.iterator();
		while (it.hasNext()) {
			modelEdges.addElement(it.next());
		}
		applyEdgesList.getSelectionModel().setSelectionInterval(0, modelEdges.getSize());
		applyEdgesList.repaint();
	}

	/**
	 * Fills the combobox of fields for the nodes
	 */
	public void setNodesList() {
		nodesFieldsComboBox.removeAllItems();
		ArrayList al = new ArrayList();
		al.addAll(Guess.getGraph().getNodeSchema().allFields());
		Collections.sort(al);
		Iterator it = al.iterator();
		while (it.hasNext()) {
			Field f = (Field) it.next();
			nodesFieldsComboBox.addItem(f);
		}
		nodesFieldsComboBox.setSelectedIndex(0);
	}
	
	/**
	 * Fills the combobox of fields for the edges
	 */
	public void setEdgesList() {
		edgesFieldsComboBox.removeAllItems();
		ArrayList al = new ArrayList();
		al.addAll(Guess.getGraph().getEdgeSchema().allFields());
		Collections.sort(al);
		Iterator it = al.iterator();
		while (it.hasNext()) {
			Field f = (Field) it.next();
			edgesFieldsComboBox.addItem(f);
		}
		edgesFieldsComboBox.setSelectedIndex(0);		
	}

	
	/**
	 * Create the panel for the tab "Nodes"
	 * @return The panel for the nodes
	 */
	public JPanel createPanelNodes() {

		panelNodes.setOpaque(false);
		panelNodes.setBounds(0, 0, 322, 260);
		panelNodes.setLayout(null);

		nodesFieldsLabel.setDisplayedMnemonic(KeyEvent.VK_S);
		nodesFieldsLabel.setText("Select Node Field:");
		nodesFieldsLabel.setBounds(10, 9, 105, 16);
		panelNodes.add(nodesFieldsLabel);

		newValueNodeLabel.setDisplayedMnemonic(KeyEvent.VK_N);
		newValueNodeLabel.setText("New Value:");
		newValueNodeLabel.setBounds(10, 165, 90, 16);
		panelNodes.add(newValueNodeLabel);

		newValueNodeLabel.setLabelFor(newValueNodesText);
		newValueNodesText.setBounds(122, 163, 186, 20);
		panelNodes.add(newValueNodesText);

		applyToNodeLabel.setDisplayedMnemonic(KeyEvent.VK_A);
		applyToNodeLabel.setText("Apply To:");
		applyToNodeLabel.setBounds(10, 37, 54, 16);
		panelNodes.add(applyToNodeLabel);


		nodesItemsScrollpane.setBounds(122, 36, 186, 121);
		nodesItemsScrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		nodesItemsScrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		panelNodes.add(nodesItemsScrollpane);

		applyToNodeLabel.setLabelFor(applyNodesList);
		nodesItemsScrollpane.setViewportView(applyNodesList);

		selectAllNodesButton.setMnemonic(KeyEvent.VK_L);
		selectAllNodesButton.setText("Select All");
		selectAllNodesButton.setBounds(23, 132, 90, 25);
		panelNodes.add(selectAllNodesButton);

		nodesFieldsLabel.setLabelFor(nodesFieldsComboBox);
		nodesFieldsComboBox.setBounds(122, 5, 186, 25);
		nodesFieldsComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (nodesFieldsComboBox.getSelectedIndex()>=0) {
				selectedFieldNodes = (Field) nodesFieldsComboBox.getSelectedItem();
				}
			}
			});
		
		panelNodes.add(nodesFieldsComboBox);
		
		selectAllNodesButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent ev) {
				applyNodesList.setSelectionInterval(0, applyNodesList.getModel().getSize());
				applyNodesList.repaint();
			}
		});		
		
		return panelNodes;
	}
	
	/**
	 * Create the panel for the tab "Edges"
	 * @return The panel for the edges
	 */
	public JPanel createPanelEdges() {

		panelEdges.setOpaque(false);
		panelEdges.setBounds(0, 0, 322, 260);
		panelEdges.setLayout(null);
		
		edgesFieldsLabel.setDisplayedMnemonic(KeyEvent.VK_S);
		edgesFieldsLabel.setBounds(10, 9, 105, 16);
		edgesFieldsLabel.setText("Select Edge Field:");
		panelEdges.add(edgesFieldsLabel);

		newValueEdgesLabel.setDisplayedMnemonic(KeyEvent.VK_N);
		newValueEdgesLabel.setBounds(10, 165, 105, 16);
		newValueEdgesLabel.setText("New Value:");
		panelEdges.add(newValueEdgesLabel);

		newValueEdgesText.setName("newValue");
		newValueEdgesLabel.setLabelFor(newValueEdgesText);
		newValueEdgesText.setBounds(122, 163, 186, 20);
		panelEdges.add(newValueEdgesText);

		applyToEdgesLabel.setDisplayedMnemonic(KeyEvent.VK_A);
		applyToEdgesLabel.setBounds(10, 37, 54, 16);
		applyToEdgesLabel.setText("Apply To:");
		panelEdges.add(applyToEdgesLabel);

		edgesItemsScrollpane.setBounds(122, 36, 186, 121);
		edgesItemsScrollpane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		edgesItemsScrollpane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelEdges.add(edgesItemsScrollpane);

		applyToEdgesLabel.setLabelFor(applyEdgesList);
		edgesItemsScrollpane.setViewportView(applyEdgesList);

		selectAllEdgesButton.setActionCommand("Select All");
		selectAllEdgesButton.setMnemonic(KeyEvent.VK_E);
		selectAllEdgesButton.setBounds(23, 132, 90, 25);
		selectAllEdgesButton.setText("Select All");
		panelEdges.add(selectAllEdgesButton);

		edgesFieldsLabel.setLabelFor(edgesFieldsComboBox);
		edgesFieldsComboBox.setBounds(122, 5, 186, 25);
		edgesFieldsComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (edgesFieldsComboBox.getSelectedIndex()>=0) {
				selectedFieldEdges = (Field) edgesFieldsComboBox.getSelectedItem();
				}
			}
			});		
		panelEdges.add(edgesFieldsComboBox);

		selectAllEdgesButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent ev) {
				applyEdgesList.setSelectionInterval(0, applyEdgesList.getModel().getSize());
				applyEdgesList.repaint();
			}
		});
		
		return panelEdges;
	}

	private void createPanel() {


		getRootPane().setDefaultButton(applyButton);
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.setBounds(240, 247, 90, 25);
		getContentPane().add(closeButton);
		applyButton.setMnemonic(KeyEvent.VK_P);
		applyButton.setBounds(144, 247, 90, 25);
		getContentPane().add(applyButton);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				singleton = null;
				dispose();
			}
		});
		
		getContentPane().setLayout(null);
		setTitle("Field Editor - GUESS");
		setResizable(false);
		

		applyButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent ev) {
				if (tabbedpane.getSelectedComponent().equals(panelEdges)) {
					applyToEdgesList();
				} else {
					applyToNodesList();
				}
			}
		});

		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent ev) {
				singleton = null;
				dispose();
			}
		});

		applyButton.setActionCommand("Apply");
		applyButton.setName("okB");
		applyButton.setText("Apply");

		closeButton.setActionCommand("Done");
		closeButton.setName("cancelB");
		closeButton.setText("Close");

		tabbedpane.setBounds(10, 10, 320, 218);

		tabbedpane.addTab("Edges", createPanelEdges());
		tabbedpane.addTab("Nodes", createPanelNodes());
		
		getContentPane().add(tabbedpane);
		
	}

}
