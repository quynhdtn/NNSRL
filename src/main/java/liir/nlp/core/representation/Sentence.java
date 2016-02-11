package liir.nlp.core.representation;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by quynhdo on 28/08/15.
 */
public class Sentence extends ArrayList<Word> {

    String id = "";
    String str="";
    Text text;

    public Sentence(){
        super();
    }
    public void setText(Text text) {
        this.text = text;
    }

    public Text getText() {

        return text;
    }

    public Sentence(Collection<? extends Word> c) {
        super(c);

    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getStr() {

        return str;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id = id;
    }



    public Word getWord(String wId){

        for (Word w : this)
            if (w.getId().equals(wId))
                return w;
        return null;
    }
    public String toXMLString(){
        StringBuilder sb = new StringBuilder();
        sb.append("<s id=\"" + id + "\"" + ">\n");
        if (!str.equals("")) {
            sb.append("\t<str>");
            sb.append(str);
            sb.append("\t</str>\n");
        }
        this.forEach(w -> {
            sb.append("\t\t" + w.toXMLString());
            sb.append("\n");
        });
        sb.append("</s>\n");
        return sb.toString();
    }

    public List<Predicate> getPredicates(){
        List<Predicate> preds= new ArrayList<>();

        for (Word w : this){
            if (w instanceof Predicate)
                preds.add((Predicate)w);
        }

        return preds;
    }

    public boolean add(Word w) {

        w.setSentence(this);
        return super.add(w);
    }

    public String toString(){

        StringBuilder sb=new StringBuilder();
        for (Word w : this){
            sb.append(w.getStr() + " ");
        }
        return sb.toString().trim();
    }
}
