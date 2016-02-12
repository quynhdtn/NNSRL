package liir.nlp.nnsrl.features;

import liir.nlp.nnsrl.ml.Instance;
import liir.nlp.we.WEDictionary;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by quynhdo on 03/10/15.
 * The feature is formed by combination of other features
 */
public class CojoinFeature extends  Feature{

    List<Feature> elements;

    String join_symbol = "_";

    public CojoinFeature(FeatureName name){
        super(name);
     //   vocabulary = new ArrayList<>();
        elements = new ArrayList<>();
    }


    public void addFeature(Feature f){
        elements.add(f);
    }

    public void setJoin_symbol(String join_symbol) {
        this.join_symbol = join_symbol;
    }

    public Object getFeatureValue(Object data){
        StringBuilder sb = new StringBuilder();
        ArrayList<Object> vals =new ArrayList<>();



        elements.forEach(f -> {
            Object v = f.getFeatureValue(data);
            vals.add(v);

        });

        if(vals.size()==2){

            if ((vals.get(0) instanceof String) && (vals.get(1) instanceof  String))
                return vals.get(0) + join_symbol + vals.get(1);

            Object lval = null;
            String sval = null;
            if ((vals.get(0) instanceof Set || vals.get(0) instanceof List) && (vals.get(1) instanceof  String)){
                lval=vals.get(0);
                sval=(String)vals.get(1);
            }
            if ((vals.get(1) instanceof Set || vals.get(1) instanceof List) && (vals.get(0) instanceof  String)){
                lval=vals.get(1);
                sval=(String)vals.get(0);
            }

            if (lval !=null){
                if (lval instanceof List)
                {
                    List<String> rs= new ArrayList<>();
                    for (Object v : (List)lval){
                        rs.add(v.toString() + join_symbol + sval);
                    }

                    return rs;
                }

                if (lval instanceof Set)
                {
                    Set<String> rs= new TreeSet<>();
                    for (Object v : (Set)lval){
                        rs.add(v.toString() + join_symbol + sval);
                    }

                    return rs;
                }
            }




        }



        return null;
    }

    public Object getFeatureValue(Object data, HashMap<FeatureName, Object> featureCaches){
        StringBuilder sb = new StringBuilder();
        ArrayList<Object> vals =new ArrayList<>();



        elements.forEach(f -> {
                Object v=null;
            if (featureCaches.containsKey(f.getName()))
                v= featureCaches.get(f.getName());
            else v = f.getFeatureValue(data);
            vals.add(v);

        });

        if(vals.size()==2){

            if ((vals.get(0) instanceof String) && (vals.get(1) instanceof  String))
                return vals.get(0) + join_symbol + vals.get(1);

            Object lval = null;
            String sval = null;
            if ((vals.get(0) instanceof Set || vals.get(0) instanceof List) && (vals.get(1) instanceof  String)){
                lval=vals.get(0);
                sval=(String)vals.get(1);
            }
            if ((vals.get(1) instanceof Set || vals.get(1) instanceof List) && (vals.get(0) instanceof  String)){
                lval=vals.get(1);
                sval=(String)vals.get(0);
            }

            if (lval !=null){
                if (lval instanceof List)
                {
                    List<String> rs= new ArrayList<>();
                    for (Object v : (List)lval){
                        rs.add(v.toString() + join_symbol + sval);
                    }

                    return rs;
                }

                if (lval instanceof Set)
                {
                    Set<String> rs= new TreeSet<>();
                    for (Object v : (Set)lval){
                        rs.add(v.toString() + join_symbol + sval);
                    }

                    return rs;
                }
            }




        }



        return null;
    }
}
