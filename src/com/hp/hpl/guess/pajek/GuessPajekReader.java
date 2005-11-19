package com.hp.hpl.guess.pajek;

import java.util.*;
import com.hp.hpl.guess.*;
import java.io.*;
import com.hp.hpl.guess.ui.Colors;
import java.awt.Color;

public class GuessPajekReader {

    private static Random rand = new Random();

    private static HashSet shapes = new HashSet();
    
    static {
	shapes.add("box");
	shapes.add("ellipse");
	//shapes.add("diamond");
	//shapes.add("cross");
	shapes.add("triangle");
    }

    public static void readFile(Graph g, String filename) {
	try {
	    BufferedReader br = new BufferedReader(new FileReader(filename));
	    boolean vert = false;
	    boolean edges = false;
	    boolean arcs = false;
	    int nodes = -1;
	    while(br.ready()) {
		String line = br.readLine();
		line = line.trim();
		if (line.equals(""))
		    continue;

		if (line.startsWith("*Vertices")) {
		    vert = true;
		    edges = false;
		    arcs = false;
		    int space = (int)Math.max((double)line.lastIndexOf(' '),
					      (double)line.lastIndexOf('\t'));
		    nodes = Integer.parseInt(line.substring(space+1));
		    continue;
		} else if (line.startsWith("*Arcs")) {
		    vert = false;
		    edges = false;
		    arcs = true;
		    continue;
		} else if (line.startsWith("*Edges")) {
		    vert = false;
		    edges = true;
		    arcs = false;
		    continue;
		}
		
		if (vert) {
		    processVert(g,line);
		    continue;
		} else if (edges) {
		    processEdge(g,line,false);
		    continue;
		} else if (arcs) {
		    processEdge(g,line,true);
		    continue;
		}
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static void processVert(Graph g, String line) {
	int quoteS = line.indexOf("\"");
	int quoteE = line.lastIndexOf("\"");

	String label = null;
	String id = null;
	String[] rest = null;
	int pointer = 0;
	if (quoteS != quoteE) {
	    label = line.substring(quoteS+1,quoteE);
	    String[] result = line.split("\\s+");
	    id = result[0];
	    line = (line.substring(quoteE+1,line.length())).trim();
	    rest = line.split("\\s+");
	} else {
	    rest = line.split("\\s+");
	    id = rest[0];
	    label = rest[1];
	    pointer = 2;
	}

	double x = rand.nextDouble() * 1000;
	double y = rand.nextDouble() * 1000;

	if (rest.length > pointer) {
	    try {
		x = 1000 * Double.parseDouble(rest[pointer]);
		y = 1000 * Double.parseDouble(rest[pointer+1]);
		pointer = pointer+2;
	    } catch (NumberFormatException nfe) {
	    }
	}

	if (rest.length > pointer) {
	    try {
		double whatisthis = Double.parseDouble(rest[pointer]);
		pointer++;
	    } catch (NumberFormatException nfe) {
	    }
	}
	
	String shape = "box";

	if (rest.length > pointer) {
	    shape = rest[pointer];
	    if (shapes.contains(shape.toLowerCase())) {
		pointer++;
	    }
	}

	double s_size = 10;
	double x_fact = 1;
	double y_fact = 1;

	boolean rounded = false;
	
	String color = "blue";
	String labelcolor = null;

	for (int i = pointer ; i < rest.length - 1; i = i+2) {
	    String key = rest[i].toLowerCase();
	    String value = rest[i+1];
	    if (key.equals("x_fact")) {
		x_fact = Double.parseDouble(value);
	    } else if (key.equals("y_fact")) {
		y_fact = Double.parseDouble(value);
	    } else if (key.equals("sh")) {
		shape = value;
	    } else if (key.equals("r")) {
		if (!value.equals("0")) {
		    rounded = true;
		}
	    } else if (key.equals("phi")) {
		// rotation, ignore
	    } else if (key.equals("q")) {
		// diamond ratio, ignore
	    } else if (key.equals("ic")) {
		color = value;
	    } else if (key.equals("bc")) {
		// boundry color, ignore
	    } else if (key.equals("bw")) {
		// boundry width, ignore
	    } else if (key.equals("lc")) {
		labelcolor = value;
	    } else if (key.equals("la")) {
		// label angle, ignore
	    } else if (key.equals("lr")) {
		// label start, ignore
	    } else if (key.equals("lphi")) {
		// label angle, ignore
	    } else if (key.equals("fos")) {
		// font size, ignore
	    } else if (key.equals("font")) {
		// font, ignore
	    } else if (key.equals("hooks")) {
		// hooks, ignore
	    }
	}

	double width = s_size * x_fact;
	double height = s_size * y_fact;

	int style = 1;
	if (shape.equals("box")) {
	    style = 1;
	    if (rounded)
		style = 3;
	} else if (shape.equals("ellipse")) {
	    style = 2;
	}

	Node n = g.addNode("pajek"+id);
	n.__setattr__("style",new Integer(style));
	n.__setattr__("x",new Double(x));
	n.__setattr__("y",new Double(y));
	n.__setattr__("width",new Double(width));
	n.__setattr__("height",new Double(height));
	n.__setattr__("color",color);
	n.__setattr__("label",label);
    }

    public static void processEdge(Graph g, String line, boolean directed) {

	String[] rest = line.split("\\s+");
	String source = "pajek"+rest[0];
	String target = "pajek"+rest[1];

	Node s = g.getNodeByName(source);
	if (s == null) {
	    s = g.addNode(source);
	}
	Node t = g.getNodeByName(target);
	if (t == null) {
	    t = g.addNode(target);
	}

	String label = null;
	
	Edge e = null;
	if (directed) {
	    //System.out.println("creating directed");
	    e = g.addDirectedEdge(s,t);
	} else {
	    //System.out.println("creating undirected");
	    e = g.addUndirectedEdge(s,t);
	}

	double weight = 1;

	int pointer = 2;

	if (rest.length > pointer) {
	    try {
		weight = Double.parseDouble(rest[pointer]);
		pointer++;
	    } catch (NumberFormatException nfe) {
	    }
	}

	String color = "blue";
	String labelcolor = null;
	double width = 2;

	for (int i = pointer ; i < rest.length - 1; i = i+2) {
	    String key = rest[i].toLowerCase();
	    String value = rest[i+1];
	    //System.out.println(key + "=" + value);
	    if (key.equals("c")) {
		color = value;
	    } else if (key.equals("width")) {
		width = Double.parseDouble(value);
	    } else if (key.equals("lc")) {
		labelcolor = value;
	    } else if (key.equals("l")) {
		if (value.startsWith("\"")) {
		    if (value.endsWith("\"")) {
			continue;
		    }
		    int j = 2;
		    value = rest[i+j];
		    while(!value.endsWith("\"")) {
			j++;
			value = rest[i+j];
		    }
		    i += j - 1;
		}
	    } else if (key.equals("label")) {
		if (value.startsWith("\"")) {
		    if (value.endsWith("\"")) {
			label = value.substring(1,value.length()-1);
			continue;
		    }
		    label = value.substring(1,value.length());
		    int j = 2;
		    value = rest[i+j];
		    while(!value.endsWith("\"")) {
			label += " " + value;
			j++;
			value = rest[i+j];
		    }
		    label += " " + value.substring(0,value.length()-1);
		    i += j - 1;
		} else {
		    label = value;		    
		}
	    }
	    // ignoring everything else
	}
	
	e.__setattr__("width",new Double(width));
	e.__setattr__("color",Colors.getColor(color,Color.green));
	e.__setattr__("weight", new Double(weight));
	if (label != null)
	    e.__setattr__("label",label);
    }

    public static void main(String[] args) {
	readFile(null,args[0]);
    }
}
