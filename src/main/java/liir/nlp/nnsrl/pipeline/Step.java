package liir.nlp.nnsrl.pipeline;

import liir.nlp.core.representation.Sentence;
import liir.nlp.nnsrl.features.FeatureGenerator;
import liir.nlp.nnsrl.ml.Problem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quynhdo on 20/01/16.
 * a SRL step
 */
public abstract class Step {
    

    HashMap<String,Problem> probs =  new HashMap<>();


    public void addProblem (String key, String fea_file, String temp_folder, String script_path){
        try {
            if (! Files.exists(Paths.get(temp_folder)))
                Files.createDirectory(Paths.get(temp_folder));
            this.probs.put(key, new Problem(fea_file, temp_folder, script_path));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void addProblem (String key, FeatureGenerator fg, String temp_folder, String script_path){
        try {
            if (! Files.exists(Paths.get(temp_folder)))
                Files.createDirectory(Paths.get(temp_folder));
            this.probs.put(key, new Problem(fg, temp_folder, script_path));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public abstract void train(List<Sentence> sens);
    public abstract void predict (List<Sentence> sens);
}
