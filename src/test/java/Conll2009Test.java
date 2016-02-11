import liir.nlp.core.representation.Sentence;
import liir.nlp.nnsrl.features.FeatureGenerator;
import liir.nlp.nnsrl.io.CoNLL2009Reader;
import liir.nlp.nnsrl.ml.DataSet;
import liir.nlp.nnsrl.phases.PIDataSetMaker;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by quynhdo on 01/10/15.
 */
public class Conll2009Test {

    public static void extractFeatureTrain(String data, String fea_file, String save_folder) throws Exception {
        CoNLL2009Reader cr = new CoNLL2009Reader(new File(data), false);
        List<Sentence> all = cr.readAll();
        FeatureGenerator fg = new FeatureGenerator(fea_file);  //load the feature config

        System.out.println("We have generated " + String.valueOf(fg.getFeatureList().size()) +  " features.");

        DataSet ds = PIDataSetMaker.forTraining(all, true);
        fg.extractFeature(ds);
        try {
            ds.saveData(fg.getFeatureList(), save_folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void extractFeatureTest(String data, String fea_folder, String output) throws Exception {
        CoNLL2009Reader cr = new CoNLL2009Reader(new File(data), false);
        List<Sentence> all = cr.readAll();
        FeatureGenerator fg = new FeatureGenerator(fea_folder);
        System.out.println("We have loaded " + String.valueOf(fg.getFeatureList().size()) +  " features.");
        DataSet ds = PIDataSetMaker.forTesting(all, true);

        fg.extractFeature(ds);

        ds.setFeature_set(fg.getFeatureList());
        try {
            //    ds.saveData(fg.getFeatureList(), "eval_v");

            ds.getDataAndLabelAsSparse(output);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {


        String input = "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-train.txt";
        String fea_file = "src/main/resources/featureWE.properties";
        String fea_folder = "train_v/fea";
       String output =  "train_v";

   //  String output =  "train.forsvm.v.txt";
    Conll2009Test.extractFeatureTrain(input, fea_file, output);
   //    Conll2009Test.extractFeatureTest(input, fea_folder, output);

      //  String input = "/Users/quynhdo/Downloads/CoNLL2009-ST-English-trial.txt";
   //     CoNLL2009Reader cr = new CoNLL2009Reader(new File(input), false);
     //   List<Sentence> all = cr.readAll();

       // System.out.println(all.size());

   //     all.forEach(s -> System.out.println(s.toXMLString()));

        /*
        FeatureGenerator fg = new FeatureGenerator();

        DataSet ds = PIDataSetMaker.forTraining(all, true);
        fg.extractFeature(ds);
     //   fg.printFeatureValueAs1HotVector(ds);

        fg.getSize();

        try {
            ds.saveData(fg.getFeatureList(), "train_v");
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    /*

        FeatureGenerator fg = new FeatureGenerator("train_v/fea");

        System.out.println(fg.getFeatureList().size());


        DataSet ds = PIDataSetMaker.forTesting(all, true);

        fg.extractFeature(ds);

        ds.setFeature_set(fg.getFeatureList());
  //      fg.printFeatureValueAs1HotVector(ds);

     //   fg.getSize();

        try {
        //    ds.saveData(fg.getFeatureList(), "eval_v");

            ds.getDataAndLabelAsSparse("eval.forsvm.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

*/
    }



}
