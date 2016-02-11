package liir.nlp.nnsrl.features;

import java.util.*;

/**
 * Created by quynhdo on 28/10/15.
 * for numeric features when we 1 hot vector style, it return the vector with only 1 element which is the value of the feature
 */
public abstract class NumericFeature extends  Feature{

    public NumericFeature(FeatureName name){
        super(name);
    }


    public float[] asOneHotVector(String val){

        float[] v = new float[1];
        v[0]= Float.parseFloat(val);

        return v;

    }

    public float[] asOneHotVector(List<String> val){

        float[] v = new float[vocabulary.size()];

        for (int i= 0;i<val.size();i++)
            v[i] = Integer.parseInt( val.get(i) );

        return v;

    }



    // we doens' allow a set of numeric values
    public float[] asOneHotVector(Set<String> val){

       return null;

    }


    public String asSparseFormat(String val){
            return val;
    }


    public String asSparseFormat(List<String> vals){
        StringBuilder sb=new StringBuilder();


        for (String val : vals) {

                sb.append(val + "\t");


        }


        return sb.toString();
    }


    public String asSparseFormat(Set<String> vals){
        StringBuilder sb=new StringBuilder();


        for (String val : vals) {

            sb.append(val + "\t");


        }


        return sb.toString();
    }

    public void addFeatureValueToVocabulary(String newval){
        return;
    }


    public void addFeatureValueToVocabulary(List<String> newval){
       return;
    }

    public void addFeatureValueToVocabulary(Object newval){
        return;
    }
}
