import liir.nlp.core.representation.Sentence;
import liir.nlp.nnsrl.evaluation.Evaluator;
import liir.nlp.nnsrl.features.FeatureGenerator;
import liir.nlp.nnsrl.io.CoNLL2009Reader;
import liir.nlp.nnsrl.ml.DataSet;
import liir.nlp.nnsrl.phases.PIDataSetMaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 28/10/15.
 */
public class EvaluationTest {




    public static void runOptimizer(String temp_folder) throws Exception {
        File folder = new File(temp_folder);

        File[] listOfFiles = folder.listFiles();

        ArrayList<String> rsfile = new ArrayList<>();

        for (File file : listOfFiles) {
            if (file.isFile()) {

                if (file.getName().endsWith(".txt")) {
                    rsfile.add(file.getAbsolutePath());

                }


            }

        }


        String best_for_loc="";
        String best_for_mnr="";
        String best_for_dir="";
        String best_for_tmp="";

        float best_gain_loc = Float.MIN_VALUE;
        float best_gain_mnr = Float.MIN_VALUE;

        float best_gain_dir = Float.MIN_VALUE;

        float best_gain_tmp = Float.MIN_VALUE;


        String sys2 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/origin_nobrown/ood.txt";

        //    String sys2 = "/Users/quynhdo/Documents/PAPERS/DEC/OriginalFull/out1.lund";

        String gold = "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-evaluation-ood.txt";
        //     String out1 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/extra_300R/extra_allWordFeaReal.rs.txt";

        //  String out1 = ///;
        String out2 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/origin_nobrown/ood_result.txt";

        //     String out3 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/extra_300R/extra_allWordFeaReal.compare.txt";
        //     String out3 = "/Users/quynhdo/Desktop/Model1_rs/1_400newtrain.ood.cpr.txt";

        for (String fp : rsfile){

            String sys1 = fp;
            String out1 = fp + ".rs.txt";
            String out3= fp + ".cpr.txt";
            Evaluator el = new Evaluator();
            Evaluator.compareResult(sys1, sys2, gold, out1, out2, out3);

            if (best_gain_loc > el.args_f.get("AM-LOC"))
            {
                best_gain_loc = el.args_f.get("AM-LOC");

                best_for_loc =  fp;
            }

            if (best_gain_mnr > el.args_f.get("AM-MNR"))
            {
                best_gain_mnr = el.args_f.get("AM-MNR");

                best_for_mnr =  fp;
            }

            if (best_gain_tmp > el.args_f.get("AM-TMP"))
            {
                best_gain_tmp = el.args_f.get("AM-TMP");

                best_for_tmp =  fp;
            }

            if (best_gain_mnr > el.args_f.get("AM-DIR"))
            {
                best_gain_mnr = el.args_f.get("AM-DIR");

                best_for_mnr =  fp;
            }


        }

        System.out.println(best_for_loc + ": " + String.valueOf(best_gain_loc));

        System.out.println(best_for_mnr + ": " + String.valueOf(best_gain_mnr));

        System.out.println(best_for_tmp + ": " + String.valueOf(best_gain_tmp));

        System.out.println(best_for_dir + ": " + String.valueOf(best_gain_dir));



    }



    public static void main(String[] args){

        /*
        String sys = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/originalresults/ood.txt";
        String gold = "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-evaluation-ood.txt";
        Evaluator el = new Evaluator(sys, gold);

        */

 //    String sys1 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/ood.txt";
       String sys1="/Users/quynhdo/Desktop/10pc_ood.txt";
    //    String sys1 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/produce/ood.new.txt";
      //  String sys1 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/origin_nobrown/eval_pi.txt";
        String sys2 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/origin_nobrown/ood.txt";
   //     String sys2 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/extra_300R/oldnew.model1.txt";

   //     String sys2 = "/Users/quynhdo/Documents/PAPERS/DEC/OriginalFull/out1.lund";

        String gold = "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-evaluation-ood.txt";
  // String out1 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/produce/ood.model1AllPP.rs.txt";

        String out1 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/ood_loc.rs.txt";
        String out2 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/origin_nobrown/eval_result.txt";

 //     String out3 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/produce/ood.model1AllPP.cpr.txt";
        String out3 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/ood_loc.cpr.txt";
        // Evaluator el = new Evaluator(sys, gold,out );
        Evaluator.compareResult(sys1,sys2,gold,out1,out2,out3);

    }



}
