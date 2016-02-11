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
 * Created by quynhdo on 01/02/16.
 */
public class ArgumentIdentification extends Step{

    String positive_label = "1";
    String negative_label = "0";
    String neural_label="0";
    public  ArgumentIdentification(String feature_config_folder, String srl_temp_folder, String script_path) throws IOException {

        if (!Files.exists(Paths.get(srl_temp_folder + File.separator + "ai")))
            Files.createDirectory(Paths.get(srl_temp_folder + File.separator + "ai"));

        this.addProblem("V", feature_config_folder + File.separator + "ai" + File.separator +"V" ,
                srl_temp_folder + File.separator + "ai" + File.separator + "V", script_path  + File.separator + "ai" + File.separator+ "run.sh" );

        this.addProblem("N", feature_config_folder+ File.separator + "ai"  + File.separator + "N"  ,
                srl_temp_folder + File.separator + "ai" + File.separator + "N", script_path + File.separator + "ai" + File.separator+ "run.sh" );

    }




    @Override
    public void train(List<Sentence> data) {
        DataSet ds_V = new DataSet();
        DataSet ds_N = new DataSet();

        data.forEach(s -> s.getPredicates().forEach( p -> {

            if (p.getPos().startsWith("V")){

                for (Word w : s){
                    Instance ins = new Instance(new Tuple2<Word,Predicate>(w,p));
                    if (p.getArgmap().containsKey(w.getId())){


                        ds_V.add(new Tuple2<>(ins, positive_label));
                    }
                    else{
                        ds_V.add(new Tuple2<>(ins, negative_label));
                    }
                }
            }

            if (p.getPos().startsWith("N")){

                for (Word w : s){
                    Instance ins = new Instance(new Tuple2<Word,Predicate>(w,p));
                    if (p.getArgmap().containsKey(w.getId())){


                        ds_N.add(new Tuple2<>(ins, positive_label));
                    }
                    else{
                        ds_N.add(new Tuple2<>(ins, negative_label));
                    }
                }
            }



        }));

        this.probs.get("V").train(ds_V);

        this.probs.get("N").train(ds_N);
    }

    @Override
    public void predict(List<Sentence> data) {

        DataSet ds_V = new DataSet();
        DataSet ds_N = new DataSet();

        data.forEach(s -> s.getPredicates().forEach( p -> {

            if (p.getPos().startsWith("V")){

                for (Word w : s){
                    Instance ins = new Instance(new Tuple2<Word,Predicate>(w,p));
                    ins.setUsed_for_training(false);

                    ds_V.add(new Tuple2<>(ins, neural_label));


                }
            }

            if (p.getPos().startsWith("N")){

                for (Word w : s){
                    Instance ins = new Instance(new Tuple2<Word,Predicate>(w,p));
                    ins.setUsed_for_training(false);


                    ds_N.add(new Tuple2<>(ins, neural_label));


                }
            }


        }));



        if (ds_V.size() >0) {
            List<String> rs =      this.probs.get("V").test(ds_V);
            for (int i =0; i <ds_V.size(); i++) {

                String t = rs.get(i);
                if (t.equals(this.positive_label)) {
                    Predicate p = (Predicate) ((Tuple2<Word,Predicate>)ds_V.get(i)._1().getVal())._2();
                    Word w = ((Tuple2<Word,Predicate>)ds_V.get(i)._1().getVal())._1();
                    p.addArgument(w.getId(), "ARG");
                }

            }

        }


        if (ds_N.size() >0) {
            List<String> rs =      this.probs.get("N").test(ds_N);
            for (int i =0; i <ds_N.size(); i++) {
                String t = rs.get(i);
                if (t.equals(this.positive_label)) {
                    Predicate p = (Predicate) ((Tuple2<Word,Predicate>)ds_N.get(i)._1().getVal())._2();
                    Word w = ((Tuple2<Word,Predicate>)ds_N.get(i)._1().getVal())._1();
                    p.addArgument(w.getId(), "ARG");
                }

            }

        }

    }
}
