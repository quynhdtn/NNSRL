package liir.nlp.nnsrl.ml;

import liir.nlp.core.representation.Sentence;
import liir.nlp.nnsrl.features.FeatureGenerator;
import liir.nlp.nnsrl.io.CoNLL2009Reader;
import liir.nlp.nnsrl.phases.PIDataSetMaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by quynhdo on 07/01/16.
 */
public class Problem {

    FeatureGenerator fg;
    String temp_folder;
    String train_folder_name="/train_folder";
    String train_file = "train.forsvm.txt";
    String model_file = "train.mdl";
    String train_script_path = "";
    String test_file = "test.forsvm.txt";
    String test_output = "test.out.txt";

    public Problem(FeatureGenerator fg, String temp_folder, String train_script_path){
        this.fg =  fg;
        this.temp_folder = temp_folder;
        this.train_script_path =  train_script_path;

    }

    public Problem(String fea_file, String temp_folder,String train_script_path){// feature_file can be either a file or a folder
        try {
            this.fg =  new FeatureGenerator(fea_file);
            this.temp_folder = temp_folder;
            this.train_script_path =  train_script_path;
            System.out.println("We have generated " + String.valueOf(fg.getFeatureList().size()) +  " features.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void train(DataSet ds){

        fg.extractFeature(ds);
        try {

            if (!Files.exists(Paths.get(temp_folder +File.separator + this.train_folder_name)))
                Files.createDirectory(Paths.get(temp_folder +File.separator + this.train_folder_name));

            ds.saveData(fg.getFeatureList(), temp_folder + File.separator + this.train_folder_name);
            ds.setFeature_set(fg.getFeatureList());
            ds.getDataAndLabelAsSparse(temp_folder + File.separator + this.train_file);
            System.out.println("sh " + this.train_script_path + " -train " + " " + temp_folder +File.separator  + this.train_file
                    +" " +temp_folder +File.separator  + this.model_file);
            Process p = Runtime.getRuntime().exec("sh " + this.train_script_path + " -train " + " " + temp_folder +File.separator  + this.train_file
            +" " +temp_folder +File.separator  + this.model_file);  //train the model by using a script file
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                System.out.println("Script output: " + s);
            }
        //    Files.deleteIfExists(Paths.get(temp_folder + File.separator + this.train_file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> test(DataSet ds){
        fg.extractFeature(ds);

        ds.setFeature_set(fg.getFeatureList());
        try {


            ds.getDataAndLabelAsSparse(temp_folder + File.separator + this.test_file);

            Process p = Runtime.getRuntime().exec("sh " + this.train_script_path + " -test "
                    +" " + temp_folder +File.separator  + this.test_file+" " +temp_folder +File.separator  + this.model_file+
                     " " + temp_folder +File.separator  + this.test_output);  //predict by using a script file

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                System.out.println("Script output: " + s);
            }
            List<String> rs = Files.readAllLines(Paths.get(temp_folder +File.separator  + this.test_output));
            Files.deleteIfExists(Paths.get(temp_folder +File.separator  + this.test_output));
            return rs;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

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
}
