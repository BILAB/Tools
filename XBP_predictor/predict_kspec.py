import argparse

from core import seq2feature
from core import feature

from libsvm import svm
from libsvm import svmutil


def k_spec(seq,k):
	total = feature.seq2frq(seq,k)
	return {dim:float(cnt)/sum(total.values()) for dim,cnt in total.items()}

def get_dataset(fname,k):
	labels = []
	features = []
	for prot_id,seq in seq2feature.parse_fasta(fname):
		labels.append(prot_id)
		features.append(k_spec(seq,k))
	return labels,features

if __name__ == "__main__":
	parser = argparse.ArgumentParser(description='Predict X binding proteins.')
	
	parser.add_argument('-model',action="store",dest="model")
	parser.add_argument('-thr',action="store",dest="thr",type = float)
	parser.add_argument('-fname',action="store",dest="fname")
	model = parser.parse_args().model
	fname = parser.parse_args().fname
	thr = parser.parse_args().thr
	
	labels,features = get_dataset(fname,2)
	model = svmutil.svm_load_model(model)

	plbl, pacc, pvals = svmutil.svm_predict([0]*len(features),features,model,"")

	for cnt,(prot_id,seq) in enumerate(seq2feature.parse_fasta(fname)):
		pval = pvals[cnt][0]
		if pval >= thr:
			print "> %s:%f" % (prot_id,pval)
			print seq
	
