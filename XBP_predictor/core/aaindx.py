DIR = '/users/masaki070540/code/xbind/dataset/aaindx/'


try:
    import cPickle as pickle
except:
    import pickle

import os

def nonrd_aa():
    related = []
    for fname in os.listdir(DIR):
        if fname.split('.')[-1] != 'dump':
            continue
        with open(DIR + fname) as fp:
            aa = pickle.load(fp)
            if aa['id'] not in related:
                if 'NA' not in aa['aaindx'].values():
                    yield aa
                related.append(aa['id'])
            for rid in aa['relation'].keys():
                if rid not in related:
                    related.append(rid)

def clust_aa():
    related = {}
    for fname in os.listdir(DIR):
        if fname.split('.')[-1] != 'dump':
            continue
        with open(DIR + fname) as fp:
            aa = pickle.load(fp)
            if len(related) < 1:
                related.update({[aa['id']]:[]})
            else:
                for repid,lsim_id in related.items():
                    if aa['id'] in lsim_id:
                        related[repid].append(aa['id'])
                related.append(aa['id'])
            
            for rid in aa['relation'].keys():
                if rid not in related:
                    related.append(rid)

def getAAindx(id):
    with open(DIR + id + '.dump') as fp:
            aa = pickle.load(fp)
    return aa
