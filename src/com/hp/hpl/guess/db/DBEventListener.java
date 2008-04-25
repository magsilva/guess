package com.hp.hpl.guess.db;

public interface DBEventListener {

    public void stateLoaded(String state);

    public void stateSaved(String state);
}