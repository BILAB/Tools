import random
#from core import feature
import feature
#from core import mkftr
import mkftr

def mkvec(ftr,seq,window,pos,pssm = False):
	# ftr is the function make dict like "{pos:value for i in dimension}"
	n = seq[max(0,int(pos - window/2.0)):pos]
	c = seq[pos + 1:min(int(pos + window/2.0) + 1,len(seq))]
	if pssm:
		vec = ftr([-100 for i in range(int(window/2 - len(n)))] + n + [-100 for i in range((int(window/2.0 - len(c))))])
	else:
		vec = ftr('+'*(int(window/2 - len(n))) + n + seq[pos] + c + '-'*(int(window/2.0 - len(c))))
	return vec

def _mkdtst(window,ftr,represent = 'dataset/sugar.txt',
			answer = 'dataset/answer_sugar.txt',
			name = 'sugar',
			flg = True,pssm = False):
	
	def ans2int(ans,idch,pos):
		if ans.isans(idch,pos):
			return 1
		return -1

	with open(represent) as fp:
		lid = [line.strip() for line in iter(fp.readline,'')]
	ans_sgr = mkftr.ans(answer)
	# ? Dose idchoice need ?
	for cnt,group in enumerate(mkftr.idchoice(lid)):
		for idch,start,seq in mkftr.slider(group,name,pssm = pssm):
			idch = idch.strip()
			# 2012/1/31 pos -> pos + start
			# !!! now nodyfying !!!
			yield idch,{pos:(ans2int(ans_sgr,idch,start + pos),mkvec(ftr,seq,window,pos,pssm = pssm))
						for pos in range(len(seq)) if ans_sgr.isans(idch,start + pos) or not flg}
			# !!! now modfying !!!
			
			"""
			for pos in range(len(seq)):
				if ans_sgr.isans(idch,start + pos):
					yield idch,pos,+1,mkvec(ftr,seq,window,pos)
				elif not flg:
					yield idch,pos,-1,mkvec(ftr,seq,window,pos)
			"""

def mkdtset(window,ftr,name):
	with open('dataset/svm/%s.dtset.%s' % (name,str(cnt)),'w') as dtset:
		with open('dataset/svm/%s.log.%s' % (name,str(cnt)),'w') as log:
			for lbl,vec in mkvec(ftr,seq,window,pos):
				if lbl > 0:
					dtset.write('+1 ' + ' '.join(vec) + '\n')
					log.write('+\t' + '\t'.join([idch,str(start),str(pos)]) + '\n')
				else:
					dtset.write('-1 ' + ' '.join(vec) + '\n')
					log.write('-\t' + '\t'.join([idch,str(start),str(pos)]) + '\n')

def _mkneg(fname,window,ftr,pssm = False):
	with open(fname) as fp:
		neg = [i[0] for i in mkftr.getseq(fp)]
		# ? didn't idchoice() need ? 
		for cnt,group in enumerate(mkftr.idchoice(neg)):
			fp.seek(0)
			for negid,seq in mkftr.getseq(fp):
				if pssm:
					# For PSSM
					seq = [int(i) for i in seq.split()]
					if len(seq) < 100:
						continue
				else:
					if len(seq) < 100 or seq.find('X') > 0:
						continue
				if negid in group:
					_indx = [i for i in range(1,len(seq))]
					random.shuffle(_indx)
					yield negid,{pos:(-1,mkvec(ftr,seq,window,pos,pssm = pssm)) for pos in sorted(_indx[:5])}

def mkneg(window,ftr,name):
    with open('dataset/svm/%s.ndtset.%s' % (name,str(cnt)),'w') as dtset:
        with open('dataset/svm/%s.nlog.%s' % (name,str(cnt)),'w') as log:
            for pos,vec in _mkneg_query('dataset/negative900ver2.fasta',seq,window,ftr):
                dtset.write('-1 ' + ' '.join(vec)+ "\n")
                log.write("%s\t%s\n" % (id,str(pos)))


def aaindx():
    for window in [10,30,50]:
        mkvector(window,feature.seq2frq,'3let.%s' % (window))
        mkvector(window,lambda seq: feature.seq2aaindx(seq,'BIGC670101',null = 0),'vol.%s' % (window))
        mkvector(window,feature.seq2hyd,'hyd.%s' % (window))

