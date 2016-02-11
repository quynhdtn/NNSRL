package liir.nlp.core.representation;


import liir.nlp.utils.BrownClusterReader;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by quynhdo on 30/09/15.
 */
public class Span {

    Word first_word;
    Word end_word;
    Word head_word;

    Sentence sen;

    public Span(Word first_word) {
        this.first_word = first_word;
    }

    public Word getFirst_word() {
        return first_word;
    }

    public Word getEnd_word() {
        return end_word;
    }

    public Word getHead_word() {
        return head_word;
    }

    public Sentence getSen() {
        return sen;
    }

    public void setFirst_word(Word first_word) {

        this.first_word = first_word;
    }

    public void setEnd_word(Word end_word) {
        this.end_word = end_word;
    }

    public void setHead_word(Word head_word) {
        this.head_word = head_word;
    }

    public void setSen(Sentence sen) {
        this.sen = sen;
    }



    /* Implement feature extraction
     *
     */

    public String getFirstWord(){
        return first_word.getStr().toLowerCase();
    }

    public String getLastWord(){
        return end_word.getStr().toLowerCase();
    }

    public String getHeadWord(){
        if (head_word!=null)
            return head_word.getStr().toLowerCase();
        else
            return  null;
    }

    public Set<String> getBagOfWords(){

        Set<String> words = new TreeSet<String>();
        for (int  i =  sen.indexOf(first_word); i<= sen.indexOf(end_word); i++){
            words.add(sen.get(i).getStr().toLowerCase());
        }

        return  words;


    }


    public String getBrownClusterOfHeadWord(BrownClusterReader br){

        String hw = getHeadWord();
        if (hw != null)
            return String.valueOf(br.getCluster(hw));
        else
            return null;


    }





}
