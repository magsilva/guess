package com.hp.hpl.guess;

import java.util.*;

public class CompoundQuery extends Query
{
	private static final long serialVersionUID = 6986822677301740069L;
	private String op;
    private Query query1;
    private Query query2;
    
    public Set<String> getStates(Set<String> init) {
	if (init == null) {
	    init = new HashSet<String>();
	}
	query1.getStates(init);
	query2.getStates(init);
	return(init);
    }

	public CompoundQuery(Graph graph, String op, Query query1, Query query2, int type)
	{
		super(graph, type);

		this.op = op;
		this.query1 = query1;
		this.query2 = query2;
	}

	public String toSQLString()
	{
	    if (op.equals("&")) {
		op = "AND";
	    } else if (op.equals("|")) {
		op = "OR";
	    }
	    return "(" + query1.toSQLString() + ") " + op + " (" + query2.toSQLString() + ")";
	}

	public boolean describes(GraphElement element)
	{
		if (op.equals(AND))
			return query1.describes(element) && query2.describes(element);
		if (op.equals(OR))
			return query1.describes(element) || query2.describes(element);
		
		throw new Error("Invalid operation:  " + op);
	}
}
