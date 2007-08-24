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
	Vector chinesefonts = new Vector();
	java.awt.Font[] allfonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	int fontcount = 0;
	StringBuffer sb = new StringBuffer();
	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]),args[1]));
	while(br.ready()) {
	    sb.append(br.readLine());
	}
	String teststring = sb.toString();
	for (int j = 0; j < allfonts.length; j++) {
	    if (allfonts[j].canDisplayUpTo(teststring) == -1) {
		System.out.println(allfonts[j].getFontName());
	    }
	}
    }
}