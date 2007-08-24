package com.hp.hpl.guess;

import java.awt.*;
import java.util.*;
import java.io.*;

public class FontTest {

    public static void main(String[] args) throws IOException {
	if (args.length < 2) {
	    System.out.println("Usage: FontTest file format");
	    System.exit(0);
	}
	StringBuffer sb = new StringBuffer();
	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]),args[1]));
	while(br.ready()) {
	    sb.append(br.readLine());
	}
	Vector<Font> valid = getValid(sb.toString());
	System.out.println("\n\nValid fonts to use: \n");
	for (int i = 0 ; i < valid.size() ; i++) {
	    System.out.println(valid.elementAt(i).getFontName());
	}
	if (valid.size() == 0) {
	    System.out.println("no matching fonts found\n");
	}
    }
    
    public static Vector getValid(String teststring) {
	Vector<Font> validFonts = new Vector<Font>();
	java.awt.Font[] allfonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	int fontcount = 0;
	for (int j = 0; j < allfonts.length; j++) {
	    if (allfonts[j].canDisplayUpTo(teststring) == -1) {
		validFonts.add(allfonts[j]);
	    }
	}
	return(validFonts);
    }
}