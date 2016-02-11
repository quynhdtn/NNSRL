package liir.nlp.utils;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 11/02/16.
 */
public class DependencyUtil2 {

    public static Word getHead(Word w){
        return w.getHeadWord();
    }


    public static List<Word> getChildren(Word w){
        return w.getChildren();
    }




    public static Word getLeftSibling(Word w){
        Word h = w.getHeadWord();
        if (h==null) return null;

        int idx = h.getChildren().indexOf(w);

        if (idx >=1)
            return h.getChildren().get(idx-1);


        return null;
    }

    public static Word getRightSibling(Word w){
        Word h = w.getHeadWord();
        if (h==null) return null;
        List<Word> children = h.getChildren();

        int idx = children.indexOf(w);

        if (idx < children.size()-1)
            return h.getChildren().get(idx+1);


        return null;
    }

    public static Word getLeftMostDep(Word w){
        for (int i = w.getSentence().size()-1; i>=0; i-- ) {

            Word ww = w.getSentence().get(i);
            if (Integer.parseInt(ww.getHead()) == w.getIndex()+1)
                return ww;

        }
        return null;

    }

    public static Word getRightMostDep(Word w){
        for (int i =0; i<w.getSentence().size(); i++ ) {

            Word ww = w.getSentence().get(i);
            if (Integer.parseInt(ww.getHead()) == w.getIndex()+1)
                return ww;

        }
        return null;

    }


    // find the dependency path from scr to trg
    public String getDependencyPath(Word scr, Word trg, WordData d ){

        List<Word> scr_to_root = getPathToRoot(scr);
        List<Word> trg_to_root = getPathToRoot(trg);

        DependencyPath dp = new DependencyPath();

        for (int i=0;i<scr_to_root.size()-1; i++)
        {
            DependencyPathElement de = new DependencyPathElement(scr_to_root.get(i));
            de.setIsUp(true);
            dp.add(de);
        }
        DependencyPathElement der = new DependencyPathElement(scr_to_root.get(scr_to_root.size()-1));
        dp.add(der);

        for (int i=trg_to_root.size()-2;i>=0; i--)
        {
            DependencyPathElement de = new DependencyPathElement(trg_to_root.get(i));

            dp.add(de);
        }

        dp.get(dp.size()-1).setIsEnd(true);

        return dp.getPath(d);


    }

    private  static List<Word> getPathToRoot(Word w){
        Sentence s = w.getSentence();
        List<Word> arr = new ArrayList<>();
        Word tmp =w;
        while (!tmp.getHead().equals("0")){  //not root

            arr.add(tmp);
            tmp = tmp.getHeadWord();
        }

        arr.add(tmp);
        return arr;
    }

    public class DependencyPathElement{
        boolean isUp=false;
        boolean isEnd=false;

        public boolean isEnd() {
            return isEnd;
        }

        public void setIsEnd(boolean isEnd) {

            this.isEnd = isEnd;
        }

        Word val;
        public DependencyPathElement(Word val)
        {
            this.val = val;

        }

        public void setIsUp(boolean isUp) {
            this.isUp = isUp;
        }

        public void setVal(Word val) {
            this.val = val;
        }

        public boolean isUp() {

            return isUp;
        }

        public Word getVal() {
            return val;
        }
    }

    public class DependencyPath extends ArrayList<DependencyPathElement>{


        public String getPath(WordData d){
            StringBuffer sb =new StringBuffer();
            forEach( e -> {
                sb.append(e.getVal().getData(d));
                if (e.isUp())
                    sb.append("!up!");

                else if (!e.isEnd())
                    sb.append("!down!");


            });

            return sb.toString();
        }
    }
}
