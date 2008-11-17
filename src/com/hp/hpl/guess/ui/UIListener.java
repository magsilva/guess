package com.hp.hpl.guess.ui;

import com.hp.hpl.guess.Edge;
import com.hp.hpl.guess.Node;

public interface UIListener extends GraphMouseListener {

    public void shiftClickNode(Node n);

    public void shiftClickEdge(Edge e);

    public void clickNode(Node n);

    public void clickEdge(Edge e);
}
