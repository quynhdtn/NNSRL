from theano.tensor.shared_randomstreams import RandomStreams
from liir.python.ml.nn.Functions import NegativeLogLikelihoodCostFunction, TanhActivateFunction, SoftmaxActivateFunction, \
    SigmoidActivateFunction, SquaredErrorCostFunction
from liir.python.ml.nn.Layer import Layer
import numpy as np
__author__ = 'quynhdo'
from liir.python.ml.nn.NNNet import NNNet
import math
class Mlp(NNNet):
    def __init__(self, layer_units, activate_function=TanhActivateFunction, cost_function=NegativeLogLikelihoodCostFunction, idx="", x=None, xtype="matrix", y=None):
        '''

        :param layer_units: list of integers - size of layers
        :param activate_function: the activate function for the hidden layers, the final connection should has the softmax activate function
        :param cost_function:
        :param idx:
        :param x:
        :param y:
        :return:
        '''
        if len(layer_units)==2:
            nHidden = int( math.sqrt(layer_units[0] * (layer_units[1]+2)) + 2 *math.sqrt(layer_units[0] / (layer_units[1]+2)) )
            layer_units.insert(1, nHidden)

        assert len(layer_units) >= 3 # the MLP should has at least 3 layers


        NNNet.__init__(self, costfunction=cost_function, idx=idx, x=x, y=y, x_type=xtype, otype="softmax")
        self.addLayer(Layer(numNodes=layer_units[0], ltype = Layer.Layer_Type_Input)) # create the input layer
        for i in range(1, len(layer_units)-1):
            self.addLayer(Layer(numNodes=layer_units[i], ltype = Layer.Layer_Type_Hidden)) # create hidden layers
        self.addLayer(Layer(numNodes=layer_units[len(layer_units)-1], ltype = Layer.Layer_Type_Output)) # create output layers
        ### create connections
        rng = np.random.RandomState(89677)

        for i in range(1, len(layer_units)-1):
            if activate_function is SigmoidActivateFunction:
                w_lamda = 4 * np.sqrt(6. / (layer_units[i-1]+ layer_units[i]))

            if activate_function is TanhActivateFunction:
                w_lamda =  np.sqrt(6. / (layer_units[i-1]+ layer_units[i]))

            else:
                w_lamda =  np.sqrt(6. / (layer_units[i-1]+ layer_units[i]))


            self.createConnection(scr=self.layers[i-1], dst=self.layers[i], activate_func=activate_function, w_lamda=w_lamda, rng=rng)
        w_lamda = 4 * np.sqrt(6. / (layer_units[len(layer_units)-2]+ layer_units[len(layer_units)-1]))

        self.createConnection(scr=self.layers[len(layer_units)-2], dst=self.layers[len(layer_units)-1], activate_func=SoftmaxActivateFunction, w_lamda=w_lamda, rng=rng)

        self.start()

    def fit(self, train_data, train_data_label,  batch_size, training_epochs, learning_rate, validation_data=None, validation_data_label=None,save_model_path= None,regularization_name=None, regularization_lambda=0.0001):
       NNNet.fit(self, train_data=train_data, train_data_label=train_data_label, training_epochs=training_epochs,
                 learning_rate=learning_rate, save_model_path=save_model_path, batch_size=batch_size,
                 regularization_name=regularization_name,
                 regularization_lambda=regularization_lambda,
                 validation_data=validation_data,
                 validation_data_label=validation_data_label)


if __name__ == "__main__":

    from sklearn.datasets import load_iris
    import numpy
    iris = load_iris()
    X= iris.data
    Y = iris.target

    from sklearn import metrics
    import theano as th
    import theano.tensor as T


#    mp=Factory.buildMLPStandard(len(X[0]), len(set(Y)))

    mp=Mlp(layer_units=[len(X[0]), len(set(Y))], cost_function=NegativeLogLikelihoodCostFunction)
    from sklearn import cross_validation
    import scipy.sparse
    X_train, X_test, y_train, y_test = cross_validation.train_test_split( iris.data, Y, test_size=0.4, random_state=0)
   # X_train = scipy.sparse.csr_matrix( X_train)
#    X_test = scipy.sparse.csr_matrix(y_test)

    mp.fit(th.shared(X_train), T.cast(y_train, 'int32'), 15, 3000, 0.005)


    print( mp.evaluate(th.shared(X_test), T.cast(y_test, 'int32')) )



  #  print (y_pred)

  #  print (metrics.f1_score(y_test,y_pred))


    from sklearn import svm
    clf = svm.SVC()
    clf.fit(X_train, y_train)
    y_pred=clf.predict(X_test)
    print (metrics.f1_score(y_test,y_pred))
