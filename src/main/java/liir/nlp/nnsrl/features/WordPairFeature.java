package liir.nlp.nnsrl.features;

import liir.nlp.core.representation.Word;
import liir.nlp.utils.*;
import scala.Tuple2;

/**
 * Created by quynhdo on 02/10/15.
 */
public class WordPairFeature extends Feature {

    WordPairData pd;

    public WordPairFeature(FeatureName name, WordPairData pd) {
        super(name);
        this.pd = pd;
    }

    public Object getFeatureValue(Object data){

        if (data instanceof Tuple2){

            Word w1= (Word) ((Tuple2) data)._1();
            Word w2= (Word) ((Tuple2) data)._2();
            DependencyUtil du = new DependencyUtil();
            switch (pd){
                case wordDepPath: return du.getDependencyPath(w1, w2, WordData.word);
                case lemmaDepPath: return du.getDependencyPath(w1, w2, WordData.lemma);
                case posDepPath: return du.getDependencyPath(w1, w2, WordData.pos);
                case deprelDepPath: return du.getDependencyPath(w1, w2, WordData.deprel);
                case position: return DistanceUtil.getRelativePosition(w1, w2).toString();

            }

        }

       return null;
    }


}