package com.hp.hpl.guess.ui;

import java.util.Collection;
import com.hp.hpl.guess.Node;

public interface ConvexHull {

    /**
     * adds the nodes to the hull
     */
    public void addNodes(Collection<Node> s);

    /**
     * remove nodes from the hull
     */
    public void removeNodes(Collection<Node> s);

    /**
     * adds a node from the hull
     */
    public void addNode(Node n);

    /**
     * remove nodes from the hull
     */
    public void removeNode(Node n);
    
    /**
     * get the nodes in this hull
     */
    public Collection<Node> getNodes();

    /**
     * sets the color
     */
    public void setColor(Object c);

    /**
     * gets the hull color
     */
    public String getColor();

    /**
     * sets the convex hull visibility
     */
    public void setVisible(boolean state);

    /**
     * gets the hull visibility
     */
    public boolean getVisible();
}
     
