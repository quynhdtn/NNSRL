package liir.nlp.nnsrl.io;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Word;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 29/01/16.
 */
public class CoNLL2009NoLabelReader extends CoNLL2009Reader{
    public CoNLL2009NoLabelReader(File file, boolean use_gold) {
        super(file, use_gold);
    }

    public Sentence newSentence(String[] lines, boolean use_gold){
        Sentence ret=new Sentence();
        Word nextWord;
        int ix=1;

        List<String[]> argscache = new ArrayList<>(); // store argument information

        for(String line:lines){
            String[] cols=WHITESPACE_PATTERN.split(line);

                nextWord = new Word();
                nextWord.setId(cols[0]);
                nextWord.setStr(cols[1]);
                if (!use_gold) {
                    nextWord.setLemma(cols[3]);
                    nextWord.setPos(cols[5]);
                    nextWord.setHead(cols[9]);
                    nextWord.setDeprel(cols[11]);
                }
                else{
                    nextWord.setLemma(cols[2]);
                    nextWord.setPos(cols[4]);
                    nextWord.setHead(cols[8]);
                    nextWord.setDeprel(cols[10]);
                }




            ret.add(nextWord);
            nextWord.setSentence(ret);
        }
             buildDependencyTree(ret);
        buildSemanticTree(ret, argscache);

        return ret;
    }

}
