__author__ = 'quynhdo'
import scipy
import numpy as np
def load_file(p):
    f= open(p, "r")
    data=[]
    labels=[]

    lines= f.readlines()
    max_dim=-1



    for line in lines:
        tmps = line.strip().split()
        if (len(tmps)>1):
            labels.append(tmps[0])
            dl={}
            for  idx in range(1, len(tmps)) :
                elems= tmps[idx].split(":")
                if (len(elems)==2):
                    dl[int(elems[0])-1]=float(elems[1])
                    if (int(elems[0])> max_dim):
                        max_dim=int(elems[0])
            data.append(dl)

    dataarr = scipy.sparse.lil_matrix((len(labels), max_dim))

    for i in range(len(data)):
        dl = data[i]
        for itm in dl:
            dataarr[i,itm]= dl[itm]

    return (dataarr.todense(), np.asarray(labels))


