package liir.nlp.core.representation;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import liir.nlp.utils.WordData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quynhdo on 28/08/15.
 */
public class Word {
    int id=0;
    String str="";
    String lemma="";
    String pos="";
    String head="";
    String deprel="";

    public void setWord_form(String word_form) {
        this.word_form = word_form;
    }

    Sentence sentence;
    int index;

    Word headWord=null;
    List<Word> children = new ArrayList<>();

    String word_form="";

    public String getWord_form() {
        return word_form;
    }

    HashMap<String,Object> otherFeatures;

    List<Word> path_to_root=null;

    public void setPath_to_root(List<Word> path_to_root) {
        this.path_to_root = path_to_root;
    }

    public List<Word> getPath_to_root() {

        return path_to_root;
    }

    public Word(){}
    public Word(int id, String str, String lemma, String pos, String head, String deprel, Sentence sentence) {
        if (id!=-1)
            this.id = id;
        if (str!=null)
            this.str = str;
        if (lemma!=null)
            this.lemma = lemma;
        if (pos!=null)
            this.pos = pos;
        if (head!=null)
            this.head = head;
        if (deprel !=null)
            this.deprel = deprel;
        if (sentence !=null)
        {
            this.sentence = sentence;
            this.index = this.sentence.indexOf(this);
        }
    }

    public Word(Word w){
        this.id = w.getId();
        this.str = w.getStr();
        this.lemma = w.getLemma();
        this.pos = w.getPos();
        this.head = w.getHead();
        this.deprel = w.getDeprel();
        this.sentence = w.getSentence();
        this.otherFeatures = w.otherFeatures;
        this.index = w.index;
        this.goldLemma= w.getGoldLemma();
        this.goldDeprel= w.getGoldDeprel();
        this.goldHead= w.getGoldHead();
        this.goldPos= w.getGoldPos();
    }

    public int getPositionInSentence(){
        return sentence.indexOf(this) + 1;
    }
    public String toXMLString(){

        String s=  "<w id=\"" + id + "\" str=\"" + str  + "\" lemma=\"" + lemma  + "\" pos=\"" + pos  + "\" head=\"" + head +  "\" deprel=\"" + deprel +    "\"";

        if (otherFeatures!=null){
            for (String k : otherFeatures.keySet()){
                s = s + " " + k + "=\"" + otherFeatures.get(k) + "\"";
            }
        }


        s = s + "/>" ;
        return s;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setDeprel(String deprel) {
        this.deprel = deprel;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence; this.index = this.sentence.indexOf(this);
    }

    public int getId() {
        return id;
    }

    public String getStr() {
        return str;
    }

    public String getLemma() {
        return lemma;
    }

    public String getPos() {
        return pos;
    }

    public String getHead() {
        return head;
    }

    public String getDeprel() {
        return deprel;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void addFeature (String name, String value){
        if (otherFeatures == null)
            otherFeatures = new HashMap<>();
        otherFeatures.put(name, value);
    }

    public String getFeature (String name){

        if (otherFeatures == null)
            return  "";
        if (otherFeatures.containsKey(name))


        return otherFeatures.get(name).toString();
        else
            return "";
    }

    public void addObjectFeature (String name, Object value){
        if (otherFeatures == null)
            otherFeatures = new HashMap<>();
        otherFeatures.put(name, value);
    }

    public Object getFeatureAsObject (String name){

        if (otherFeatures == null)
            return  null;
        if (otherFeatures.containsKey(name))


            return otherFeatures.get(name);
        else
            return null;
    }

    public int getIndex() {
        return index;
    }


    public String getData(WordData d){
        switch (d){
            case word: return word_form;
            case pos: return pos;
            case lemma: return lemma;
            case deprel: return deprel;
        }

        return null;
    }



    String goldLemma;
    String goldPos;
    String goldHead;
    String goldDeprel;


    public void setGoldLemma(String goldLemma) {
        this.goldLemma = goldLemma;
    }

    public void setGoldPos(String goldPos) {
        this.goldPos = goldPos;
    }

    public void setGoldHead(String goldHead) {
        this.goldHead = goldHead;
    }

    public void setGoldDeprel(String goldDeprel) {
        this.goldDeprel = goldDeprel;
    }

    public String getGoldLemma() {

        return goldLemma;
    }

    public String getGoldPos() {
        return goldPos;
    }

    public String getGoldHead() {
        return goldHead;
    }

    public String getGoldDeprel() {
        return goldDeprel;
    }

    public void setHeadWord(Word headWord) {
        this.headWord = headWord;
    }

    public Word getHeadWord() {

        return headWord;
    }

    public void setChildren(List<Word> children) {
        this.children = children;
    }

    public List<Word> getChildren() {

        return children;
    }

    public void addChild(Word w){
        children.add(w);
    }



    public List<Word> getPathToRoot(){
        List<Word> path;
        if (this.head.equals("0"))
        {
            path=new ArrayList<>();
            path.add(this);
            return path;
        }
        path = this.headWord.getPathToRoot();
        path.add(this);
        return path;
    }


    public String getPath(Word w, WordData d){
        List<Word> scr_to_root = getPathToRoot();
        List<Word> trg_to_root = w.getPathToRoot();


        int commonIndex=0;

        StringBuilder sb=new StringBuilder();
        int s1 = scr_to_root.size();
        int s2 = trg_to_root.size();
        int min=(s1<s2?s1:s2);


        for (int i=0;i<min;++i){
            if(scr_to_root.get(i)==trg_to_root.get(i))
            {
                commonIndex = i;
                break;
            }

        }


        for (int i=s1-1;i>=commonIndex+1; i--)
        {

            sb.append(scr_to_root.get(i).getData(d));
            sb.append("!up!");

        }

        sb.append(scr_to_root.get(commonIndex).getData(d));
        sb.append("!down!");

        for (int i=commonIndex+1;i<s2; i++)
        {

            sb.append("!down!");
            sb.append(trg_to_root.get(i).getData(d));

        }


        return sb.toString();

    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        liir.nlp.core.representation.Word w = new liir.nlp.core.representation.Word();
        Method m= liir.nlp.core.representation.Word.class.getMethod("show", null);
        int i = Integer.parseInt(m.invoke(w).toString());
        System.out.println(i);

    }

}
