package liir.nlp.nnsrl.features;

import liir.nlp.nnsrl.ml.Instance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by quynhdo on 30/09/15.
 */
public abstract class Feature implements Comparable<Feature> {

    List<String> vocabulary;

    FeatureName name;

    String unkown_value="!UNKOWN!";

    List<List<Integer>> value_caches;

    public Feature(FeatureName name){
        this.name=name;
        vocabulary = new ArrayList<String>();
        vocabulary.add(unkown_value);

    }

    //load a vob
    public void loadFromFile(String path) throws IOException {
        vocabulary.clear();
        List<String> vals = Files.readAllLines(Paths.get(path));
        for (String v : vals){
            if (! v.matches("\\s*"))
                vocabulary.add(v);

        }


    }


    //load a vob
    public void loadCacheFromFile(String path) throws IOException {
        this.value_caches=new ArrayList<>();
        List<String> vals = Files.readAllLines(Paths.get(path));
        for (String v : vals){
           String[] tmps =v.split("\\s+");
            if (tmps.length>0){
                if (tmps[0].equals("_"))
                {
                    List<Integer> vi = new ArrayList<>();
                    value_caches.add(vi);
                }
                else{
                    List<Integer> vi = new ArrayList<>();

                    for (int i=0;i<tmps.length;i++)
                    {
                        String[] tmps1= tmps[i].split(":");
                        if (tmps1.length==2){
                            int k =  Integer.parseInt(tmps1[1]);
                            if (k>0)
                                vi.add(Integer.parseInt(tmps1[0]));
                        }
                    }
                    value_caches.add(vi);
                }
            }

        }


    }

    public FeatureName getName() {
        return name;
    }

    public List<String> getVocabulary() {

        return vocabulary;
    }

    public int getSize()
    {
        return vocabulary.size();
    }

    public abstract Object getFeatureValue(Object data);

    public void addFeatureValueToVocabulary(String newval){
        if (! vocabulary.contains(newval))
            vocabulary.add(newval);
    }


    public void addFeatureValueToVocabulary(List<String> newval){
        newval.forEach(v -> addFeatureValueToVocabulary(v));
    }

    public void addFeatureValueToVocabulary(Object newval){

        if (newval == null){
            addFeatureValueToVocabulary("!null!");
        }

        if (newval instanceof  String)
            addFeatureValueToVocabulary((String) newval);

        if (newval instanceof List)
        {
            ((List)newval).forEach( v -> {
                if (v instanceof  String)
                    addFeatureValueToVocabulary((String )v);
            });
        }


        if (newval instanceof Set)
        {
            ((Set)newval)


                    .forEach(v -> {
                        if (v instanceof String)
                            addFeatureValueToVocabulary((String) v);
                    });
        }

    }


    /***
     * represent value as 1-hot vector in feature value space
     * @param val - value of the single feature
     * @return One-Hot vector representation of val
     */
    public float[] asOneHotVector(String val){

        float[] v = new float[vocabulary.size()];
        if (vocabulary.contains(val)){
            v[vocabulary.indexOf(val)]=1;
        }
        else
        v[vocabulary.indexOf(unkown_value)]=1;

        return v;

    }


    /***
     * represent value as 1-hot vector in feature value space
     * @param val - value of the set feature
     * @return One-Hot vector representation of val
     */
    public float[] asOneHotVector(List<String> val){

        float[] v = new float[vocabulary.size()];


        for (String s : val) {
                if (vocabulary.contains(s))
                v[vocabulary.indexOf(s)]=v[vocabulary.indexOf(s)]+1;

            else
                v[vocabulary.indexOf(unkown_value)]=v[vocabulary.indexOf(unkown_value)]+1;

        }

        return v;

    }




    public float[] asOneHotVector(Set<String> val){

        float[] v = new float[vocabulary.size()];


            for (String s : val) {
                    if (vocabulary.contains(s))
                        v[vocabulary.indexOf(s)]=1;

                else
                        v[vocabulary.indexOf(unkown_value)]=v[vocabulary.indexOf(unkown_value)]+1;

            }

        return v;

    }


    public float[] asOneHotVector(Object val){

       if (val instanceof  String)
           return asOneHotVector((String)val);

        if (val instanceof  List)
            return asOneHotVector((List<String>)val);

        if (val instanceof Set)
            return asOneHotVector((Set<String>)val);

        return null;
    }



    public String asSparseFormat(String val, int offset){
        String last_v = vocabulary.get(vocabulary.size()-1);

        if (val.equals(last_v))
            return String.valueOf(vocabulary.indexOf(val) + offset) + ":1\t";

        if (vocabulary.contains(val))
            return String.valueOf(vocabulary.indexOf(val) + offset) + ":1\t" +String.valueOf(vocabulary.size()-1 + offset) + ":0\t" ;

        else
            return String.valueOf(vocabulary.indexOf(unkown_value) +offset) + ":1\t" +String.valueOf(vocabulary.size()-1 + offset) + ":0\t";
    }


    public String asSparseFormat(List<String> vals, int offset){
        StringBuilder sb=new StringBuilder();

        int unknown_count=0;

        HashMap< Integer, Integer> idx=new HashMap<>();
        for (String val : vals) {
            if (vocabulary.contains(val)) {
                if (!idx.containsKey(vocabulary.indexOf(val) + offset))
                idx.put(vocabulary.indexOf(val) + offset, 1);
                else
                    idx.put(vocabulary.indexOf(val) + offset, idx.get(vocabulary.indexOf(val)+offset)+1);
            }
             //   sb.append( String.valueOf(vocabulary.indexOf(val) + offset) + ":1\t");

            else
                unknown_count++;

        }
        String last_v = vocabulary.get(vocabulary.size()-1);
        if (! vals.contains(last_v))
            idx.put(vocabulary.size()-1 + offset, 0);
        if (unknown_count>0)
            idx.put(vocabulary.indexOf(unkown_value) +offset, unknown_count);


    //    sb.append( String.valueOf(vocabulary.indexOf(unkown_value) +offset) + ":"+ String.valueOf(unknown_count)+ "\t");

        SortedSet<Integer> keys = new TreeSet<Integer>(idx.keySet());
        for (Integer key : keys) {
            sb.append(String.valueOf(key) + ":"+ String.valueOf(idx.get(key)) + " ");
        }

        return sb.toString();
    }


    public String asSparseFormat(Set<String> vals, int offset){
        StringBuilder sb=new StringBuilder();

        int unknown_count=0;
        HashMap< Integer, Integer> idx=new HashMap<>();
        for (String val : vals) {
            if (vocabulary.contains(val))
                if (!idx.containsKey(vocabulary.indexOf(val) + offset))
                    idx.put(vocabulary.indexOf(val) + offset, 1);
                else
                    idx.put(vocabulary.indexOf(val) + offset, idx.get(vocabulary.indexOf(val)+offset)+1);
            else
                unknown_count++;

        }
        String last_v = vocabulary.get(vocabulary.size()-1);
        if (! vals.contains(last_v))
            idx.put(vocabulary.size()-1+ offset, 0);



        if (unknown_count>0)
            idx.put(vocabulary.indexOf(unkown_value) +offset, unknown_count);
        //    sb.append( String.valueOf(vocabulary.indexOf(unkown_value) +offset) + ":"+ String.valueOf(unknown_count)+ "\t");

        SortedSet<Integer> keys = new TreeSet<Integer>(idx.keySet());
        for (Integer key : keys) {
            sb.append(String.valueOf(key) + ":"+ String.valueOf(idx.get(key))+ " ");
        }

        return sb.toString();
    }

    public String  asSparseFormat(Object val, int offset){

        if (val instanceof  String)
            return asSparseFormat((String) val, offset);

        if (val instanceof  List)
            return asSparseFormat((List<String>) val, offset);

        if (val instanceof Set)
            return asSparseFormat((Set<String>) val, offset);

        return null;
    }

    public List<List<Integer>> getValue_caches() {
        return value_caches;
    }

    @Override
    public int compareTo(Feature other){
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        return this.getSize() - (other.getSize());
    }

}
