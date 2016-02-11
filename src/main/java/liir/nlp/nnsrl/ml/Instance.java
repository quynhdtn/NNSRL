package liir.nlp.nnsrl.ml;

import liir.nlp.core.representation.Word;
import liir.nlp.nnsrl.features.CojoinFeature;
import liir.nlp.nnsrl.features.Feature;
import liir.nlp.nnsrl.features.FeatureName;
import scala.Tuple2;

import java.util.*;

/**
 * Created by quynhdo on 02/10/15.
 */
public class Instance {

    HashMap<FeatureName, Object> featureCaches;

    Object val;

    boolean used_for_training=true;



    public Instance(Object val){
        featureCaches = new HashMap<>();
        this.val = val;
    }


    public void setUsed_for_training(boolean used_for_training) {
        this.used_for_training = used_for_training;
    }

    public void extractFeature(Feature f){
        Object v = null;
        /*
        if (f instanceof  CojoinFeature)
            v = ((CojoinFeature) f).getFeatureValue(this.val, featureCaches);
        else
*/
         v = f.getFeatureValue(this.val);
        if (v == null)
            v = "!null!";

        if (this.used_for_training)
            f.addFeatureValueToVocabulary(v);

        featureCaches.put(f.getName(), v);
    }



    public Object getFeature(FeatureName fn){
        if (featureCaches.containsKey(fn))
            return featureCaches.get(fn);
        else
            return null;
    }

    public void printFeatureVals(List<Feature> fl){
       fl.forEach(f -> System.out.print(featureCaches.get(f.getName()).toString() + "\t"));
    }

    public void printFeatureValsAs1HotVector(List<Feature> fl){
        fl.forEach(f -> System.out.print(Arrays.toString(f.asOneHotVector(featureCaches.get(f.getName()))) + "\t"));

        fl.forEach(f -> f.asOneHotVector(featureCaches.get(f.getName())));
    }


    public Float[] asFeatureArray(List<Feature> fl){

        ArrayList<Float> arr = new ArrayList<>();

        fl.forEach(f -> {float[] vals = f.asOneHotVector(featureCaches.get(f.getName()));

        for (int j=0;j<vals.length;j++)
            arr.add(vals[j]);

        });

        Float[] rs = new Float[arr.size()];

        arr.toArray(rs);
        return rs;
    }


    // get the information about the data instance, position, id in the dataset
    public String getInfo(){
        StringBuilder sb=new StringBuilder();
        if (val instanceof Word){

            sb.append(((Word) val).getSentence().getId());
            sb.append("\t");
            sb.append(((Word) val).getId());

        }

        if (val instanceof Tuple2){
            sb.append(((Word) ((Tuple2) val)._1()).getSentence().getId());
            sb.append("\t");
            sb.append(((Word) ((Tuple2) val)._1()).getId());
            sb.append("\t");
            sb.append(((Word) ((Tuple2) val)._2()).getId());
        }



        return sb.toString();
    }

    public Object getVal() {
        return val;
    }
}
