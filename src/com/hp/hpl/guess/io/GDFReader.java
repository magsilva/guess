package com.hp.hpl.guess.io;

import java.sql.*;
import java.util.*;
import java.io.*;
import org.python.core.*;

import com.hp.hpl.guess.*;
import com.hp.hpl.guess.storage.*;
import com.hp.hpl.guess.ui.ExceptionWindow;
import com.hp.hpl.guess.ui.StatusBar;
import javax.swing.table.AbstractTableModel;
import edu.uci.ics.jung.exceptions.ConstraintViolationException;
import com.hp.hpl.guess.db.*;

public class GDFReader {

    public static String[] stringSplit(String line) {
	// not very optimized, but we shouldn't do this that often
	if ((line.indexOf("'") >= 0) ||
	    (line.indexOf("\"") >= 0) ||
	    (line.indexOf("\\") >= 0)) {
	    char[] chars = new char[line.length()];
	    line.getChars(0,chars.length,chars,0);
	    boolean inQuote = false;
	    char quoteChar = '\'';
	    char slashChar = '\\';
	    Vector toReturn = new Vector();
	    StringBuffer curString = null;
	    for (int i = 0 ; i < chars.length ; i++) {
		//System.out.println(chars[i]);
		if (chars[i] == slashChar) {
		    if (chars[i+1] == 'n') {
			curString.append("\n");
			i++;
			continue;
		    } else if (chars[i+1] == 't') {
			curString.append("\t");
			i++;
			continue;
		    }
		}
		if (inQuote) {
		    if (chars[i] == quoteChar) {
			inQuote = false;
			//System.out.println(curString);
			if (curString != null)
			    toReturn.addElement(curString.toString());
			curString = null;
			continue;
		    } 
		} else {
		    if (chars[i] == ',') {
			//System.out.println(curString);
			if (curString != null) {
			    toReturn.addElement(curString.toString());
			} else if ((i > 0) && 
				   (chars[i-1] == ',')) {
			    toReturn.addElement("");
			}
			curString = null;
			continue;
		    } else if ((chars[i] == '\'') ||
			       (chars[i] == '\"')) {
			inQuote = true;
			quoteChar = chars[i];
			continue;
		    }
		}
		if (curString == null)
		    curString = new StringBuffer();
		curString.append(chars[i]);
	    }
	    if ((curString != null) ||
		(line.charAt(line.length() - 1) == ',')) {
		toReturn.addElement(curString.toString());
		//System.out.println(curString);
	    }
	    String[] toR = new String[toReturn.size()];
	    toReturn.copyInto(toR);
	    return(toR);
	} else {	
	    return(line.split(","));
	}
    }

    private static String fixString(String init,Hashtable defs) {
	String s = init.trim();
	StringBuffer toRet = new StringBuffer();
	
	String[] foo = s.split(",");
	for (int i = 0 ; i < foo.length ; i++) {
	    String t = foo[i].trim().toLowerCase();
	    String[] subelem = t.split(" ");
	    if (defs.containsKey(subelem[0])) {
		toRet.append(defs.get(subelem[0]));
	    } else {
		toRet.append(foo[i]);
	    }
	    if (Helper.isBadName(subelem[0])) {
		System.out.println("\n\nWARNING! field name \"" + subelem[0] + "\" may conflict with a restricted word\n\n");
	    }
	    if (i < foo.length - 1) {
		toRet.append(",");
	    }
	}
	return(toRet.toString());
    }

    Hashtable defaultNodeTypes  = new Hashtable();
    Hashtable defaultEdgeTypes  = new Hashtable();

    private String[] processNodeDefs(Graph g, String s) {
	NodeSchema ns = g.getNodeSchema();
    	String[] foo = s.split(",");
	String toRet = new String[foo.length];
	for (int i = 0 ; i < foo.length ; i++) {
	    String t = foo[i].trim().toLowerCase();
	    String[] subelem = t.split(" ");
	    String attrName = subelem[0];
	    String attrType = defaultNodeTypes.get(attrName);
	    String def = null;
	    for (int j = 1 ; subelem.length ; j++) {
		// handle not nulls?
		if (subelem[j].equals("default")) {
		    def = subelem[j+1];
		    j++;
		    continue;
		} else {
		    attrType = subelem[j];
		}
	    }
	    if (dnt == null) {
		// trouble, report error
	    
	    }
	    Field f = ns.getField(attrName);
	    if (f != null) {
		// field already defined
		Object o = convertToType(f.getSQLType(),def);
		if (o != null)
		    f.setDefault(o);
	    } else {
		//System.out.println(attrType);
		int t = getSQLType(attrType);
		g.addNodeField(attrName,t,convertToType(t,def));
	    }
	    toRet[i] = attrName;
	}
	return(toRet);
    }


    public String[] processEdgeDefs(Graph g, String s) {
	return(null);
    }
 
    private Object convertToType(int attrType,String def) {
	if (def == null) {
	    return(null);
	}
	//	def = def.trim();
	try {
	    if ((attrType == Types.BIT) ||
		(attrType == Types.BOOLEAN)) {
		if ((def.equalsIgnoreCase("true")) ||
		    (def.equalsIgnoreCase("1"))) {
		    return(Boolean.TRUE);
		} else {
		    return(Boolean.FALSE);
		}
	    } else if ((attrType == Types.INTEGER) || 
		       (attrType == Types.TINYINT) ||
		       (attrType == Types.SMALLINT)) {
		return(new Integer(Integer.parseInt(def)));
	    } else if (attrType == Types.DOUBLE) {
		return(new Double(Double.parseDouble(def)));
	    } else if (attrType == Types.FLOAT) {
		return(new Float(Float.parseFloat(def)));
	    } else if (attrType == Types.VARCHAR) {
		return(def);
	    } else {
		System.out.println("Treating unknown type " + attrType + 
				   " as String");
		return(def);
	    }
	} catch (Exception e) {
	    ExceptionWindow.getExceptionWindow(e);
	}
	return(null);
    }

    public GDFReader(Graph g, String file) throws Exception {
	BufferedReader br = new BufferedReader(new FileReader(file));

	String line = null;
	boolean inNodeDef = false;
	boolean inEdgeDef = false;
	
	int nodecount = 0;
	int edgecount = 0;
	
	Random rand = new Random();
	
	String[] nodeCols = null;
	String[] edgeCols = null;

	int nNameCol = -1;
	int eNode1Col = -1;
	int eNode2Col = -1;

	boolean directed = false;

	while ((line = br.readLine()) != null) {
	    line = line.trim();
	    lineNum++;
	    if ((line.startsWith("#")) || (line.equals(""))) {
		continue;
	    }
	    if (line.startsWith("nodedef>")) {
		inEdgeDef = false;
		inNodeDef = true;
		String def = line.substring(8);
		nodeCols = processNodeDef(g,def);
		for (int i = 0 ; i < nodeCols.length ; i++) {
		    if (nodeCols[i].equalsIgnoreCase("name")) {
			nNameCol = i;
			break;
		    }
		}
		if (nNameCol == -1) {
		    ExceptionWindow.getExceptionWindow("No name column, invalid GDF file");
		}
	    } else if (line.startsWith("edgedef>")) {
		inEdgeDef = true;
		inNodeDef = false;
		String def = line.substring(8);
		edgeCols = processEdgeDef(def);
		for (int i = 0 ; i < edgeCols.length ; i++) {
		    if (edgeCols[i].equalsIgnoreCase("node1")) {
			eNode1Col = i;
			break;
		    } else if (edgeCols[i].equalsIgnoreCase("node2")) {
			eNode2Col = i;
			break;
		    } else if (nodeCols[i].equalsIgnoreCase("directed")) {
			directed = true;
		    }

		}
		if (eNode1Col == -1) {
		    ExceptionWindow.getExceptionWindow("No node1 column, invalid GDF file");
		}
		if (eNode2Col == -1) {
		    ExceptionWindow.getExceptionWindow("No node2 column, invalid GDF file");
		}
	    } else {
		String[] vals = stringSplit(line);
		if (inNodeDef) {
		    Node n = g.addNode(vals[nNameCol]);
		    n.__setattr__("label",vals[nNameCol]);
		    for (int i = 0 ; i < vals.length ; i++) {
			if (i == nNameCol)
			    continue;
			n.__setattr__(nodeCols[i],vals[i]);
		    }
		} else if (inEdgeDef) {
		    Node s = g.getNodeByName(vals[eNode1Col]);
		    Node t = g.getNodeByName(vals[eNode2Col]);
		    Edge e = null;
		    if (directed) {
			e = g.addDirectedEdge(s,t);
		    } else {
			e = g.addUndirectedEdge(s,t);
		    }
		    for (int i = 0 ; i < vals.length ; i++) {
			if ((i == eNode1Col) || (i == eNode2Col))
			    continue;
			n.__setattr__(edgeCols[i],vals[i]);
		    }
		} else {
		    System.out.println("Your database definition file may "+
				       "have a problem in it, not sure what "+
				       "to do with:\n"+line+ " (line: "+
				       lineNum+")");
		}
	    }
	}
	System.out.println("\nLoaded " + nodecount + 
			   " nodes and " + edgecount + " edges");
	return;	
    }

    private int getSQLType(String s) throws Exception {
	if (s.equals("array")) {
	    return(Types.ARRAY);
	} else if (s.equals("bigint")) {
	    return(Types.BIGINT);
	}  else if (s.equals("binary")) {
	    return(Types.BINARY);
	}  else if (s.equals("bit")) {
	    return(Types.BIT);
	}  else if (s.equals("boolean")) {
	    return(Types.BOOLEAN);
	}  else if (s.equals("blob")) {
	    return(Types.BLOB);
	}  else if (s.equals("decimal")) {
	    return(Types.DECIMAL);
	}  else if (s.equals("double")) {
	    return(Types.DOUBLE);
	}  else if (s.equals("float")) {
	    return(Types.FLOAT);
	}  else if ((s.equals("integer")) || (s.equals("int"))) {
	    return(Types.INTEGER);
	}  else if (s.equals("java_object")) {
	    return(Types.JAVA_OBJECT);
	}  else if (s.equals("longvarchar")) {
	    return(Types.LONGVARCHAR);
	}  else if (s.equals("null")) {
	    return(Types.NULL);
	}  else if (s.equals("numeric")) {
	    return(Types.NUMERIC);
	}  else if (s.equals("real")) {
	    return(Types.REAL);
	}  else if (s.equals("smallint")) {
	    return(Types.SMALLINT);
	}  else if (s.equals("struct")) {
	    return(Types.STRUCT);
	}  else if (s.equals("time")) {
	    return(Types.TIME);
	}  else if (s.equals("timestamp")) {
	    return(Types.TIMESTAMP);
	}  else if (s.equals("tinyint")) {
	    return(Types.TINYINT);
	}  else if (s.equals("varchar")) {
	    return(Types.VARCHAR);
	}  
	throw new Exception("Unsuported Type");
    }
}
