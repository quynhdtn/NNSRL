package liir.nlp.nnsrl.pipeline;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Word;
import liir.nlp.core.representation.Predicate;
import liir.nlp.nnsrl.evaluation.Evaluator;
import liir.nlp.nnsrl.io.CoNLL2009NoLabelReader;
import liir.nlp.nnsrl.io.CoNLL2009Reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 25/01/16.
 */
public class Pipeline {

    List<Step> steps=new ArrayList<>();

    public Pipeline(String feature_folder, String temp_folder, String script_folder) throws IOException {
        if (!Files.exists(Paths.get(temp_folder)))
        Files.createDirectory(Paths.get(temp_folder));
        PredicateIdentification pi = new PredicateIdentification(feature_folder, temp_folder, script_folder);
        PredicateDisambiguation pd = new PredicateDisambiguation(feature_folder, temp_folder, script_folder);
        ArgumentIdentification ai = new ArgumentIdentification(feature_folder, temp_folder, script_folder);
        ArgumentClassification ac = new ArgumentClassification(feature_folder, temp_folder, script_folder);

        this.steps.add(pi);
        this.steps.add(pd);
        this.steps.add(ai);
        this.steps.add(ac);

    }


    public void train(List<Sentence> data){
        steps.forEach(s -> s.train(data));
    }


    public void predict(List<Sentence>data){
        steps.forEach(s -> s.predict(data));
    }

    public static void main(String[] args) throws IOException {
       Pipeline srl  = new Pipeline("/Users/quynhdo/Documents/WORKING/PhD/workspace/WE/NNSRL/feas_lund","/Users/quynhdo/Documents/WORKING/PhD/workspace/WE/NNSRL/temps","/Users/quynhdo/Documents/WORKING/PhD/workspace/WE/NNSRL/scripts");
     //   Pipeline srl  = new Pipeline("/Users/quynhdo/Documents/WORKING/PhD/workspace/WE/NNSRL/temps","/Users/quynhdo/Documents/WORKING/PhD/workspace/WE/NNSRL/temps","/Users/quynhdo/Documents/WORKING/PhD/workspace/WE/NNSRL/scripts");

        String input = "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-evaluation-ood.txt";
        CoNLL2009Reader cr = new CoNLL2009Reader(new File(input), false);

        List<Sentence> sens= cr.readAll();

        System.out.println(sens.size());
//      srl.predict(sens);



        System.out.println("haha");


       CoNLL2009Reader gcr = new CoNLL2009Reader(new File(input), false);
        Evaluator eval = new Evaluator(sens, gcr.readAll(), "output.txt");


        for (Sentence s: sens)
            for (Predicate p: s.getPredicates())
                if (p.getArgmap().size()>0)
                    System.out.println("ok");
      srl.train(sens);

    }
}

