from math import floor
import gzip
import StringIO

DIR = "/raid1/share/database/PDB_chain/all/pdb/"
path = lambda pdbid : "/raid1/share/database/PDB_uncompressed/all/pdb/pdb%s.ent" % (pdbid)
# For rollback PDB Database.
_back = lambda pdbid : ("/net2/presto-users/masaki070540/Desktop/binding-site/pdb/pdb%s.ent.gz" % (pdbid.lower()))

NUC = ['DA','DC','DG','DI','DT','DU','ADE','C',' DA',' DC',' DG',' DT',
       'I','U','GUN','TDR','N']

AMINO = { "MET":"M",  "ALA":"A" , "VAL":"V" ,
           "LEU":"L" , "ILE":"I" , "PRO":"P" ,
           "PHE":"F" , "TRP":"W" , "CYS":"C" ,
           "GLY":"G" , "SER":"S" , "THR":"T" ,
           "TYR":"Y" , "ASN":"N" , "GLN":"Q" ,
           "ASP":"D" , "GLU":"E" , "LYS":"K" ,
           "ARG":"R", "HIS":"H" , "UNK":"-"}

sws = {
  'ALA':8.26,'GLN':3.93,'LEU':9.66,'SER':6.55,
  'ARG':5.53,'GLU':6.75,'LYS':5.85,'THR':5.34,
  'ASN':4.06,'GLY':7.08,'MET':2.42,'TRP':1.08,
  'ASP':5.46,'HIS':2.27,'PHE':3.86,'TYR':2.92,
  'CYS':1.36,'ILE':5.97,'PRO': 4.69,'VAL':6.87
    }

hyd = ['I','V','L','F','C','M','A','G','T','S',
        'W','Y','P','H','Q','E','N','D','K','R']

kyte = {
    'I':4.5,'V':4.2,'L':3.8,'F':2.8,'C':2.5,'M':1.9,
    'A':1.8,'G':-0.4,'T':-0.7,'W':-0.9,'S':-0.8,
    'Y':-1.3,'P':-1.6,'H':-3.2,'E':-3.5,'Q':-3.5,'D':-3.5,
    'N':-3.5,'K':-3.9,'R':-4.5
    }


def rollback(pdbid):
	with open(_back(pdbid)) as fp:
		sf = StringIO.StringIO(fp.read())
		with gzip.GzipFile(fileobj = sf) as fpdb:
			for rec in (line.strip() for line in iter(fpdb.readline,"")):
				yield rec

def _readliner(pdbid):
    with open(path(pdbid.lower()),'r') as fp:
        for rec in (line.strip() for line in iter(fp.readline,"")):
			yield rec

def lcut(pdbid,ch,lpos,window=1,is_rollback = False):
	# ToDo: NMR has many structure.
	"""
    >>> lcut('1acz','A',[510,511,512],window=2)
    None
	"""
	# PDB database rollback if rollback == True
	if is_rollback:
		readliner = rollback
	else:
		readliner = _readliner

	#lpos.sort()
	seq = {}
	for line in readliner(pdbid):
		if line[0:4] not in ['ATOM','TER ']:
			continue
		if line[0:3] == 'TER':
			if len(seq) > 0:
				break
			else:
				continue
                
		#if line[21] not in [ch,ch.upper()]:
		if line[21] != ch:
			continue
		nsq = int(line[22:26].strip())
		for pos in sorted(lpos):
			if floor(pos - window/2.0) <= nsq <= floor(pos + window/2.0):
				res  = line[17:20]
				#if pos not in seq.keys():
				if not seq.has_key(pos):
					seq.update({pos:{nsq:AMINO[res.strip()]}})
				#elif nsq not in seq[pos].keys():
				elif not seq.has_key(nsq):
					try:
						seq[pos].update({nsq:AMINO[res.strip()]})
					except KeyError:
						seq[pos].update({nsq:'-'})
		if floor(max(lpos) + window/2.0) <= nsq:
			break
	return seq

    
def whole(pdbid,ch):
    #lfasta = []
    seq = {}
    with open(path(pdbid.lower()),'r') as fp:
        for line in iter(fp.readline,""):
            if line[0:4] not in ['ATOM','TER ']:
                continue
            if line[0:3] == 'TER':
                if len(seq) > 0:
                    break
                else:
                    continue
            if line[21] != ch:
                continue
            nsq = int(line[22:26].strip())
            res  = line[17:20]

            if nsq not in seq.keys():
                try:
                    seq.update({nsq:AMINO[res.strip()]})
                except KeyError:
                    seq.update({nsq:'-'})
        return seq

def init_amino():
    return {'SER':0,'ASP':0,'CYS':0,'ASN':0,'GLY':0,
            'MET':0,'TYR':0,'PHE':0,'HIS':0,'ARG':0,
            'LYS':0,'VAL':0,'LEU':0,'ALA':0,'ILE':0,
            'THR':0,'PRO':0,'GLU':0,'GLN':0,'TRP':0,
            'UNK':0}

def init_AA(size=1,aalist = None):
	"""
    >>> init_AA(size=2)
    {'A':0,'C':0,'E':0,'D':0,'G':0,'F':0,'I':0,'H':0,'K':0,'M':0,'L':0,'N':0,'Q':0,'P':0,'S':0,'R':0,'T':0,'W':0,'V':0,'Y':0}
	
	"""
	
	if aalist is None:
		aalist = ['M','A','V','L','I','P',
					  'F','W','C','G','S','T',
					  'Y','N','Q','D','E','K',
					  'R','H']
	
	nspace = aalist
	for i in range(size - 1):
		new = []
		for res in nspace:
			new += [res + i for i in aalist]
		nspace = new
	return {i:0 for i in nspace}


def _test():
    import doctest
    doctest.testmod()
