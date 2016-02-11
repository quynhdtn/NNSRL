package liir.nlp.nnsrl.pipeline;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Word;
import liir.nlp.core.representation.Predicate;
import liir.nlp.nnsrl.features.FeatureGenerator;
import liir.nlp.nnsrl.ml.DataSet;
import liir.nlp.nnsrl.ml.Instance;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by quynhdo on 29/01/16.
 */
public class PredicateDisambiguation extends Step{


    String feature_config_folder="";
    String srl_temp_folder="";
    String script_path="";
    String reference_file = "";
    String default_sense="01";
    HashMap<String,String> sensDict=new HashMap<>();
    public  PredicateDisambiguation(String feature_config_folder, String srl_temp_folder, String script_path) throws IOException {

        if (!Files.exists(Paths.get(srl_temp_folder + File.separator + "pd")))
            Files.createDirectory(Paths.get(srl_temp_folder + File.separator + "pd"));

        if (!Files.exists(Paths.get(srl_temp_folder + File.separator + "pd" +File.separator + "V")))
            Files.createDirectory(Paths.get(srl_temp_folder + File.separator + "pd"+File.separator + "V"));

        if (!Files.exists(Paths.get(srl_temp_folder + File.separator + "pd" +File.separator + "N")))
            Files.createDirectory(Paths.get(srl_temp_folder + File.separator + "pd"+File.separator + "N"));

        if (Files.isDirectory(Paths.get(feature_config_folder + File.separator + "pd" + File.separator +"V"))){
            Files.walk(Paths.get(feature_config_folder + File.separator + "pd" + File.separator +"V")).forEach(filePath -> {
                if (Files.isDirectory(filePath)) {
                    System.out.println("V" + filePath.getFileName());
                    this.addProblem("V" + filePath.getFileName(), filePath.toString(),
                            srl_temp_folder + File.separator + "pd" + File.separator + "V" + File.separator + filePath.getFileName(),
                            script_path  + File.separator + "pd" + File.separator+ "run.sh" );

                }
         });
        }

        if (Files.isDirectory(Paths.get(feature_config_folder + File.separator + "pd" + File.separator +"N"))){
            Files.walk(Paths.get(feature_config_folder + File.separator + "pd" + File.separator +"N")).forEach(filePath -> {
                if (Files.isDirectory(filePath)) {
                    System.out.println("N" + filePath.getFileName());

                    this.addProblem("N" + filePath.getFileName(), filePath.toString(),
                            srl_temp_folder + File.separator + "pd" + File.separator + "N" + File.separator + filePath.getFileName(),
                            script_path + File.separator + "pd" + File.separator + "run.sh");

                }
            });
        }



        this.feature_config_folder=feature_config_folder;
        this.srl_temp_folder = srl_temp_folder;
        this.script_path = script_path;
        this.reference_file = srl_temp_folder + File.separator + "pd" + File.separator + "ref.txt";
        if (Files.exists(Paths.get(srl_temp_folder + File.separator + "pd" + File.separator + "ref.txt"))){
            List<String> lines= Files.readAllLines(Paths.get(srl_temp_folder + File.separator + "pd" + File.separator + "ref.txt"));
            for (String l :  lines){
                String[] tmps = l.split("\t");
                if (tmps.length>0){
                    this.sensDict.put(tmps[0],tmps[1]);
                }
            }
        }
    }

    @Override
    public void train(List<Sentence> sens)  {

        List<String> pred_references_V =  new ArrayList<>();
        List<String> pred_references_N =  new ArrayList<>();
        sens.forEach(s -> s.getPredicates().forEach(p -> {
            if (p.getPos().startsWith("V"))
            if (!pred_references_V.contains(p.getLemma()))
                pred_references_V.add(p.getLemma());

            if (p.getPos().startsWith("N"))
                if (!pred_references_N.contains(p.getLemma()))
                    pred_references_N.add(p.getLemma());
        }));

        HashMap<String, DataSet> datasets = new HashMap<>();
        HashMap<String, Set<String>> sensDict =  new HashMap<>();
        for (String p : pred_references_V){
            DataSet ds = new DataSet();
            datasets.put("V"+p, ds);
            sensDict.put("V"+p, new TreeSet<String>());
        }

        for (String p : pred_references_N){
            DataSet ds = new DataSet();
            datasets.put("N"+p, ds);
            sensDict.put("N"+p, new TreeSet<String>());

        }


        sens.forEach(s -> s.getPredicates().forEach(p -> {


            if (p.getPos().startsWith("V")){
                Instance ins = new Instance(p);
                datasets.get("V" + p.getLemma()).add(new Tuple2<>(ins, p.getSense()));
                Set<String> ss = sensDict.get("V"+ p.getLemma());
                ss.add(p.getSense());
                sensDict.put("V"+ p.getLemma(), ss);
            }

            if (p.getPos().startsWith("N")){
                Instance ins = new Instance(p);
                datasets.get("N" + p.getLemma()).add(new Tuple2<>(ins, p.getSense()));
                Set<String> ss = sensDict.get("N"+ p.getLemma());
                ss.add(p.getSense());
                sensDict.put("N" + p.getLemma(), ss);
            }


            }

        ));
        FeatureGenerator fgv = null;
        FeatureGenerator fgn = null;
        try {
            fgv = new FeatureGenerator(feature_config_folder + File.separator + "pd" + File.separator +"V");
            fgn = new FeatureGenerator(feature_config_folder + File.separator + "pd" + File.separator +"N");

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> lines= new ArrayList<>();
        for (String p : pred_references_V){
            if (sensDict.get("V"+p).size()>1){
            this.addProblem("V"+p, fgv,
                    srl_temp_folder + File.separator + "pd" + File.separator + "V" + File.separator + p,
                    script_path + File.separator + "pd" + File.separator + "run.sh"
                    );
            this.probs.get("V"+p).train(datasets.get("V"+p));}
            else{
                lines.add("V"+p +"\t" + sensDict.get("V"+p).toArray()[0]);
            }
        }

        for (String p : pred_references_N){
            if (sensDict.get("N"+p).size()>1){
            this.addProblem("N" + p, fgn,
                    srl_temp_folder + File.separator + "pd" + File.separator + "N" + File.separator + p,
                    script_path + File.separator + "pd" + File.separator + "run.sh"
            );
            this.probs.get("N"+p).train(datasets.get("N"+p));}
            else{
                lines.add("N" + p + "\t" + sensDict.get("N" + p).toArray()[0]);
            }
        }


        try {
            Files.write(Paths.get(this.reference_file), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void predict(List<Sentence> sens) {
        List<String> pred_references_V =  new ArrayList<>();
        List<String> pred_references_N =  new ArrayList<>();
        sens.forEach(s -> s.getPredicates().forEach(p -> {
            if (p.getPos().startsWith("V"))
                if (!pred_references_V.contains(p.getLemma()))
                    pred_references_V.add(p.getLemma());

            if (p.getPos().startsWith("N"))
                if (!pred_references_N.contains(p.getLemma()))
                    pred_references_N.add(p.getLemma());
        }));

        HashMap<String, DataSet> datasets = new HashMap<>();
        for (String p : pred_references_V){
            DataSet ds = new DataSet();
            datasets.put("V"+p, ds);
        }

        for (String p : pred_references_N){
            DataSet ds = new DataSet();
            datasets.put("N"+p, ds);

        }

        sens.forEach(s -> s.getPredicates().forEach(p -> {
                    if (p.getPos().startsWith("V")){
                        if (this.sensDict.containsKey("V" + p.getLemma()))
                            p.setSense(this.sensDict.get("V" + p.getLemma()));
                        else {
                            if (!this.probs.containsKey("V" + p.getLemma())){
                                p.setSense(this.default_sense);
                            }
                            else{
                            Instance ins = new Instance(p);
                                ins.setUsed_for_training(false);

                                datasets.get("V" + p.getLemma()).add(new Tuple2<>(ins, "00"));}
                        }

                    }

                    if (p.getPos().startsWith("N")){
                        if (this.sensDict.containsKey("N" + p.getLemma()))
                            p.setSense(this.sensDict.get("N" + p.getLemma()));
                        else {
                            if (!this.probs.containsKey("N" + p.getLemma())){
                                p.setSense(this.default_sense);
                            }
                            else {
                                Instance ins = new Instance(p);
                                ins.setUsed_for_training(false);

                                datasets.get("N" + p.getLemma()).add(new Tuple2<>(ins, "00"));
                            }
                        }
                    }


                }

        ));


        for (String k :  datasets.keySet()){
            DataSet ds = datasets.get(k);

            if (ds.size()>0){
                List<String> rs =     this.probs.get(k).test(ds);
                for (int i =0; i <ds.size(); i++) {

                    String t = rs.get(i);

                    ((Predicate) ds.get(i)._1().getVal()).setSense(t);

                }
            }
        }
    }
}
