import liir.nlp.nnsrl.evaluation.Evaluator;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by quynhdo on 14/12/15.
 */
public class OptimizerTest{



        public static void runOptimizer(String temp_folder) throws Exception {
            File folder = new File(temp_folder);

            File[] listOfFiles = folder.listFiles();

            ArrayList<String> rsfile = new ArrayList<>();

            for (File file : listOfFiles) {
                if (file.isFile()) {

                    if (file.getName().endsWith("newtrain.mdl.dev.txt")) {
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


            String sys2 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/origin_nobrown/dev.txt";

            //    String sys2 = "/Users/quynhdo/Documents/PAPERS/DEC/OriginalFull/out1.lund";

            String gold = "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-development.txt";
            //     String out1 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/extra_300R/extra_allWordFeaReal.rs.txt";

            //  String out1 = ///;
            String out2 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/origin_nobrown/dev_result.txt";

            //     String out3 = "/Users/quynhdo/Documents/WORKING/PhD/NNSRL/Lund/extra_300R/extra_allWordFeaReal.compare.txt";
            //     String out3 = "/Users/quynhdo/Desktop/Model1_rs/1_400newtrain.ood.cpr.txt";

            for (String fp : rsfile){

                String sys1 = fp;
                String out1 = fp + ".rs.txt";
                String out3= fp + ".cpr.txt";

                Evaluator el = new Evaluator(sys1,gold, out1);

                if (best_gain_loc < el.args_f.get("AM-LOC"))
                {
                    best_gain_loc = el.args_f.get("AM-LOC");

                    best_for_loc =  fp;
                }

                if (best_gain_mnr < el.args_f.get("AM-MNR"))
                {
                    best_gain_mnr = el.args_f.get("AM-MNR");

                    best_for_mnr =  fp;
                }

                if (best_gain_tmp < el.args_f.get("AM-TMP"))
                {
                    best_gain_tmp = el.args_f.get("AM-TMP");

                    best_for_tmp =  fp;
                }

                if (best_gain_dir< el.args_f.get("AM-DIR"))
                {
                    best_gain_dir = el.args_f.get("AM-DIR");

                    best_for_dir =  fp;
                }


            }

            System.out.println(best_for_loc + ": " + String.valueOf(best_gain_loc));

            System.out.println(best_for_mnr + ": " + String.valueOf(best_gain_mnr));

            System.out.println(best_for_tmp + ": " + String.valueOf(best_gain_tmp));

            System.out.println(best_for_dir + ": " + String.valueOf(best_gain_dir));




        }



    public static void main(String[] args){
        try {
            OptimizerTest.runOptimizer("/Users/quynhdo/Desktop/Model1_rs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
