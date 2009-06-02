package com.hp.hpl.guess.util;

import java.util.prefs.*;
import com.hp.hpl.guess.Guess;
import java.util.HashMap;
import java.io.*;

public class PrefWrapper extends Preferences {

    private Preferences pref = null;
    private HashMap inMem = null;

    protected PrefWrapper(Preferences pref) {
	this.pref = pref;
	if (pref == null) {
	    inMem = new HashMap();
	}
    }

    public String absolutePath() {
	if (pref != null) {
	    return(pref.absolutePath());
	} 
	return("");
    }

    public void addNodeChangeListener(NodeChangeListener ncl) {
	if (pref != null) {
	    pref.addNodeChangeListener(ncl);
	}
    }

    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
	if (pref != null) {
	    pref.addPreferenceChangeListener(pcl);
	}
    }

    public String[] childrenNames() throws BackingStoreException {
	if (pref != null) {
	    return(pref.childrenNames());
	} else {
	    return new String[]{};
	}
    }
    public void clear() throws BackingStoreException {
	if (pref != null) {
	    pref.clear();
	}
    }
    
    public void exportNode(OutputStream os) throws IOException,BackingStoreException {
	if (pref != null) {
	    pref.exportNode(os);
	}
    }
    
    public void exportSubtree(OutputStream os) throws IOException,BackingStoreException {
	if (pref != null) {
	    pref.exportSubtree(os);
	}
    }

    public void flush() throws BackingStoreException {
	if (pref != null) {
	    pref.flush();
	}
    }


    public String get(String key, String def){
	if (pref != null) {
	    return(pref.get(key,def));
	}
	return(def);
    }


    public boolean getBoolean(String key, boolean def){
	if (pref != null) {
	    return(pref.getBoolean(key,def));
	}
	return(def);
    } 

    public byte[] getByteArray(String key, byte[] def){
	if (pref != null) {
	    return(pref.getByteArray(key,def));
	}
	return(def);
    }

    public double getDouble(String key, double def){
	if (pref != null) {
	    return(pref.getDouble(key,def));
	}
	return(def);
    } 
 
    public float getFloat(String key, float def){
	if (pref != null) {
	    return(pref.getFloat(key,def));		
	}
	return(def);
    }

    public int getInt(String key, int def){
	if (pref != null) {
	    return(pref.getInt(key,def));
	}
	return(def);
    }

    public long getLong(String key, long def){
	if (pref != null) {
	    return(pref.getLong(key,def));
	}
	return(def);
    }
    
    public static void importPreferences(InputStream is) throws IOException,InvalidPreferencesFormatException {
	if (Guess.getAppletMode()) {
	}

	try {
	    Preferences.importPreferences(is);
	} catch (SecurityException ex) {
	
	}
    }

    
    public boolean isUserNode(){
	if (pref != null) {
	    return(pref.isUserNode());
	}
	return(false);
    } 

    public String[] keys() throws BackingStoreException {
	if (pref != null) {
	    return(pref.keys());
	}
	return(new String[]{});
    } 

    public String name(){
	if (pref != null) {
	    return(pref.name());
	}
	return("");
    }

    public Preferences node(String pathName) {
	if (pref != null) {
	    return(pref.node(pathName));
	}
	return(new PrefWrapper(null));
    }

    public boolean nodeExists(String pathName) throws BackingStoreException {
	if (pref != null) {
	    return(pref.nodeExists(pathName));
	}
	return(false);
    }

    public Preferences parent(){
	if (pref != null) {
	    return(pref.parent());
	}
	return(new PrefWrapper(null));
    }

    public void put(String key, String value) {
	if (pref != null) {
	    pref.put(key,value);
	} 
    }

    public void putBoolean(String key, boolean value) {
	if (pref != null) {
	    pref.putBoolean(key,value);
	} 
    }
    public void putByteArray(String key, byte[] value) {
	if (pref != null) {
	    pref.putByteArray(key,value);
	} 
    }
     
    
    public void putDouble(String key, double value) {
	if (pref != null) {
	    pref.putDouble(key,value);
	} 
    }

    public void putFloat(String key, float value) {
	if (pref != null) {
	    pref.putFloat(key,value);
	} 
    }

    public void putInt(String key, int value) {
	if (pref != null) {
	    pref.putInt(key,value);
	} 
    }

    
    public void putLong(String key, long value) {
	if (pref != null) {
	    pref.putLong(key,value);
	} 
    }

    public void remove(String key) {
	if (pref != null) {
	    pref.remove(key);
	}
    }

    public void removeNode()  throws BackingStoreException {
	if (pref != null) {
	    pref.removeNode();
	}
    }
    
    public void removeNodeChangeListener(NodeChangeListener ncl) {
	if (pref != null) {
	    pref.removeNodeChangeListener(ncl);
	}
    }
    
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
	if (pref != null) {
	    pref.removePreferenceChangeListener(pcl);
	}
    }

    public void sync() throws BackingStoreException {
	if (pref != null) {
	    pref.sync();
	}
    } 
    
    public String toString() {
	if (pref != null) {
	    return(pref.toString());
	}
	return("Empty preference wrapper for applet mode");
    }

    public static PrefWrapper systemNodeForPackage(Class<?> c) {
	if (Guess.getAppletMode()) {
	    return(new PrefWrapper(null));
	}

	try {
	    return(new PrefWrapper(Preferences.systemNodeForPackage(c)));
	} catch (SecurityException ex) {
	    return(new PrefWrapper(null));
	}
    }

    public static PrefWrapper systemRoot() {
	if (Guess.getAppletMode()) {
	    return(new PrefWrapper(null));
	}
	try {
	    return(new PrefWrapper(Preferences.systemRoot()));
	} catch (SecurityException ex) {
	    return(new PrefWrapper(null));
	}
    }

    public static PrefWrapper userNodeForPackage(Class<?> c) {
	if (Guess.getAppletMode()) {
	    return(new PrefWrapper(null));
	}
	try {
	    return(new PrefWrapper(Preferences.userNodeForPackage(c)));
	} catch (SecurityException ex) {
	    return(new PrefWrapper(null));
	}
    }

    public static PrefWrapper userRoot() {
	if (Guess.getAppletMode()) {
	    return(new PrefWrapper(null));
	}
	try {
	    return(new PrefWrapper(Preferences.userRoot()));
	} catch (SecurityException ex) {
	    return(new PrefWrapper(null));
	}
    }
}