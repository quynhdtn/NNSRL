package liir.nlp.nnsrl.features;

import liir.nlp.core.representation.Word;
import liir.nlp.utils.*;
import scala.Tuple2;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by quynhdo on 02/10/15.
 */
public class WordFeature  extends Feature{


    WordData wd;
    WordExtractor.TargetWord wt;
    BrownClusterReader br;
    int DataElementPosition;

    public WordFeature(FeatureName name, WordData wd, WordExtractor.TargetWord wt, int DataElementPosition) {
        super(name);
        this.wd = wd;
        this.wt = wt;
        this.DataElementPosition = DataElementPosition;
    }

    public void setBr(BrownClusterReader br) {
        this.br = br;
    }

    public Object getFeatureValue(Object data){

        Word w=null;
        if (data instanceof Word) {
            w = WordExtractor.getWord((Word) data, wt);

        }

        else if ( data instanceof Tuple2){
            if (DataElementPosition == 0)
                w = WordExtractor.getWord( (Word)  ((Tuple2) data)._1(), wt);
            else
                w = WordExtractor.getWord( (Word)  ((Tuple2) data)._2(), wt);
        }

        if (w == null)
                return null;

        switch (wd) {
                case word:
                    return w.getStr().toLowerCase();
                case pos:
                    return w.getPos();
                case lemma:
                    return w.getLemma();
                case deprel:
                    return w.getDeprel();
                case cluster:
                    return br.getCluster(w.getStr().toLowerCase());

                case child_word_set:
                    return getChildrenFeature(w, WordData.word);

                case child_pos_set:
                    return getChildrenFeature(w, WordData.pos);

                case child_lemma_set:
                    return getChildrenFeature(w, WordData.lemma);

                case child_dep_set:
                    return getChildrenFeature(w, WordData.deprel);

                case child_word_dep_set:
                    return getChildrenFeature(w, WordData.word, WordData.deprel);

                case child_pos_dep_set:
                return getChildrenFeature(w, WordData.pos, WordData.deprel);


                case dep_sub_cat:
                    return getDepSubCat(w);
            }



       return null;
    }


    private Set<String> getChildrenFeature(Word w, WordData wd){

        /*
        if (w.getFeatureAsObject("children") == null){
            List<Word> children =  DependencyUtil.getChildren(w);
            w.addObjectFeature("children", children);
        }
        Set<String> rs=new TreeSet<>();
        List<Word> childrens = (List<Word>) w.getFeatureAsObject("children");*/

        List<Word> childrens = w.getChildren();
        Set<String> rs=new TreeSet<>();

        childrens.forEach(c -> rs.add(c.getData(wd)));
        return rs;


    }

    private Set<String> getChildrenFeature(Word w, WordData wd1, WordData wd2){

        /*
        if (w.getFeatureAsObject("children") == null){
            List<Word> children =  DependencyUtil.getChildren(w);
            w.addObjectFeature("children", children);
        }
        Set<String> rs=new TreeSet<>();
        List<Word> childrens = (List<Word>) w.getFeatureAsObject("children");*/
        List<Word> childrens = w.getChildren();
        Set<String> rs=new TreeSet<>();

        childrens.forEach(c -> {
            StringBuilder sb=new StringBuilder();
            String v1=c.getData(wd1);
            String v2=c.getData(wd2);
            sb.append(v1);
            sb.append("_");
            sb.append(v2);
            rs.add(sb.toString());
        });
        return rs;


    }

     // get the subcategorization-frame feature =  sequence of dependency label of w’s children in the dependency tree.
    public String getDepSubCat(Word w){

        /*
        if (w.getFeatureAsObject("children") == null){
            List<Word> children =  DependencyUtil.getChildren(w);
            w.addObjectFeature("children", children);
        }
        List<Word> childrens = (List<Word>) w.getFeatureAsObject("children");*/

        List<Word> childrens = w.getChildren();
        StringBuilder sb = new StringBuilder();
        childrens.forEach(c -> {
            sb.append(c.getDeprel());
            sb.append(" ");
        });

        String rs = sb.toString().trim();
        if (rs.equals(""))
            return "_NO_CHILD_";
        return rs;

    }

}
