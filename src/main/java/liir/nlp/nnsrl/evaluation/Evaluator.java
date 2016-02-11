package liir.nlp.nnsrl.evaluation;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Predicate;
import liir.nlp.nnsrl.io.CoNLL2009Reader;
import se.lth.cs.srl.corpus.Word;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by quynhdo on 28/10/15.
 */
public class Evaluator {

    float precision;
    float recall;
    float f1;
    float pi_p;
    float pi_r;
    float pi_f;
    float pd_p;
    float pd_r;
    float pd_f;

    float ai_p;
    float ai_r;
    float ai_f;
    float ac_p;
    float ac_r;
    float ac_f;

  public  HashMap<String,Float> args_p=new HashMap<>();
   public HashMap<String,Float> args_r=new HashMap<>();
 public   HashMap<String,Float> args_f=new HashMap<>();

    HashMap<String, Integer> label_nums = new HashMap<>();
    int[][] confusion_matrix= null;
    ArrayList<String> labels_for_matrix =  new ArrayList<>();
    public Evaluator(){

    }

    public Evaluator(String systemFilePath, String goldFilePath, String output)
    {
        CoNLL2009Reader scr = new CoNLL2009Reader(new File(systemFilePath), false);
         List<Sentence> sall = scr.readAll();

         CoNLL2009Reader gcr = new CoNLL2009Reader(new File(goldFilePath), false);
        List<Sentence> gall = gcr.readAll();
        runEvaluator(sall, gall, output);

    }
    public Evaluator(List<Sentence> sall, List<Sentence> gall, String output)
    {
        runEvaluator(sall, gall, output);
    }

    public void runEvaluator(List<Sentence> sall, List<Sentence> gall, String output)
    {
      //  CoNLL2009Reader scr = new CoNLL2009Reader(new File(systemFilePath), false);
       // List<Sentence> sall = scr.readAll();

       // CoNLL2009Reader gcr = new CoNLL2009Reader(new File(goldFilePath), false);
       // List<Sentence> gall = gcr.readAll();

        DatasetInformation sys = new DatasetInformation(sall);

        DatasetInformation gold = new DatasetInformation(gall);
        this.label_nums=gold.label_nums;
        CorrectnessInformation correct = new CorrectnessInformation(sall, gall, true);
        this.confusion_matrix = correct.confusion_matrix;
        this.labels_for_matrix  = correct.lbls;
        List<String> lines=new ArrayList<>();

        // predicate identification

        System.out.println("Predicate identification results: ");
        lines.add("Predicate identification results: \n");

        System.out.format("Number of predicate: %d\n", gold.predCount);
        lines.add("Number of predicate: "+  String.valueOf(gold.predCount) + "\n");
        float p= ((float) correct.predCount / sys.predCount * 100);
        float r = ((float) correct.predCount / gold.predCount * 100);

        pi_p=p;
        pi_r=r;
        pi_f=(2 * p * r / (p + r));

        System.out.format("Precision: %10.2f",p);
        System.out.format("\t Recall: %10.2f",r );
        System.out.format("\t F1: %10.2f", pi_f);
        lines.add("Precision: " + String.valueOf(p) + "\tRecall: " + String.valueOf(r) + "\tF1: " + String.valueOf(2 * p * r / (p + r)));


        System.out.println("\n\nSense disambiguation results: ");
        lines.add("\n\nSense disambiguation results: ");

        System.out.format("Number of predicate: %d\n", gold.predCount);
        lines.add("Number of predicate: " + String.valueOf(gold.predCount) + "\n");
        p= ((float) correct.senseCount / sys.predCount * 100);
        r = ((float) correct.senseCount / gold.predCount * 100);
        pd_p=p;
        pd_r=r;
        pd_f=(2 * p * r / (p + r));

        System.out.format("Precision: %10.2f",p);
        System.out.format("\t Recall: %10.2f",r );
        System.out.format("\t F1: %10.2f", (2 * p * r / (p + r)));
        lines.add("Precision: " + String.valueOf(p) + "\tRecall: " + String.valueOf(r) + "\tF1: " + String.valueOf(pd_f));


        System.out.println("\n\nArgument identification results: ");
        lines.add("\n\nArgument identification results: ");

        System.out.format("Number of argument ", gold.argCount);
        lines.add("Number of argument %d\n" + String.valueOf(gold.argCount));
        p= ((float) correct.argCount / sys.argCount * 100);
        r = ((float) correct.argCount / gold.argCount * 100);

        ai_p=p;
        ai_r=r;
        ai_f=(2 * p * r / (p + r));

        System.out.format("Precision: %10.2f",p);
        System.out.format("\t Recall: %10.2f",r );
        System.out.format("\t F1: %10.2f", (2 * p * r / (p + r)));

        lines.add("Precision: " + String.valueOf(p) + "\tRecall: " + String.valueOf(r) + "\tF1: " + String.valueOf(2 * p * r / (p + r)));


        System.out.println("\n\nArgument classification results: ");
        lines.add("\n\nArgument classification results: ");
        System.out.format("Number of argument %d\n", gold.argCount);
        lines.add("Number of argument " + String.valueOf(gold.argCount));
        p= ((float) correct.labelCount / sys.argCount * 100);
        r = ((float) correct.labelCount / gold.argCount * 100);


        ac_p=p;
        ac_r=r;
        ac_f=(2 * p * r / (p + r));
        System.out.format("Precision: %10.2f",p);
        System.out.format("\t Recall: %10.2f",r );
        System.out.format("\t F1: %10.2f", (2 * p * r / (p + r)));
        lines.add("Precision: " + String.valueOf(p) + "\tRecall: " + String.valueOf(r) + "\tF1: " + String.valueOf(2 * p * r / (p + r)));


        System.out.println("\n\nFinal results: ");
        lines.add("\n" +
                "\n" +
                "Final results: ");

        p= ((float) (correct.labelCount  + correct.senseCount ) / (sys.argCount  + sys.predCount) * 100);
        r = ((float) (correct.labelCount  + correct.senseCount ) / (gold.argCount  + gold.predCount) * 100);

        this.precision=p;
        this.recall=r;
        this.f1=2 * p * r / (p + r);
        System.out.format("Precision: %10.2f",p);
        System.out.format("\t Recall: %10.2f",r );
        System.out.format("\t F1: %10.2f", (2 * p * r / (p + r)));
        lines.add("Precision: " + String.valueOf(p) + "\tRecall: " + String.valueOf(r) + "\tF1: " + String.valueOf(2 * p * r / (p + r)));


        System.out.println("\n\nDetail information per role: ");
        lines.add("\n\nDetail information per role: ");

        SortedSet<String> keys = new TreeSet<String>(gold.label_nums.keySet());
        for (String kr : keys){

            System.out.println("\n"+ kr);
            lines.add("\n"+ kr +"\t");

            try {

                p = ((float) (correct.label_nums.get(kr)) / (sys.label_nums.get(kr)) * 100);
                r = ((float) (correct.label_nums.get(kr)) / (gold.label_nums.get(kr)) * 100);

                System.out.format("Precision: %10.2f", p);
                System.out.format("\t Recall: %10.2f", r);
                System.out.format("\t F1: %10.2f", (2 * p * r / (p + r)));
                lines.add("Precision: " + String.valueOf(p) + "\tRecall: " + String.valueOf(r) + "\tF1: " + String.valueOf(2 * p * r / (p + r)));
                this.args_p.put(kr, p);
                this.args_r.put(kr,r);
                this.args_f.put(kr,2 * p * r / (p + r));

            }
            catch (Exception e){
                System.out.format("Precision: Nan");
                System.out.format("Precision: Nan");
                System.out.format("\t F1: Precision: Nan");
                lines.add("Precision: " + "Nan" + "\tRecall: " + "Nan" + "\tF1: " + "Nan");
                this.args_p.put(kr, -1f);
                this.args_r.put(kr,-1f);
                this.args_f.put(kr, -1f);

            }

        }

        System.out.print( "\n\t");
        for (String l : this.labels_for_matrix){
            System.out.print(l +  "\t");

        }

        System.out.print( "\n");
        for (int  i =0;i<this.labels_for_matrix.size(); i++)
        {
            System.out.print(this.labels_for_matrix.get(i)+ "\t");
            for ( int j = 0; j <this.labels_for_matrix.size();j++)
                System.out.print(this.confusion_matrix[i][j]+ "\t");
            System.out.print( "\n");


        }



        try {
            Files.write(Paths.get(output), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public class CorrectnessInformation{
        int predCount = 0;  //correct predicate coutn
        int senseCount = 0; //correct sense count
        int argCount =0; //correct argument identification
        int labelCount = 0; //correct argument classification
        HashMap<String, Integer> label_nums = new HashMap<>();  // label - numcorrect
        int[][] confusion_matrix =null;

        ArrayList<String> lbls =  new ArrayList<>();
        private ArrayList<String> getLabels(List<Sentence> sys, List<Sentence> gold){

            ArrayList<String> lbls =  new ArrayList<>();
            for (Sentence s :sys){
                for (Predicate p :  s.getPredicates()){
                    for (String arg : p.getArgmap().keySet()){
                        if (!lbls.contains(p.getLabel(arg)))
                            lbls.add(p.getLabel(arg));

                    }

                }
            }

            for (Sentence s :gold){
                for (Predicate p :  s.getPredicates()){
                    for (String arg : p.getArgmap().keySet()){
                        if (!lbls.contains(p.getLabel(arg)))
                            lbls.add(p.getLabel(arg));

                    }

                }
            }
            return  lbls;
        }

        public CorrectnessInformation(List<Sentence> sys, List<Sentence> gold, boolean extractConfusion){


            ArrayList<String> lbls=new ArrayList<>();
               if (extractConfusion) {
                   lbls = getLabels(sys, gold);
               }
            Collections.sort(lbls);

            lbls.add("No-Arg");

            this.lbls = lbls;

            int[][] mtx = new int[lbls.size()][lbls.size()];

            for (int i=0;i<sys.size();i++){
                Sentence s = sys.get(i);
                Sentence g = gold.get(i);

                final ArrayList<String> finalLbls1 = lbls;
                s.getPredicates().forEach(p->{
            //                  if (p.getPos().startsWith("V")) {
                    if (g.get(s.indexOf(p)) instanceof Predicate) {
                        {
                            predCount++;
                            Predicate gp = (Predicate) g.get(s.indexOf(p));
                            if (p.getSense().equals(gp.getSense()))
                                senseCount++;

                            HashMap<String, String> args = p.getArgmap();
                            HashMap<String, String> gargs = gp.getArgmap();
                            final ArrayList<String> finalLbls = finalLbls1;
                            args.keySet().forEach(arg -> {
                                int pidx = finalLbls.indexOf(args.get(arg));
                                int gidx = finalLbls.indexOf("No-Arg");
                                if (gargs.containsKey(arg)) {
                                    argCount++;
                                    gidx = finalLbls.indexOf(gargs.get(arg));

                                    if (args.get(arg).equals(gargs.get(arg))) {
                                        {
                                            labelCount++;
                                            String lbl = args.get(arg);
                                            if (label_nums.containsKey(lbl)) {
                                                label_nums.put(lbl, label_nums.get(lbl) + 1);
                                            } else
                                                label_nums.put(lbl, 1);
                                        }
                                    }
                                }

                                mtx[gidx][pidx]+=1;
                            });


                            gargs.keySet().forEach(garg -> {
                                int gidx = finalLbls.indexOf(gargs.get(garg));
                                int pidx = finalLbls.indexOf("No-Arg");
                                if (!args.containsKey(garg)) {
                                    mtx[gidx][pidx]+=1;
                                }


                            });

                        }


                    }

       //                       }

                });
            }
            this.confusion_matrix=mtx;

        }

        public CorrectnessInformation(List<Sentence> sys, List<Sentence> gold){


            for (int i=0;i<sys.size();i++){
                Sentence s = sys.get(i);
                Sentence g = gold.get(i);

                s.getPredicates().forEach(p->{

            //       if (p.getPos().startsWith("V")) {
                        if (g.get(s.indexOf(p)) instanceof Predicate) {
                            {
                                predCount++;
                                Predicate gp = (Predicate) g.get(s.indexOf(p));
                                if (p.getSense().equals(gp.getSense()))
                                    senseCount++;

                                HashMap<String, String> args = p.getArgmap();
                                HashMap<String, String> gargs = gp.getArgmap();
                                args.keySet().forEach(arg -> {
                                    if (gargs.containsKey(arg)) {
                                        argCount++;

                                        if (args.get(arg).equals(gargs.get(arg))) {
                                            {
                                                labelCount++;
                                                String lbl = args.get(arg);
                                                if (label_nums.containsKey(lbl)) {
                                                    label_nums.put(lbl, label_nums.get(lbl) + 1);
                                                } else
                                                    label_nums.put(lbl, 1);
                                            }
                                        }
                                    }
                                });

                           }


            //          }

                  }

                });
            }


        }



    }

    public class DatasetInformation{
        int predCount=0; // number of predicates
        int argCount= 0; // number of arguments
        HashMap<String,Integer> pred_nums = new HashMap<>(); // predicate - count
        HashMap<String, Integer> label_nums = new HashMap<>(); //label-count

        public DatasetInformation(List<Sentence> sens){
            sens.forEach(s -> {
                s.getPredicates().forEach(p -> {
             //      if (p.getPos().startsWith("V")) {
                        predCount++;
                        if (pred_nums.containsKey(p.getLemma()))
                            pred_nums.put(p.getLemma(), pred_nums.get(p.getLemma()) + 1);
                        else
                            pred_nums.put(p.getLemma(), 1);


                        p.getArgmap().values().forEach(lbl -> {
                            argCount++;
                            if (label_nums.containsKey(lbl))
                                label_nums.put(lbl, label_nums.get(lbl) + 1);
                            else
                                label_nums.put(lbl, 1);
                        }
                        );

         //          }
                });
            } );
        }


        public int getPredCount() {
            return predCount;
        }

        public int getArgCount(){
            return argCount;
        }
    }

    public static void compareResult(String systemFilePath1, String systemFilePath2, String goldFilePath, String output1, String output2, String compareoutput){
        Evaluator el1= new Evaluator(systemFilePath1, goldFilePath, output1);
        Evaluator el2= new Evaluator(systemFilePath2, goldFilePath, output1);

        List<String> lines=new ArrayList<>();
        lines.add("PI: " + String.valueOf(el1.pi_p - el2.pi_p) + "\t" +String.valueOf(el1.pi_r - el2.pi_r) + "\t" +String.valueOf(el1.pi_f - el2.pi_f) );
        lines.add("PD: " + String.valueOf(el1.pd_p - el2.pd_p) + "\t" +String.valueOf(el1.pd_r - el2.pd_r) + "\t" +String.valueOf(el1.pd_f - el2.pd_f) );
        lines.add("AI: " + String.valueOf(el1.ai_p - el2.ai_p) + "\t" +String.valueOf(el1.ai_r - el2.ai_r) + "\t" +String.valueOf(el1.ai_f - el2.ai_f) );
        lines.add("AC: " + String.valueOf(el1.ac_p - el2.ac_p) + "\t" +String.valueOf(el1.ac_r - el2.ac_r) + "\t" +String.valueOf(el1.ac_f - el2.ac_f) );
        SortedSet<String> keys = new TreeSet<String>(el1.args_f.keySet());
        for (String k : keys){
            if (el1.args_p.get(k)  == -1f) {
                lines.add(k + "\t" + "Nan");
                lines.add(k + "\t" + "Nan");
                lines.add(k + "\t" + "Nan");
            }
            else{
                lines.add(k + "\t" + String.valueOf(el1.args_p.get(k) - el2.args_p.get(k))+ "\t" + String.valueOf(el1.args_r.get(k) - el2.args_r.get(k))
                        + "\t" + String.valueOf(el1.args_f.get(k) - el2.args_f.get(k)) +"\t/" + String.valueOf(el1.label_nums.get(k)));
              //  lines.add(k + "\t" + String.valueOf(el1.args_r.get(k) - el2.args_r.get(k)));
               // lines.add(k + "\t" + String.valueOf(el1.args_f.get(k) - el2.args_f.get(k)));
            }


        }

        try {
            Files.write(Paths.get(compareoutput), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
