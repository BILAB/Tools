## Support For Other learning method.

import time
import random
import sqlite3

from svm import svm
from svm import svmutil
from core import util
from core import seq2feature
from core import pssm2feature2

#from sklearn.svm import SVC
import numpy


def crossed(groups,ngroups):
	while len(groups) > 1:
		yield groups.pop() + ngroups.pop(),groups + ngroups

def split_dtst(lid,k = 5,seed = None):
	"""
	>>> len([i for i in range(10,1000) if len(split_dtst(range(i))) != 5])
	0
	
	## PASS:split dataset into k part.

	>>> len(_recur_list(split_dtst(range(144))))
	144

	>>> print [len(i) for i in split_dtst(range(144))]
	None
	>>> print [len(i) for i in split_dtst(range(143))]
	None
	>>> print [len(i) for i in split_dtst(range(142))]
	None
	>>> print [len(i) for i in split_dtst(range(141))]
	None
	>>> print [len(i) for i in split_dtst(range(140))]
	None
	"""
	if seed is not None:
		# initalize seed.
		random.seed(seed)

	random.shuffle(lid)
	n,m = divmod(len(lid),k)
	# amari no kazu dake +1 suru.
	_indx = [i*n + min(cnt + 1,m) for cnt,i in enumerate(range(1,k+1))]
	#_indx = [i*n for i in range(k + 1 - m)] + [i*n + 1 for i in range(k + 1 - m,k + 1)]
	#_indx[k] += m - 1 
	# !!! list[start:end] is not contained last element. 
	_indx[-1]+= 1
	start = 0
	divided = []
	for end in _indx:
		divided.append(lid[start:end])
		start = end
	return divided

def _recur_list(rlist):
	"""
	>>> _recur_list([[1,2,3],4,[5,6,[7,8,9,[10]]]])
	>>> [1,2,3,4,5,6,7,8,9,10]
	"""
	out = []
	for i in rlist:
		if isinstance(i,list):
			out += _recur_list(i)
		else:
			out.append(i)
	return out


def fold(ids,k = 5,seed = None):
	# !!! must be same length groups and ngroups !!!
	"""
	>>> [(i,j) for i,j in fold([[i] for i in range(5)])]
	None
	"""
	# split dataset into k part
	groups = split_dtst(ids,k,seed)
	
	for i in range(k):
		test = groups[i]
		train = groups[:i] + groups[i+1:]
		if isinstance(test,list):
			test = _recur_list(test)
		if isinstance(train,list):
			train = _recur_list(train)
		yield test,train

def lbl_iter(test):
    for key,i in test:
        for j in i.values():
            yield j[0]

def data_iter(test):
    for key,i in test:
        for j in i.values():
            yield j[1]


def id_iter(test):
    for key,i in test:
        for j in i.keys():
            yield key + ':' + str(j)


def test2svm_prob(test):
    ltest = [l for l in lbl_iter(test)]
    dtest = [d for d in data_iter(test)]
    itest = [i for i in id_iter(test)]
	
    return ltest,dtest,itest


def train2svm_prob(train):
	ltrain = [l for l in lbl_iter(train)]
	dtrain = [d for d in data_iter(train)]
	itrain = [i for i in id_iter(train)]

	return svm.svm_problem(ltrain,dtrain),itrain


class valid(object):
	# For 2 class SVM.
	def __init__(self,name,dirname,fname,fans):
		# name is used as log file name
		# dirname is location saving log file
		self._name = name
		self._dir = dirname
		#pssm = pssm2feature2.pssm(fname)
		self._starts = {idch:start for idch,start,seq in seq2feature.fasta2seq(fname)}
		if fans is not None:
			self._ans = util.ans(fans)
	
	def valid(self,datasets,clf,method = fold,filter = None,seed = None):
		if seed is None:
			seed = time.time()
		saving_seed = "%s/log/%s.log.seed" % (self._dir,self._name)
		with open(saving_seed,"w") as fp:
			print "Using seed: %s" % seed
			fp.write("seed:%f\n" % seed)
		# Should groups and ngroups be idch ?
		groups = [(test,train) for test,train in method(datasets.pids,seed = seed)]
		
		for cnt,pdtsts in enumerate(groups):
			# cnt is number of cluster.
			tst_d_lbl,tst_lbl,tst_dtst = datasets.mkTest(part_ids=pdtsts[0])
			trn_d_lbl,trn_lbl,trn_dtst = datasets.mkTrain(part_ids=pdtsts[1])
			
			print "start %s validation" % (cnt)

			tst_lbl,tst_dtst = numpy.array(tst_lbl),numpy.array(tst_dtst)
			trn_lbl,trn_dtst = numpy.array(trn_lbl),numpy.array(trn_dtst)
			
			if filter is not None:
				# For Univarable Feature Selection.
				# For supervised filter.
				filter.fit(trn_dtst,trn_lbl)
				tst_dtst = filter.transform(tst_dtst)
				trn_dtst = filter.transform(trn_dtst)
			clf.fit(trn_dtst,trn_lbl)
			# random forest predict probability of class
			dec_vals = clf.decision_function(tst_dtst)
			#dec_vals = clf.predict_proba(tst_dtst)
			#dec_vals = list(dec_vals)
			self._save_log(tst_d_lbl,dec_vals,cnt)
	
	def _iter_result(self,tst_d_lbl,dec_vals,cnt):
		for idch_pos,dec_val in zip(tst_d_lbl,dec_vals):
			idch,pos = idch_pos
			# For 2class problem
			#dec_val1,dec_val2 = dec_val
			dist = self._ans.get_dist(pos,idch)
			
			dec_val = list(dec_val)[0]
			yield idch,pos,dist,dec_val,cnt
	
	def _save_log(self,tst_d_lbl,dec_vals,cnt):
		saving_db = "%s/log/%s.log.db" % (self._dir,self._name)
		with sqlite3.connect(saving_db) as con:
			#is_ans bool, answer is written in answer database.
			# We can summrize below sql statement.
			# ( where valid.idch = answr.idch and valid.pos = answer.bp + answer.start )
			
			mktbl = """
			create table valid (
			idch text,
			pos interger,
			dist interger,
			dec_val real,
			cnt interger

			);"""
			
			man_db = mkDB(self._iter_result(tst_d_lbl,dec_vals,cnt),mktbl,"valid")
			if cnt == 0:
				man_db.mkTable(con)
			
			man_db.updtDB(con)
			con.commit()
	
	def create_model(self,datasets,opt,opp,part_ids = None):
		# Should groups and ngroups be idch ?
		if part_ids is None:
			part_ids = datasets.pids
		
		trn_d_lbl,trn_lbl,trn_dtst = datasets.mkTrain(part_ids=part_ids)
		ptrn = svm.svm_problem(trn_lbl,trn_dtst)
		
		print "create model ..."
		#opt = svm.svm_parameter(opt)
		model = svmutil.svm_train(ptrn,opt)
		# create saving direcotry
		#self._mkdir(cnt)
		# create log files
		#self._save_log(itest,plbl,pval,cnt)
		model_name = "%s/model/%s.model" % (self._dir,self._name)
		svmutil.svm_save_model(model_name, model)

class mkDB(object):
	def __init__(self,iter_record,mktable,table_name):
		# iter_record is iterator of yielding records.
		# mktable is sql statement of create table
		# table_name is created table name by mktable().
		
		self._iter_rec = iter_record
		self._mkTable = mktable
		self._tbl = table_name
	
	def mkTable(self,con):
		con.execute(self._mkTable)
		
	def updtDB(self,con):
		for record in self._iter_rec:
			sql = lambda record,table : "insert into %s values ( " % (table) + ", ".join(["?" for i in range(len(record))]) + ");"
			#print record
			#print sql(record,self._tbl)
			con.execute(sql(record,self._tbl),record)


if __name__ == "__main__":
	import doctest
	doctest.testmod()
