package liir.nlp.nnsrl.features;

import liir.nlp.we.WEDictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by quynhdo on 11/01/16.
 * A NLP feature will be represented as a vector
 * The other features which are not in the Word embedding dictionary will be represented as a normal 1 hot vector
 *
 */
public class WEFeature extends  Feature{  //only available for String feature
    WEDictionary weDict;
    Feature originFeature;
    boolean useWordVectorAlso= true;
    public WEFeature(FeatureName name, WEDictionary weDict, Feature originFeature, boolean useWordVectorAlso){

        super(name);
        this.weDict = weDict;
        this.originFeature = originFeature;
        this.useWordVectorAlso  = useWordVectorAlso;

    }

    public WEDictionary getWeDict() {
        return weDict;
    }

    public void setWeDict(WEDictionary weDict) {
        this.weDict = weDict;
    }

    public void loadFromFile(String path) throws IOException {
        originFeature.vocabulary.clear();
        vocabulary.clear();
        List<String> vals = Files.readAllLines(Paths.get(path));
        for (String v : vals){
            if (! v.matches("\\s*"))
            {
                originFeature.vocabulary.add(v);
                if (!weDict.containsKey(v))
                    vocabulary.add(v);   //this vob only contains words that are not in WEDict
            }
        }


    }

    public int getSize(){
        if (this.useWordVectorAlso)
            return this.weDict.getDim() + this.vocabulary.size();
        else
            return this.weDict.getDim();
    }
    public Object getFeatureValue(Object data){

        return originFeature.getFeatureValue(data);

    }


    public void addFeatureValueToVocabulary(String newval){
        if (! originFeature.vocabulary.contains(newval))
        {
            originFeature.vocabulary.add(newval);
            if (this.useWordVectorAlso)
            if (!weDict.containsKey(newval))
                vocabulary.add(newval);
        }
    }

    public float[] asOneHotVector(String val){

        float[] v = new float[getSize()];
        if (vocabulary.contains(val)){
            v[weDict.getDim() + vocabulary.indexOf( val)]=1;
        }
        else {
            if (weDict.containsKey(val)) {
                float[] wev = weDict.get(val);
                for (int j = 0; j < wev.length; j++)
                    v[j]= wev[j];
            }
            else
            v[weDict.getDim() +vocabulary.indexOf(unkown_value)] = 1;
        }

        return v;

    }

    public float[] asOneHotVector(List<String> val){

        float[] v = new float[getSize()];

        float[] wev = new float[weDict.getDim()];
        int numInWe = 0;

        for (String s : val) {
            if (vocabulary.contains(s))
                v[weDict.getDim() + vocabulary.indexOf( val)]+=1;
            else{
                if (weDict.containsKey(val)) {
                    float[] wevt = weDict.get(val);
                    for (int j = 0; j < wevt.length; j++)
                        wev[j] += wevt[j];
                    numInWe+=1;
                }
                else{
                    v[weDict.getDim() +vocabulary.indexOf(unkown_value)] += 1;
                }
            }



        }

        for (int j = 0; j < wev.length; j++)
            wev[j] = wev[j]/(float)numInWe;

        for (int j = 0; j < wev.length; j++)
            v[j]= wev[j];
        return v;

    }


    public float[] asOneHotVector(Set<String> val){

        float[] v = new float[getSize()];

        float[] wev = new float[weDict.getDim()];
        int numInWe = 0;

        for (String s : val) {
            if (vocabulary.contains(s))
                v[weDict.getDim() + vocabulary.indexOf( val)]+=1;
            else{
                if (weDict.containsKey(val)) {
                    float[] wevt = weDict.get(val);
                    for (int j = 0; j < wevt.length; j++)
                        wev[j] += wevt[j];
                    numInWe+=1;
                }
                else{
                    v[weDict.getDim() +vocabulary.indexOf(unkown_value)] += 1;
                }
            }



        }

        if (numInWe >0)
        for (int j = 0; j < wev.length; j++)
            wev[j] = wev[j]/(float)numInWe;

        for (int j = 0; j < wev.length; j++)
            v[j]= wev[j];
        return v;

    }


    public String asSparseFormat(String val, int offset){
        String last_v = vocabulary.get(vocabulary.size()-1);

        if (val.equals(last_v))
            return String.valueOf(vocabulary.indexOf(val) + offset + weDict.getDim()) + ":1\t";

        if (vocabulary.contains(val))
            return String.valueOf(vocabulary.indexOf(val) + offset+ weDict.getDim()) + ":1\t" +String.valueOf(vocabulary.size()-1 + offset+ weDict.getDim()) + ":0\t" ;

        else {

            if (weDict.containsKey(val)){
                float[] wev = weDict.get(val);
                StringBuilder sb = new StringBuilder();
                for (int j=0;j<weDict.getDim();j++)
                {
                    sb.append(String.valueOf(j + offset) + ":" + String.valueOf(wev[j]) + "\t");
                }

                sb.append(String.valueOf(vocabulary.size()-1 + offset+ weDict.getDim()) + ":0\t");
                return  sb.toString();
            }
            return String.valueOf(vocabulary.indexOf(unkown_value) + offset+ weDict.getDim()) + ":1\t" + String.valueOf(vocabulary.size() - 1 + offset+weDict.getDim()) + ":0\t";

        }


    }


    public String asSparseFormat(List<String> vals, int offset){
        StringBuilder sb=new StringBuilder();

        int unknown_count=0;

        HashMap< Integer, Float> idx=new HashMap<>();
        float[] wev = new float[weDict.getDim()];
        int numInWe = 0;

        for (String val : vals) {
            if (vocabulary.contains(val)) {
                if (!idx.containsKey(vocabulary.indexOf(val) + offset+weDict.getDim()))
                    idx.put(vocabulary.indexOf(val) + offset+weDict.getDim(), 1.f);
                else
                    idx.put(vocabulary.indexOf(val) + offset + weDict.getDim(), idx.get(vocabulary.indexOf(val)+offset+ weDict.getDim())+1);
            }
            //   sb.append( String.valueOf(vocabulary.indexOf(val) + offset) + ":1\t");

            else {

                if (weDict.containsKey(val)){

                        float[] wevt = weDict.get(val);
                        for (int j = 0; j < wevt.length; j++)
                            wev[j] += wevt[j];
                        numInWe+=1;

                }
                else
                unknown_count++;
            }

        }
        String last_v = vocabulary.get(vocabulary.size()-1);
        if (! vals.contains(last_v))
            idx.put(vocabulary.size()-1 + offset+weDict.getDim(), 0.f);
        if (unknown_count>0)
            idx.put(vocabulary.indexOf(unkown_value) +offset+weDict.getDim(), (float)unknown_count);
        //    sb.append( String.valueOf(vocabulary.indexOf(unkown_value) +offset) + ":"+ String.valueOf(unknown_count)+ "\t");


        if (numInWe >0)
        for (int j = 0; j < wev.length; j++)
        {
            wev[j] = wev[j]/(float)numInWe;
           sb.append(String.valueOf(j+offset) + ":"+ String.valueOf(wev[j]) + "\t");
        }


        SortedSet<Integer> keys = new TreeSet<Integer>(idx.keySet());
        for (Integer key : keys) {
            sb.append(String.valueOf(key) + ":"+ String.valueOf(idx.get(key)) + "\t");
        }

        return sb.toString();
    }


    public String asSparseFormat(Set<String> vals, int offset){
        StringBuilder sb=new StringBuilder();

        int unknown_count=0;

        HashMap< Integer, Float> idx=new HashMap<>();
        float[] wev = new float[weDict.getDim()];
        int numInWe = 0;

        for (String val : vals) {
            if (vocabulary.contains(val)) {
                if (!idx.containsKey(vocabulary.indexOf(val) + offset+weDict.getDim()))
                    idx.put(vocabulary.indexOf(val) + offset+weDict.getDim(), 1.f);
                else
                    idx.put(vocabulary.indexOf(val) + offset + weDict.getDim(), idx.get(vocabulary.indexOf(val)+offset+ weDict.getDim())+1);
            }
            //   sb.append( String.valueOf(vocabulary.indexOf(val) + offset) + ":1\t");

            else {

                if (weDict.containsKey(val)){

                    float[] wevt = weDict.get(val);
                    for (int j = 0; j < wevt.length; j++)
                        wev[j] += wevt[j];
                    numInWe+=1;

                }
                else
                    unknown_count++;
            }

        }
        String last_v = vocabulary.get(vocabulary.size()-1);
        if (! vals.contains(last_v))
            idx.put(vocabulary.size()-1 + offset+weDict.getDim(), 0.f);
        if (unknown_count>0)
            idx.put(vocabulary.indexOf(unkown_value) +offset+weDict.getDim(), (float)unknown_count);
        //    sb.append( String.valueOf(vocabulary.indexOf(unkown_value) +offset) + ":"+ String.valueOf(unknown_count)+ "\t");

        if (numInWe >0)
            for (int j = 0; j < wev.length; j++)
            {
                wev[j] = wev[j]/(float)numInWe;
                   sb.append(String.valueOf(j+offset) + ":"+ String.valueOf(wev[j]) + "\t");
            }


        SortedSet<Integer> keys = new TreeSet<Integer>(idx.keySet());
        for (Integer key : keys) {
            sb.append(String.valueOf(key) + ":"+ String.valueOf(idx.get(key)) + "\t");
        }

        return sb.toString();
    }

}
