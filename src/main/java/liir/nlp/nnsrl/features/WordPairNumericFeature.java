package liir.nlp.nnsrl.features;

import liir.nlp.core.representation.Word;
import liir.nlp.utils.DependencyUtil;
import liir.nlp.utils.DistanceUtil;
import liir.nlp.utils.WordData;
import liir.nlp.utils.WordPairData;
import scala.Tuple2;

/**
 * Created by quynhdo on 28/10/15.
 */
public class WordPairNumericFeature  extends NumericFeature {

    WordPairData pd;

    public WordPairNumericFeature(FeatureName name, WordPairData pd) {
        super(name);
        this.pd = pd;
    }

    public Object getFeatureValue(Object data){

        if (data instanceof Tuple2){

            Word w1= (Word) ((Tuple2) data)._1();
            Word w2= (Word) ((Tuple2) data)._2();
            DependencyUtil du = new DependencyUtil();
            switch (pd){
                case distance: return String.valueOf(DistanceUtil.getLinearPosition(w1, w2));
            }

        }

        return null;
    }



}
