package liir.nlp.we;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quynhdo on 04/11/15.
 * the dictionary of the word embeddings
 */



public class WEDictionary extends HashMap<String,float[]> {
    int dim=0;
    String path = "";

    public WEDictionary(String path){
        System.out.println( "Loading WEDictionary from " + path);
        this.path = path;
        try {
            List<String> lines= Files.readAllLines(Paths.get(path));
            for (String l : lines){
                l=l.trim();
                if (l!="")
                    put(l);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public void put(String key, String val_str){

        String[] tmp = val_str.split("\\s+");
        this.dim = tmp.length;
        float[] vals= new float[dim];
        for (int i =0;i<tmp.length; i++)
            vals[i]= Float.parseFloat(tmp[i]);

        this.put(key, vals);

    }

    public void put (String key_val_str){
        String[] tmp = key_val_str.split("\\s+");

        this.dim = tmp.length-1;
        float[] vals= new float[dim];
        for (int i =1;i<tmp.length; i++)
            vals[i-1]= Float.parseFloat(tmp[i]);

        this.put(tmp[0], vals);
    }


    public int getDim(){
        return this.dim;
    }

}


