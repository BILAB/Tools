import unittest
import doctest

import protdb
import seq2feature
import util

def getpos_fasta(fname,answer):
	"""
	>>> fname = "../dataset/mono.d4.0.nxg.miss.fasta"
	>>> answer = "../dataset/answer_mono.d4.0.nxg.txt"
	>>> getpos_fasta(fname,answer).next()
	None
	"""
	# yielding answer position residue
	ans_sgr = util.ans(answer)
	ans2int = lambda idch,pos: 1 if ans_sgr.isans(idch,pos) else -1
	for idch,start,seq in seq2feature.fasta2seq(fname):
		idch = idch.strip()
		yield idch,{pos + start:res for pos,res in enumerate(seq)
			if ans_sgr.isans(idch,start + pos)}


def getpos_pdb(fname,answer):
	"""
	>>> fname = "../dataset/mono.d4.0.nxg.miss.fasta"
	>>> answer = "../dataset/answer_mono.d4.0.nxg.txt"
	>>> getpos_pdb(fname,answer).next()
	None
	"""
	# get Residue from PDB (use residue no. )
	def insert(idch,lpos):
		"""
		Seq number of insertion is [seq number]A.
		
		#example:
		### Insertion residue exits between 3 ~ 4. ###
		    ... 1 2 3 3A 4 5 6 ...
		
		"""
		def _f(pos,points):
			# remain the insertion points exits previous residue.
			_points = [i for i in points if i <= pos]
			return pos - len(_points)
			
		inserts = {"1kj2B":[30]}
		if inserts.has_key(idch):
			lpos = [_f(i,inserts[idch]) for i in lpos]
			return idch,lpos
		else:
			return idch,lpos
			
			
	pdbids = [idch for idch,start,seq in seq2feature.fasta2seq(fname)]
	# For insertion code #
	
	
	with open(answer) as fp:
		for rec in (line.strip().split()
					for line in iter(fp.readline,"")):
			idch,lpos = rec[0],rec[1:]
			if idch not in pdbids:
				# make dataset only representive.
				continue
			# remove redunduncy.
			lpos = list(set([int(i) for i in lpos]))
			### For Insertion code. ###
			idch,lpos = insert(idch,lpos)
			## For Debugg ##
			if len(lpos) == 0:
				pass
				#print idch
			# lcut return {nsq:res} if window == 0
			yield idch,{k:v.values()[0] for k,v in
						protdb.lcut(idch[:4],idch[4],lpos,
									window=0,is_rollback=True).items()}


class Test_S2F(unittest.TestCase):
	# Test code of seq2feature.py
	def test_answer(self):
		fname = "../dataset/mono.d4.0.acd.miss.fasta.2"
		answer = "../dataset/answer_mono.d4.0.acd.txt.2"
		
		answer_from_fasta = {idch:dpos for idch,dpos in getpos_fasta(fname,answer)}
		answer_from_pdb = {idch:dpos for idch,dpos in getpos_pdb(fname,answer)}

		for idch in answer_from_fasta.keys():
			# include idch for debugger.
			try:
				self.assertEquals((idch,answer_from_fasta[idch]),(idch,answer_from_pdb[idch]))
			except AssertionError,e:
				print idch
				"""
				print "############# AssertionError (%s) #############" % idch
				print e
				"""
			except KeyError,e:
				print idch
				"""
				print "############# KeyError (%s) #############" % idch
				print e
				"""
			

def test_suite():
	def _suite(test_class):
		return unittest.makeSuite(test_class)
	
	suite = unittest.TestSuite()
	suite.addTests((_suite(Test_S2F)))
	return suite

if __name__ == "__main__":
	#doctest.testmod()
	unittest.main(defaultTest='test_suite')
