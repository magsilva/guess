def pairwiseDistance(_n1,_n2):
	_p1 = _n1.dijkstraShortestPath(org)
	_p2 = _n2.dijkstraShortestPath(org)
	_p1.reverse()
	_p2.reverse()
	while (_p1[0] == _p2[0]):
		_p1.pop(0)
		_p2.pop(0)
		if ((len(_p1) == 0) or (len(_p2) == 0)):
			break 
	return len(_p1) + len(_p2)
	
def findAverage(_grp):
	sum = 0
	count = 0
	for i in range(0,len(_grp)):
		for j in range(i+1,len(_grp)):
			sum = sum + pairwiseDistance(_grp[i],_grp[j])
			count = count + 1
	return sum / count

gps = (version != '0').groupAndSortBy(gp)
for z in gps:
	avg = findAverage(z)
	print str(z[0].gp) + " " + str(avg) 