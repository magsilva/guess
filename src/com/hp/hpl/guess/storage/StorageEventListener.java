package com.hp.hpl.guess.storage;

public interface StorageEventListener {
	
    public void stateLoaded(String state);
    
    public void stateSaved(String state);
    
}
