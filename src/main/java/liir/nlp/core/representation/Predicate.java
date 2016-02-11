package liir.nlp.core.representation;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quynhdo on 01/09/15.
 */
public class Predicate extends Word {

    String sense;
    HashMap<String,String> argmap; //<Argument ID (Mention or Word), Label>

    public Predicate(Word w) {

        super(w);
        argmap = new HashMap<>();


    }

    public void addArgument(String argId, String label){
        argmap.put(argId,label);
    }

    public String getLabel (String argId){
        return argmap.get(argId);
    }

    public void setSense(String sense) {
        this.sense = sense;
    }

    public String getSense() {

        return sense;
    }

    public List<String> getArgument(String label){
        List<String> arr = new ArrayList<>();
        for (String k : argmap.keySet()){
            if (argmap.get(k).toUpperCase().equals(label)){
                arr.add(k);

            }
        }

        return arr;
    }

    public HashMap<String, String> getArgmap() {
        return argmap;
    }






}
