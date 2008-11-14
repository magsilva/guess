package com.hp.hpl.guess.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.action.GStateAction;
import com.hp.hpl.guess.storage.StorageEventListener;
import com.hp.hpl.guess.storage.StorageFactory;

public class StateSelectorPopup extends JPopupMenu implements StorageEventListener {
	
	private static final long serialVersionUID = 1L;

	/**
	 * A List of all current saved states
	 */
	List<String> savedTitles = new ArrayList<String>();
	
	/**
	 * Maps the statename to a thumbnail
	 */
	Map<String, Icon> descToIcon = new HashMap<String, Icon>();


	/**
	 * The current state name
	 */
	String currentState = "";
	
	Set<StateSelectorEventListener> eventListeners = new HashSet<StateSelectorEventListener>();
	
	
	public StateSelectorPopup() {
		StorageFactory.getSL().addStorageEventListener(this);
		setPreferredSize(new Dimension(196, 200));
	}
	
	/**
	 * Add a state to the StateSelector, does not add 
	 * a state.
	 * @param img
	 * @param aTitle
	 */
	private void add(Icon img, String aTitle) {
		// Add title to list
		savedTitles.add(aTitle);
		
		// Put Menuitem Icon
		descToIcon.put(aTitle, img);
	}
	
	
	/**
	 * Remove a state from the StateSelector, does not 
	 * remove the state itself
	 * @param aStateDesc
	 */
	private void remove(String aStateDesc) {
		savedTitles.remove(aStateDesc);
		descToIcon.remove(aStateDesc);
	}
	
	
	public void show(Component invoker, int x, int y) {
		
		super.removeAll();
		super.add(getMenu(), BorderLayout.CENTER);
			
		super.show(invoker, x, y);
	}
	
	/**
	 * Returns a menu containing all states with thumbnails
	 * @return
	 */
	public JPanel getMenu() {
		JScrollPane itemScroller = new JScrollPane();
		JPanel itemPanel = new JPanel();
		final JPopupMenu pm = this;
		int i = 0;
		
		
		itemPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 1;
		c.weightx = 1;
		c.gridy = 0;
		
		
		Collections.reverse(savedTitles);
		Iterator<String> menuItemsIterator = savedTitles.iterator();
		while (menuItemsIterator.hasNext()) {
			final String nextItem = menuItemsIterator.next();

			
			final JLabel newItem = new JLabel(nextItem, descToIcon.get(nextItem), 0);
			newItem.setBackground(Color.WHITE);
			newItem.setOpaque(true);
			newItem.setHorizontalAlignment(SwingConstants.LEFT);
			newItem.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			newItem.setForeground(new Color(68, 70, 72));
			
			newItem.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					StorageFactory.getSL().loadState(nextItem);
					pm.setVisible(false);
				}
				
				public void mouseEntered(MouseEvent e) {
					newItem.setBackground(new Color(218,235,243));
				}
		        public void	mouseExited(MouseEvent e) {
		        	newItem.setBackground(Color.WHITE);
		        }

			});

			i++;
			c.gridy = i;
			itemPanel.add(newItem, c);
		}
		Collections.reverse(savedTitles);
		
		if (i > 4) {
			setPreferredSize(new Dimension(196, 104 * 5 + 8));
		} else {
			setPreferredSize(new Dimension(196, 104 * i + 8));
		}
		
		itemScroller = new JScrollPane(itemPanel);
		itemScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		itemPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		itemScroller.setBorder(null);
		
		JPanel ret = new JPanel();
		ret.setLayout(new GridBagLayout());
		GridBagConstraints d = new GridBagConstraints();
		d.fill = GridBagConstraints.BOTH;
		d.weighty = 1;
		d.weightx = 1;
		ret.add(itemScroller, d);
		
		return ret;
	}

	
	
	/**
	 * Load the previous state if it exists
	 */
	public void loadPrevious() {
		if (hasPrevious()) {
			String nextItem = savedTitles.get(savedTitles.indexOf(currentState) - 1);
			StorageFactory.getSL().loadState(nextItem);
		}
	}
	
	
	/**
	 * Load the next state if it exists
	 */
	public void loadNext() {
		if (hasNext()) {
			String nextItem = savedTitles.get(savedTitles.indexOf(currentState) + 1);
			StorageFactory.getSL().loadState(nextItem);
		}
	}
	
	/**
	 * Returns true if there is a next state
	 * @return
	 */
	public boolean hasNext() {
		if (savedTitles.contains(currentState)) {
			if (savedTitles.indexOf(currentState) < (savedTitles.size() - 1)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if there is a previous state
	 * @return
	 */
	public boolean hasPrevious() {
		if (savedTitles.contains(currentState)) {
			if (savedTitles.indexOf(currentState) > 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if there is a saved state
	 * @return
	 */
	public boolean isNotEmpty() {
		return (savedTitles.size() > 0);
	}
	
	
	/**
	 * Returns true if state is a saved state
	 * @return
	 */
	public boolean isState(String state) {
		return (savedTitles.contains(state));
	}
	
	/**
	 * Set the internal current state 
	 */
	public void stateLoaded(String state) {
		// Do not process undo / redo states
    	if (state.charAt(0)==GStateAction.delimiter.charAt(0)) {
    		return;
    	}
		
		currentState = state;
		
		// Call EventListeners
		Iterator<StateSelectorEventListener> eventIterator = eventListeners.iterator();
		while (eventIterator.hasNext()) {
			StateSelectorEventListener eventItem = eventIterator.next();
			eventItem.stateLoaded(state);
		}
	}

	/**
	 * Add a state to the StateSelector if a state
	 * is added
	 */
	public void stateSaved(String state) {
		// Do not save undo / redo states
    	if (state.charAt(0)==GStateAction.delimiter.charAt(0)) {
    		return;
    	}
		
		// Generate image
		BufferedImage imageSrc = Guess.getFrame().getFullImage();
		
		int thumbWidth = 100;
		int thumbHeight = 100;
		
		Image thumb = imageSrc.getScaledInstance(thumbWidth, thumbHeight ,Image.SCALE_SMOOTH);

		BufferedImage bi = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D biContext = bi.createGraphics();
		biContext.drawImage(thumb, 0, 0, null);
			
		// add to Menu
		if (savedTitles.contains(state)) {
			remove(state);
			add(new ImageIcon(thumb), state);
		} else {
			add(new ImageIcon(thumb), state);
		}
		
		currentState = state;
		
		// Call EventListeners
		Iterator<StateSelectorEventListener> eventIterator = eventListeners.iterator();
		while (eventIterator.hasNext()) {
			StateSelectorEventListener eventItem = eventIterator.next();
			eventItem.stateSaved(state);
		}
	}
	
	public void addStateSelectorEventListener(StateSelectorEventListener eventListener) {
		eventListeners.add(eventListener);
	}

}


interface StateSelectorEventListener {
    public void stateLoaded(String state);

    public void stateSaved(String state);
}

