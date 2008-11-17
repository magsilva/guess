package com.hp.hpl.guess.prefuse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import prefuse.util.ui.JForcePanel;

import com.hp.hpl.guess.ui.DockableAdapter;
import com.hp.hpl.guess.ui.MainUIWindow;

public class ForcePanel extends DockableAdapter {
    
	private static final long serialVersionUID = -4369823904283877028L;

	public int getDirectionPreference() {
	return(MainUIWindow.VERTICAL_DOCK);
    }
    
    PrefuseDisplay pd = null;

    private final JButton run = new JButton("Start/Stop");

    public ForcePanel(PrefuseDisplay pd) {
	this.pd = pd;

	final PrefuseDisplay finalDisp = pd;

        JForcePanel jp = new JForcePanel(pd.fsim);
        
	Box cf = new Box(BoxLayout.X_AXIS);
        
	cf.add(run);
        cf.setBorder(BorderFactory.createTitledBorder(""));
	cf.add(Box.createHorizontalGlue());
        jp.add(cf);

	ActionListener startstop = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    finalDisp.toggleForce();
		}
	    };
	    
	run.addActionListener(startstop);

        //add(opanel);
      
	add(jp);
        add(Box.createVerticalGlue());
    }

    public String getTitle() {
	return("Force Controls");
    }    
}
