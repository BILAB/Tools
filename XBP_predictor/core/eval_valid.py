import os
import random
import util

from core import seq2feature

#DIR = "/net2/presto-users/masaki070540/code/xbind/"
DIR = "./"

log_path = lambda name,window,c,g,no,idch:DIR + "tmp/svm/fold_whole/%s.%s.%s.%s.log.%s/%s.log" % (
    name,window,c,g,no,idch)

starts = lambda fname:{idch:start for idch,start,seq in seq2feature.fasta2seq(fname)}

def _readliner(line):
	rec = line.strip().split()
	idch = rec[0].split(':')[0]
	val = float(rec[2])
	return idch,val

def iter_desc(name,window,c,g,idch):
	fname = get_log(name,window,c,g,idch)
	# For get_log() bagg. ( Test didn't execute using All proteins.
	if fname is not None:
		with open(fname) as fp:
			for idch,val in (_readliner(line) for line in iter(fp.readline,"")):
				yield val

def get_log(name,window,c,g,idch):
	for no in range(5):
		if os.path.exists(log_path(name,window,c,g,no,idch)):
			return log_path(name,window,c,g,no,idch)
	# Test didn't execute using All proteins.
	#raise IOError,"log file is not eixit. :%s,%s,%s,%s,%s" % (name,window,c,g,idch)

#### For Positive ####

def iter_idch2result(idch,name,window,c,g,fans,fname):
	sgr_ans = util.ans(fans)
	start = starts(fname)[idch]
	for pos,val in enumerate(iter_desc(name,window,c,g,idch)):
		yield pos + start,sgr_ans.isans(idch,pos + start),val

def iter_extr_sect(idch,name,window,c,g,fans,fname):
	# Extracnt desion values near binding site in 101 residue.
	# extr_sect[50] == binding residue.
	dec_vals = [val for pos,isans,val in iter_idch2result(idch,name,window,c,g,fans,fname)]
	
	for cnt,(pos,isans,val) in enumerate(iter_idch2result(idch,name,window,c,g,fans,fname)):
		if isans:
			# Python's Specification. if you want to get the element of n ~ m. list[n:m + 1]
			vec = dec_vals[max(0,cnt - 50):min(len(dec_vals),cnt + 50) + 1]
			if cnt  - 50 < 0:
				# if return 0 on max(0,cnt - 50)
				yield [None for i in range(101 - len(vec))] + vec
			elif cnt + 50 >= len(dec_vals):
				yield vec + [None for i in range(101 - len(vec))]
			else:
				yield vec

#### For Negative ####

def iter_neg(idch,name,window,c,g,fname):
	for pos,val in enumerate(iter_desc(name,window,c,g,idch)):
		yield pos,False,val

def iter_extr_sect4neg(idch,name,window,c,g,fname,size = 5):
	dec_vals = [val for pos,isans,val in iter_neg(idch,name,window,c,g,fname)]
	_indx = [i for i in range(1,len(dec_vals))]
	random.shuffle(_indx)
	choiced = _indx[:size]
	
	for cnt,(pos,isans,val) in enumerate(iter_neg(idch,name,window,c,g,fname)):
		if cnt in choiced:
			# Python's Specification
			vec = dec_vals[max(0,cnt - 50):min(len(dec_vals),cnt + 50) + 1]
			if cnt  - 50 < 0:
				# if return 0 on max(0,cnt - 50)
				yield [None for i in range(101 - len(vec))] + vec
			elif cnt + 50 >= len(dec_vals):
				yield vec + [None for i in range(101 - len(vec))]
			else:
				yield vec

def summary_val(idch,name,window,c,g,fans,fname):
	# Error if idch == 1do3A
	sect_vals = [vals for vals in iter_extr_sect(idch,name,window,c,g,fans,fname)]
	for i in range(101):
		_vals = [vals[i] for vals in sect_vals if vals[i] is not None]
		if len(_vals) > 0:
			yield sum(_vals)/float(len(_vals))
		else:
			yield None

def summary_val4neg(idch,name,window,c,g,fname):
	# Error if idch == 1do3A
	sect_vals = [vals for vals in iter_extr_sect4neg(idch,name,window,c,g,fname)]
	for i in range(101):
		_vals = [vals[i] for vals in sect_vals if vals[i] is not None]
		if len(_vals) > 0:
			yield sum(_vals)/float(len(_vals))
		else:
			yield None
