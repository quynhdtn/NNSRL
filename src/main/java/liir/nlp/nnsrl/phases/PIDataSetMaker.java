package liir.nlp.nnsrl.phases;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.srl.Predicate;
import liir.nlp.nnsrl.io.CoNLL2009Reader;
import liir.nlp.nnsrl.ml.DataSet;
import liir.nlp.nnsrl.ml.Instance;
import scala.Tuple2;

import java.util.List;


/**
 * Created by quynhdo on 03/10/15.
 * make dataset to do predicate identification
 */
public class PIDataSetMaker {

    public static DataSet forTraining(List<Sentence> data, boolean forVerbal){

        DataSet ds = new DataSet();

        data.forEach(s -> s.forEach( w -> {

            if (forVerbal) {
                if (w.getPos().startsWith("V")) {
                    Instance ins = new Instance(w);
                    if (w instanceof Predicate)

                        ds.add(new Tuple2<>(ins, "Yes"));
                    else
                        ds.add(new Tuple2<>(ins, "No"));
                }

            }

        }));


        return ds;
    }


    public static DataSet forTesting(List<Sentence> data, boolean forVerbal){


        DataSet ds = new DataSet();

        data.forEach(s -> s.forEach( w -> {

            if (forVerbal) {
                if (w.getPos().startsWith("V")) {
                    Instance ins = new Instance(w);
                    ins.setUsed_for_training(false);
                    if (w instanceof Predicate)

                        ds.add(new Tuple2<>(ins, "Yes"));
                    else
                        ds.add(new Tuple2<>(ins, "No"));
                }

            }

        }));


        return ds;
    }
}
