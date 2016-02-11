package liir.nlp.nnsrl.pipeline;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Word;
import liir.nlp.core.representation.Predicate;
import liir.nlp.nnsrl.ml.DataSet;
import liir.nlp.nnsrl.ml.Instance;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by quynhdo on 25/01/16.
 */
public class PredicateIdentification extends  Step {



    String positive_label = "1";
    String negative_label = "0";

    public  PredicateIdentification(String feature_config_folder, String srl_temp_folder, String script_path) throws IOException {

        if (!Files.exists(Paths.get(srl_temp_folder + File.separator + "pi")))
            Files.createDirectory(Paths.get(srl_temp_folder + File.separator + "pi"));

        this.addProblem("V", feature_config_folder + File.separator + "pi" + File.separator +"V" ,
                srl_temp_folder + File.separator + "pi" + File.separator + "V", script_path  + File.separator + "pi" + File.separator+ "run.sh" );

        this.addProblem("N", feature_config_folder+ File.separator + "pi"  + File.separator + "N"  ,
                srl_temp_folder + File.separator + "pi" + File.separator + "N", script_path + File.separator + "pi" + File.separator+ "run.sh" );
    }



    @Override
    public void train(List<Sentence> data) {

        DataSet ds_V = new DataSet();
        DataSet ds_N = new DataSet();

        data.forEach(s -> s.forEach( w -> {



                    Instance ins = new Instance(w);
                    if (w instanceof Predicate) {
                        if (w.getPos().startsWith("V"))
                            ds_V.add(new Tuple2<>(ins, positive_label));

                        if (w.getPos().startsWith("N"))
                            ds_N.add(new Tuple2<>(ins, positive_label));

                    }
                        else {
                        if (w.getPos().startsWith("V"))
                            ds_V.add(new Tuple2<>(ins, negative_label));

                        if (w.getPos().startsWith("N"))
                            ds_N.add(new Tuple2<>(ins, negative_label));
                    }




        }));

        this.probs.get("V").train(ds_V);

        this.probs.get("N").train(ds_N);


    }

    @Override
    public void predict(List<Sentence> data) {
        DataSet ds_V = new DataSet();
        DataSet ds_N = new DataSet();

        data.forEach(s -> s.forEach(w -> {
            Instance ins = new Instance(w);
            ins.setUsed_for_training(false);

            if (w.getPos().startsWith("V"))
                ds_V.add(new Tuple2<>(ins, "0"));

            if (w.getPos().startsWith("N"))
                ds_N.add(new Tuple2<>(ins, "0"));

        }));
        if (ds_V.size() >0) {
            List<String> rs =      this.probs.get("V").test(ds_V);
            for (int i =0; i <ds_V.size(); i++) {

                String t = rs.get(i);
                if (t.equals(this.positive_label)) {
                    Word w = (Word) ds_V.get(i)._1().getVal();
                    Predicate pred = new Predicate(w);
                    int idx = w.getSentence().indexOf(w);
                    w.getSentence().remove(idx);
                    w.getSentence().add(idx, pred);
                }

            }

        }


        if (ds_N.size() >0) {
            List<String> rs =      this.probs.get("N").test(ds_N);
            for (int i =0; i <ds_N.size(); i++) {

                String t = rs.get(i);
                if (t.equals(this.positive_label)) {
                    Word w = (Word) ds_N.get(i)._1().getVal();
                    Predicate pred = new Predicate(w);
                    int idx = w.getSentence().indexOf(w);
                    w.getSentence().remove(idx);
                    w.getSentence().add(idx, pred);
                }

            }

        }


    }
}
