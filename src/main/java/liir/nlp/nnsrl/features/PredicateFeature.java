package liir.nlp.nnsrl.features;

import liir.nlp.core.representation.Word;
import liir.nlp.core.representation.Predicate;
import liir.nlp.utils.PredicateData;
import liir.nlp.utils.WordData;
import liir.nlp.utils.WordExtractor;
import scala.Tuple2;


/**
 * Created by quynhdo on 02/10/15.
 */
public class PredicateFeature extends Feature {

    PredicateData pd;
    int dataElementPosition;

    public PredicateFeature(FeatureName name, PredicateData pd,  int dataElementPosition) {
        super(name);
        this.pd = pd;
        this.dataElementPosition = dataElementPosition;
    }

    public Object getFeatureValue(Object data){

        Predicate p=null;
        if (data instanceof Predicate) {
           p = (Predicate)data;

        }

        else if ( data instanceof Tuple2){
            if (dataElementPosition == 0)
                p =  (Predicate)  ((Tuple2) data)._1();
            else
                p=  (Predicate)  ((Tuple2) data)._2();
        }

        if (p == null)
            return null;

        switch (pd){
            case sense: return p.getSense();

        }
        return null;
    }
}
