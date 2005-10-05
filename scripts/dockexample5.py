# work in progress

import java
import javax.swing
import com

# this is our toolbar

class dockexample5(com.hp.hpl.guess.ui.DockableAdapter):

	myLabel = javax.swing.JLabel("Setting up...")

	def __init__(self):
		self.add(self.myLabel)
		ui.dock(self)

	def getTitle(self):
		return("dockexample5")

	def update(self,val):
		self.myLabel.setText(val);
		v.repaint()
		
class bimage(java.lang.Thread):
	
	# keep a reference to our hearbeat monitor
	screeninterface = None;

	def __init__(self):
		self.screeninterface = dockexample5()

		v.setBackgroundImage("images/map.jpg")

		# start the thread
		self.start()

	def run(self):
		g.nodes.visible = false
		_n1 = g.nodes[0]
		_n2 = g.nodes[len(g.nodes)-1]
		#print _n1
		#print _n2
		nullClick = v.getLastClickedPosition()
		self.screeninterface.update("Please click on the position for " + _n1.toString())
		firstClick = v.getLastClickedPosition()
		while (firstClick is nullClick):
			Thread.sleep(1000)            # sleep
			firstClick = v.getLastClickedPosition()

		self.screeninterface.update("Please click on the position for " + _n2.toString())
		secondClick = v.getLastClickedPosition()
		while (secondClick is firstClick):
			Thread.sleep(1000)            # sleep
			secondClick = v.getLastClickedPosition()
		self.screeninterface.update("updating...")
		Thread.sleep(1000)

		xshrink = (firstClick.getX() - secondClick.getX()) / (_n1.x - _n2.x)
		yshrink = (firstClick.getY() - secondClick.getY()) / (_n1.y - _n2.y)
		#print xshrink
		#print yshrink
		xtrans = firstClick.getX() - _n1.x
		ytrans = firstClick.getY() - _n1.y
		print xtrans
		print ytrans
		centerAfterLayout(false)   # turn off centering after layout
		setSynchronous(true)       # make layouts run in same thread
		#rescaleLayout(xshrink,yshrink)
		for _n in g.nodes:
			_n.x += xtrans
			_n.y += ytrans
		g.nodes.visible = true
		ui.close(self.screeninterface)
		centerAfterLayout(true)
		setSynchronous(false)  
	
	
