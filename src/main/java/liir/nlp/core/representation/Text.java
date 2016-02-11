package liir.nlp.core.representation;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by quynhdo on 28/08/15.
 */
public class Text extends ArrayList<Sentence> {
    String id = "";

    public Text(Collection<? extends Sentence> c) {
        super(c);
    }

    public Text() {
        super();

    }

    public String getId() {
        return id;
    }

    public Sentence getSentence(String sId) {

        for (Sentence s : this)
            if (s.getId().equals(sId))
                return s;
        return null;
    }





    public boolean add(Sentence s) {
        if (s.size()==0)
            return false;
        s.setText(this);
        return super.add(s);
    }

    public List<Predicate> getPredicates() {
        List<Predicate> preds = new ArrayList<>();

        for (Sentence s : this) {
            preds.addAll(s.getPredicates());
        }

        return preds;
    }




}
