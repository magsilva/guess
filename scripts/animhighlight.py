import java

class animhighlight(java.lang.Object):

	# so we can "unhighlight" nodes
	_toFix = {}
	
	def __init__(self):
		# add the listeners
		graphevents.mouseEnterNode = self.mouseEnter
		graphevents.mouseLeaveNode = self.mouseLeave
		graphevents.clickNode = self.mouseClick
		
		# remove default behaviors
		vf.defaultNodeHighlights(false)
		vf.defaultNodeZooming(false)

	def mouseEnter(self,_node):
		self._toFix[_node] = _node.color
		StatusBar.setStatus(str(_node))
		_node.color = yellow

		for _e in _node.getOutEdges():
			if not (_e in self._toFix.keys()):
				self._toFix[_e] = _e.color
				_e.color = orange
				_e.animate("arrows")

		for _e in _node.getInEdges():
			if not (_e in self._toFix.keys()):
				self._toFix[_e] = _e.color
				_e.color = green
				_e.animate("arrows")

		for _n in _node.getPredecessors():
			if (_n != _node):
				self._toFix[_n] = _n.color
				_n.color = green
				_n.animate("pulse")

		for _n in _node.getSuccessors():
			if (_n != _node):
				self._toFix[_n] = _n.color
				_n.color = red
				_n.animate("pulse")



	def mouseLeave(self,_node):
		# put back all the original colors
		# and stop animations
		for _elem in self._toFix.keys():
			_elem.color = self._toFix[_elem]
			_elem.animationStopAll()
		self._toFix.clear();

	def mouseClick(self,_node):
		# zoom to the node AND its neighbors
		_toCenter = [_node]
		_toCenter += _node.getNeighbors()
		center(_toCenter)

animhighlight()
