#from liir.python.ml.nn.Functions import NegativeLogLikelihoodCostFunction
#from liir.python.ml.nn.classifiers.Mlp import Mlp

__author__ = 'quynhdo'
from sklearn import linear_model
#from sklearn.datasets import load_svmlight_file
from liir.python.ml.load_svm_file import *
import argparse
import pickle
import os
import theano as th
import theano.tensor as T

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-train", "--train", action="store_true")
    parser.add_argument("-test", "--test", action="store_true")
    parser.add_argument("model_path", help="Path to model")
    parser.add_argument("data", help="Path to data")
    parser.add_argument('output', nargs='?', default=os.getcwd()+"/output.txt")
    args = parser.parse_args()
    lgt = linear_model.LogisticRegression()

    data = load_file(args.data)

    if args.train:
        # convert labels to integers:
        labels=[]
        for x in data[1]:
            if not x in labels:
                labels.append(x)
        intdata = [labels.index(x) for x in data[1]]
  #      mp=Mlp(layer_units=[data[0].shape[1], len(set(intdata))], cost_function=NegativeLogLikelihoodCostFunction)
        lgt.fit(data[0],intdata)

  #      mp.fit(th.shared(data[0]), T.cast(intdata, 'int32'), 1, 10, 0.005)


        pickle.dump((lgt,labels), open(args.model_path,"wb"))

    if args.test:
        lgt,labels = pickle.load(open(args.model_path,"rb"))
        y_pred=lgt.predict(data[0])
        print(len(data[1]))
        f = open (args.output, "w")
        for y in y_pred:
            s = str(labels[y])

            f.write(s + "\n")
        f.close()

