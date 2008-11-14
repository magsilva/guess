package com.hp.hpl.guess.animation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.guess.Edge;
import com.hp.hpl.guess.GraphElement;
import com.hp.hpl.guess.Node;
import com.hp.hpl.guess.piccolo.PAnimationFactory;
import com.hp.hpl.guess.ui.EdgeEditorPopup;
import com.hp.hpl.guess.ui.NodeEditorPopup;


public abstract class AnimationFactory {
	
	private static AnimationFactory singleton = null;
	
	private static Map<String, GNodeAnimation> animNodeMap = new HashMap<String, GNodeAnimation>();
	private static Map<String, GEdgeAnimation> animEdgeMap = new HashMap<String, GEdgeAnimation>();
	
	
    public static final int PICCOLO = 1;
    public static final int PREFUSE = 2;
    public static final int TOUCHGRAPH = 3;
    public static final int NOVIS = 4;
    public static final int JUNG = 5;
	
    /**
     * Set the AnimationFactory type. Currently only PICCOLO is supported.
     * @param type
     */
	public static void setFactory(int type) {
		if (singleton!=null) {
			throw new Error("AnimationFactory.setFactory() called twice");
		}
		
		if (type == PICCOLO) {
		    singleton = new PAnimationFactory();
		} else {
			throw new Error("Only the Piccolo AnimationFactory is implemented yet.");
		}
		
	}
	
	public AnimationFactory() {
		// Add stop menu items to contextmenus
		EdgeEditorPopup.addItemToAnimation("Stop").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    	if (EdgeEditorPopup.getSelected() == null)
			    	    return;

			    	Iterator<GraphElement> it = EdgeEditorPopup.getSelected().iterator();
			    	while(it.hasNext()) {
			    		GraphElement ge = (GraphElement)it.next();
			    		if (ge instanceof Edge) {
			    			((Edge) ge).animationStopAll();
			    		}
			    	}
			        
			}
		});
		NodeEditorPopup.addItemToAnimation("Stop").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    	if (NodeEditorPopup.getSelected() == null)
			    	    return;

			    	Iterator<GraphElement> it = NodeEditorPopup.getSelected().iterator();
			    	while(it.hasNext()) {
			    		GraphElement ge = (GraphElement)it.next();
			    		if (ge instanceof Node) {
			    			((Node) ge).animationStopAll();
			    		}
			    	}
			        
			}
		});
		
	}
	
	/**
	 * Returns the AnimationFactory type
	 * @return
	 */
	public static AnimationFactory getFactory() {
		return singleton;
	}
	
	/**
	 * Generates a new animation for a node
	 * @param animationName
	 * @param aNode
	 * @return
	 */
	public abstract GNodeAnimation generateNodeAnimation(String animationName, Node aNode); 
	
	/**
	 * Generates a new animation for a edge
	 * @param animationName
	 * @param aNode
	 * @return
	 */
	public abstract GEdgeAnimation generateEdgeAnimation(String animationName, Edge aEdge); 
	
	
	/**
	 * Returns a copy of a animation object for a node
	 * @param animationName
	 * @return
	 */
	protected GNodeAnimation getNodeAnimation(String animationName) {
		try {
			return (GNodeAnimation)animNodeMap.get(animationName).clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns a copy of a animation object for a edge
	 * @param animationName
	 * @return
	 */
	protected GEdgeAnimation getEdgeAnimation(String animationName) {
		try {
			return (GEdgeAnimation)animEdgeMap.get(animationName).clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Registers a new animation for a node
	 * @param animationName
	 * @param aAnimation
	 */
	public void registerNodeAnimation(final String animationName, GNodeAnimation aAnimation) {
		// Add animation to library
		animNodeMap.put(animationName, aAnimation);
		
		// Add menu item to context menu
		NodeEditorPopup.addItemToAnimation(animationName).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    	if (NodeEditorPopup.getSelected() == null)
			    	    return;

			    	Iterator<GraphElement> it = NodeEditorPopup.getSelected().iterator();
			    	while(it.hasNext()) {
			    		GraphElement ge = (GraphElement)it.next();
			    		if (ge instanceof Node) {
			    			((Node) ge).animate(animationName);
			    		}
			    	}
			        
			}
		});
		}
	
	/**
	 * Registers a new animation for a edge
	 * @param animationName
	 * @param aAnimation
	 */
	public void registerEdgeAnimation(final String animationName, GEdgeAnimation aAnimation) {
		// Add animation to library
		animEdgeMap.put(animationName, aAnimation);
		
		// Add menu item to context menu
		EdgeEditorPopup.addItemToAnimation(animationName).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    	if (EdgeEditorPopup.getSelected() == null)
			    	    return;

			    	Iterator<GraphElement> it = EdgeEditorPopup.getSelected().iterator();
			    	while(it.hasNext()) {
			    		GraphElement ge = (GraphElement)it.next();
			    		if (ge instanceof Edge) {
			    			((Edge) ge).animate(animationName);
			    		}
			    	}
			        
			}
		});
	}
	
}
