package com.hp.hpl.guess.animation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.naming.directory.InvalidAttributeIdentifierException;

public abstract class GAnimation implements Cloneable {
	
	private Map<String,String> attributes = new HashMap<String, String>();
	private Set<String> availableAttributes = new HashSet<String>();

	/**
	 * Start the animation for n loops
	 */
	public abstract void start(int loops);
	
	/**
	 * finish the current loop and then stop
	 * the animation
	 */
	public abstract void stop();
	
	/**
	 * Add a attribute 
	 * @param name
	 * @return 
	 */
	protected void addAvailableAttributes(String name, String defaultValue) {
		availableAttributes.add(name);
		try {
			setAttribute(name,defaultValue);
		} catch (InvalidAttributeIdentifierException e) {
			// Will not happen, because added the name
			// just the line before!
		}
	}
	
	
	/**
	 * Start the animation for a (nearly) unlimited count of loops.
	 * Should be overwritten by subclasses to really get an unlimited
	 * count of loops instead of Integer.MAX_VALUE.
	 */
	public void start() {
		start(Integer.MAX_VALUE);
	}
	
	/**
	 * Return a list of available Params
	 * @return
	 */
	public Set<String> getAvailableAttributes() {
		return availableAttributes;
	}
	
	/**
	 * Set the value of parameter name
	 * @param name
	 * @param value
	 * @throws InvalidAttributeIdentifierException if name is not in getAvaibleAttributes()
	 */
	public void setAttribute(String name, String value) throws InvalidAttributeIdentifierException {
		if (!getAvailableAttributes().contains(name)) {
			throw new InvalidAttributeIdentifierException();
		}
		
		attributes.put(name, value);
	}
	
	/**
	 * Return the value of parameter name 
	 * @param name
	 * @return
	 * @throws InvalidAttributeIdentifierException if name is not in getAvaibleAttributes()
	 */
	public String getAttribute(String name) throws InvalidAttributeIdentifierException{
		if (!getAvailableAttributes().contains(name)) {
			throw new InvalidAttributeIdentifierException();
		}
		
		return attributes.get(name);
	}
	
	/**
	 * Return a copy of the current object 
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException {
	    return super.clone(); 
	}
}
