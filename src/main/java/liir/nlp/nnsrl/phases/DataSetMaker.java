package liir.nlp.nnsrl.phases;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.srl.Predicate;
import liir.nlp.nnsrl.ml.DataSet;
import liir.nlp.nnsrl.ml.Instance;
import scala.Tuple2;

import java.util.HashMap;
import java.util.List;

/**
 * Created by quynhdo on 03/10/15.
 */
public class DataSetMaker {

    public static DataSet forTrainingPredicate(List<Sentence> data){

        DataSet ds = new DataSet();

        data.forEach(s -> s.forEach( w -> {
            Instance ins = new Instance(w);
            if (w instanceof Predicate)
                ds.add(new Tuple2<>(ins, "Yes"));
            else
                ds.add(new Tuple2<>(ins, "No"));

        }));


        return ds;
    }


    /*
    public static DataSet forTrainingArgument(List<Sentence> data){

        DataSet ds = new DataSet();

        data.forEach(s -> s.getPredicates().forEach(p -> {
            HashMap<String,String> arg
            for (int i = 0; i < s.size(); i++)
                if (String.valueOf(i))

        }));


        return ds;
    }
    */
}
