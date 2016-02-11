package liir.nlp.utils;

import liir.nlp.core.representation.Word;

/**
 * Created by quynhdo on 02/10/15.
 *
 */
public class WordExtractor {


    public enum TargetWord{
        Word,
        Parent,
        LeftDep,		//This is the leftmost dependent
        RightDep,		//This is the rightmost dependent
        LeftSibling,	//This is the left sibling
        RightSibling, 	//This is the right sibling
        LeftNeighbour,   //This is the word right before
        RightNeighbour  //This is the word right after
    }


    public static Word getWord(Word w, TargetWord tw){
        switch (tw){
            case Word: return w;
            case Parent: return DependencyUtil.getHead(w);
            case LeftDep: return DependencyUtil.getLeftMostDep(w);
            case RightDep: return DependencyUtil.getRightMostDep(w);
            case LeftSibling: return DependencyUtil.getLeftSibling(w);
            case RightSibling: return DependencyUtil.getRightSibling(w);
            case LeftNeighbour: return getLeftNeighbour(w);
            case RightNeighbour:  return getRightNeighbour(w);
        }
        return null;
    }


    public static Word getLeftNeighbour(Word w){

        int i = w.getIndex()-1;
        if (i>=0) return w.getSentence().get(i);
        return null;


    }

    public static Word getRightNeighbour(Word w){

        int i = w.getIndex()+1;
        if (i < w.getSentence().size()) return w.getSentence().get(i);
        return null;


    }


}
