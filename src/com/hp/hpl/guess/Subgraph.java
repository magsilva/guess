package com.hp.hpl.guess;

import java.util.*;

import org.python.core.*;

/**
 * @pyobj Subgraph
 */
public class Subgraph {

    private static HashMap<String, Subgraph> rootNodes = new HashMap<String, Subgraph>();

    /**
     * @pyexport getSubgraph
     */
    public static Subgraph getRootSubgraph(String name) {
	if (rootNodes.containsKey(name)) {
	    return((Subgraph)rootNodes.get(name));
	} else {
	    Subgraph toRet = new Subgraph(name);
	    rootNodes.put(name,toRet);
	    return(toRet);
	}
    }

    private String name = "";
    
    private HashMap<String, Subgraph> cmap = new HashMap<String, Subgraph>();

    private HashSet<Node> nodes = new HashSet<Node>();

    //private HashSet edges = new HashSet();

    public Subgraph(String name) {
	this.name = name;
    }

    public Subgraph() {
	this("__root");
    }

    public Set<Object> getNodesShallow(Set<Object> input) {
	if (input == null) {
	    input = new HashSet<Object>(nodes);
	}  else {
	    input.addAll(nodes);
	}
	return(input);
    }

    public Set<Node> getNodesDeep(Set<Node> input) {
	if (input == null) {
	    input = new HashSet<Node>(nodes);
	} else {
	    input.addAll(nodes);
	}
	Iterator<Subgraph> it = cmap.values().iterator();
	while(it.hasNext()) {
	    Subgraph sg = (Subgraph)it.next();
	    input = sg.getNodesDeep(input);
	}
	return(input);
    }

    public Set<Object> getSubgraphsShallow(Set<Object> input) {
	if (input == null) {
	    input = new HashSet<Object>(cmap.values());
	} else {
	    input.addAll(cmap.values());
	}
	return(input);
    }

    public Set<Object> getSubgraphsDeep(Set<Object> input) {
	if (input == null) {
	    input = new HashSet<Object>(cmap.values());
	} else {
	    input.addAll(cmap.values());
	}
	Iterator<Subgraph> it = cmap.values().iterator();
	while(it.hasNext()) {
	    Subgraph sg = (Subgraph)it.next();
	    input = sg.getSubgraphsDeep(input);
	}
	return(input);
    }

    public Object __call__(Object o) {
	//System.out.println(o.getClass());
	return(null);
    }

    public Object __getattr__(String fieldName)
    {
	fieldName = fieldName.toLowerCase();

	if (fieldName.equals("nodes")) {
	    // actually need to create a meta hashset by crawling
	    // through children
	    return(getNodesDeep(new HashSet<Node>()));
	    //} else if (fieldName.equals("edges")) {
	    // actually need to create a meta hashset by crawling
	    // through children
	    //return(edges);
	} else if (fieldName.equals("subgraphs")) {
	    return(getSubgraphsDeep(new HashSet<Object>()));
	} else if (fieldName.equals("snodes")) {
	    return(getNodesShallow(new HashSet<Object>()));
	} else if (fieldName.equals("ssubgraphs")) {
	    return(getSubgraphsShallow(new HashSet<Object>()));
	} else if (!fieldName.startsWith("__")) {
	    if (cmap.containsKey(fieldName)){
		return(cmap.get(fieldName));
	    } else {
		Subgraph temp = new Subgraph(fieldName);
		cmap.put(fieldName,temp);
		return(temp);
	    }
	} 
	return(this);
    }

    public void clear() {
	nodes.clear();
	//edges.clear();
	cmap.clear();
    }

    public void add(Object o) {
	if (o instanceof Node) {
	    nodes.add((Node)o);
	    //} else if (o instanceof Edge) {
	    //edges.add(o);
	} else if (o instanceof Subgraph) {
	    cmap.put(((Subgraph)o).getName(),(Subgraph) o);
	} else if (o instanceof PySequence) {
	    PySequence seq = (PySequence)o;
	    for (int i = 0; i < seq.__len__(); i++) {
		add(((PyInstance)seq.__finditem__(i)).__tojava__(Object.class));
	    }
	} else {
	    throw(new Error("You can not add objects of type " + 
			    o.getClass() + " to a subgraph"));
	}
    }

    public void remove(Object o) {
	if (o instanceof Node) {
	    nodes.remove(o);
	    //} else if (o instanceof Edge) {
	    //edges.add(o);
	} else if (o instanceof Subgraph) {
	    cmap.remove(((Subgraph)o).getName());
	} else if (o instanceof PySequence) {
	    PySequence seq = (PySequence)o;
	    for (int i = 0; i < seq.__len__(); i++) {
		remove(((PyInstance)seq.__finditem__(i)).__tojava__(Object.class));
	    }
	} else {
	    throw(new Error("You can not add objects of type " + 
			    o.getClass() + " to a subgraph"));
	}
    }

    public void addNodes(Object o) {
	if (o instanceof Node) {
	    nodes.add((Node)o);
	} else if (o instanceof PySequence) {
	    PySequence seq = (PySequence)o;
	    for (int i = 0; i < seq.__len__(); i++) {
		addNodes(((PyInstance)seq.__finditem__(i)).__tojava__(Object.class));
	    }
	} else {
	    throw(new Error("You can not add objects of type " + 
			    o.getClass() + " to a subgraph.nodes"));
	}
    }
	
	
    public void addSubgraphs(Object o) {
	if (o instanceof Subgraph) {
	    cmap.put(((Subgraph)o).getName(),(Subgraph) o);
	} else if (o instanceof PySequence) {
	    PySequence seq = (PySequence)o;
	    for (int i = 0; i < seq.__len__(); i++) {
		addSubgraphs(((PyInstance)seq.__finditem__(i)).__tojava__(Object.class));
	    }
	} else {
	    throw(new Error("You can not add objects of type " + 
			    o.getClass() + " to a subgraph.subgraphs"));
	}
    }

    public Subgraph __add__(Object test) {
	//System.out.println(test.getClass());
	add(test);
	return(this);
    }

    public Subgraph __sub__(Object test) {
	//System.out.println(test.getClass());
	remove(test);
	return(this);
    }

    public void __setattr__(String fieldName, Object value)
    {
	fieldName = fieldName.toLowerCase();
	
	//System.out.println(fieldName + " " + value.getClass());
	if (fieldName.equals("nodes")) {
	    nodes.clear();
	    addNodes(value);
	    //} else if (fieldName.equals("edges")) {
	    //edges.clear();
	    //addEdges(value);
	} else if (fieldName.equals("subgraphs")) {
	    cmap.clear();
	    addSubgraphs(value);
	} else {
	    // we're going to try and be clever, if the value
	    // is not a sequence, node, or edge we'll try
	    // and invoke the __setattr__ on the nodes
	    if ((value instanceof Node) ||
		(value instanceof Edge) ||
		(value instanceof PySequence)) {
		if (!fieldName.startsWith("__")) {
		    Subgraph sg = null;
		    if (cmap.containsKey(fieldName)){
			sg = (Subgraph)cmap.get(fieldName);
		    } else {
			Subgraph temp = new Subgraph(fieldName);
			cmap.put(fieldName,temp);
			sg = temp;
		    }
		    sg.clear();
		    sg.add(value);
		}
	    } else {
		Iterator<Node> it = getNodesDeep(null).iterator();
		while(it.hasNext()) {
		    Node n = (Node)it.next();
		    n.__setattr__(fieldName,value);
		}
	    }
	}
    }   

    public String getName() {
	return(name);
    }

    public String toString() {
	StringBuffer sb = new StringBuffer("(nodes: ");
	
	boolean f = true;
	Iterator<Node> it = nodes.iterator();
	while(it.hasNext()) {
	    Node n = (Node)it.next();
	    if (f) {
		sb.append(n.getName());
		f = false;
	    } else {
		sb.append(", " + n.getName());
	    }
	}

	//	sb.append(") (edges: ");

	//f = true;
	//it = edges.iterator();
	//while(it.hasNext()) {
	//  Edge n = (Edge)it.next();
	//  if (f) {
	//sb.append(n.toString());
	//f = false;
	//  } else {
	//sb.append(", " + n.toString());
	//  }
	//}

	sb.append(") (subgraphs: ");
	Iterator<String> it1 = cmap.keySet().iterator();
	f = true;
	while(it1.hasNext()) {
	    String s = (String)it1.next();
	    if (f) {
		sb.append(s);
		f = false;
	    } else {
		sb.append(", " + s);
	    }
	}
	sb.append(")");

	return(sb.toString());
    }
}



