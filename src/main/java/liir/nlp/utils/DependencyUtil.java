package liir.nlp.utils;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Word;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 30/09/15.
 */
public class DependencyUtil {

    public static Word getHead(Word w){
        int hidx = Integer.parseInt(w.getHead());
        if (hidx == 0) return null;
        return w.getSentence().get(hidx-1);
    }


    public static List<Word> getChildren(Word w){
        List<Word> arr = new ArrayList<>();
        w.getSentence().forEach(ww -> {if (Integer.parseInt(ww.getHead()) == w.getIndex()+1)
        arr.add(ww);
        });
        return arr;
    }




    public static Word getLeftSibling(Word w){
        Word h = getHead(w);
        if (h==null) return null;
        for (int i = w.getIndex()-1; i>=0; i-- ) {

            Word ww = w.getSentence().get(i);
            if (Integer.parseInt(ww.getHead()) == h.getIndex()+1)
                return ww;
        }


        return null;
    }

    public static Word getRightSibling(Word w){
        Word h = getHead(w);
        if (h==null) return null;
        for (int i = w.getIndex()+1; i< w.getSentence().size(); i++ ) {

            Word ww = w.getSentence().get(i);
            if (Integer.parseInt(ww.getHead()) == h.getIndex()+1)
                return ww;
        }


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
    /*
    public String getDependencyPath(Word scr, Word trg, WordData d ){

        List<Word> scr_to_root = getPathToRoot(scr);
        List<Word> trg_to_root = getPathToRoot(trg);

        StringBuilder sb=new StringBuilder();



   //     DependencyPath dp = new DependencyPath();

        int min=(scr_to_root.size()<trg_to_root.size()?scr_to_root.size():trg_to_root.size());
        int commonIndex=0;
        for(int i=0;i<min;++i) {
            if(scr_to_root.get(i)==trg_to_root.get(i)){ //Always true at root (ie first index)
                commonIndex=i;
            }
        }

        for (int i=0;i<commonIndex-1; i++)
        {
       //     DependencyPathElement de = new DependencyPathElement(scr_to_root.get(i));
        //    de.setIsUp(true);
        //    dp.add(de);
            sb.append(scr_to_root.get(i).getData(d));
         //   sb.append(scr_to_root.get(i).getDeprel());

            sb.append("!up!");
        }

        sb.append(scr_to_root.get(commonIndex).getData(d));
        sb.append("!down!");

        for (int i=commonIndex-1; i >= 0; i--) {

            sb.append(trg_to_root.get(i).getData(d));
          //  sb.append(trg_to_root.get(i).getDeprel());

            sb.append("!down!");
        }

        return sb.toString();


    }*/

    public String getDependencyPath(Word scr, Word trg, WordData d ){

        List<Word> scr_to_root = getPathToRoot(scr);
        List<Word> trg_to_root = getPathToRoot(trg);

        DependencyPath dp = new DependencyPath();
        int min=(scr_to_root.size()<trg_to_root.size()?scr_to_root.size():trg_to_root.size());
        int commonIndexScr=0;
        int commonIndexTgr=0;

        for(int i=0;i<scr_to_root.size();++i) {
            if(trg_to_root.contains(scr_to_root.get(i))){ //Always true at root (ie first index)
                commonIndexScr=i;
                commonIndexTgr = trg_to_root.indexOf(scr_to_root.get(i));
            }
        }

        for (int i=0;i<commonIndexScr; i++)
        {
            DependencyPathElement de = new DependencyPathElement(scr_to_root.get(i));
            de.setIsUp(true);
            dp.add(de);
        }
        DependencyPathElement der = new DependencyPathElement(scr_to_root.get(commonIndexScr));
        dp.add(der);

        for (int i=commonIndexTgr-1;i>=0; i--)
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
            tmp = getHead(tmp);
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
