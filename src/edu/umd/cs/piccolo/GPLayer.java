package edu.umd.cs.piccolo;

import com.hp.hpl.guess.piccolo.PFactory;
import edu.umd.cs.piccolo.util.PPaintContext;

public class GPLayer extends PLayer {

	private static final long serialVersionUID = -7450355408255939849L;

	public void setChildPaintInvalid(boolean cp) {
	if (cp)
	    PFactory.updateTime();
	super.setChildPaintInvalid(cp);
    }

    public PNode removeChild(PNode child) {
	PFactory.updateTime();
	return(super.removeChild(child));
    }

    public void paintBackChannel(PPaintContext ppc) {
	super.paint(ppc);
    }
}
