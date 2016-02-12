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
                break;
            }
        }

        for (int i=scr_to_root.size()-1;i>=commonIndex+1; i--)
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

        for (int i=commonIndex+1; i < trg_to_root.size(); i++) {

            sb.append(trg_to_root.get(i).getData(d));
          //  sb.append(trg_to_root.get(i).getDeprel());

            sb.append("!down!");
        }

        return sb.toString();


    }
*/

    public String getDependencyPath(Word scr, Word trg, WordData d ){

      //  Tuple2<List<Word>,List<Integer>> scr_to_roota =getPathToRoot(scr);
     //   Tuple2<List<Word>,List<Integer>> trg_to_roota =getPathToRoot(trg);

        List<Word> scr_to_root = getPathToRoot(scr);
        List<Word> trg_to_root = getPathToRoot(trg);

     //   List<Integer>scr_to_root2 = scr_to_roota._2();
      //  List<Integer>trg_to_root2 = trg_to_roota._2();



        StringBuilder sb=new StringBuilder();
        int s1 = scr_to_root.size();
        int cmidx =0;
            for (int i = 0; i < s1; ++i) {
                sb.append(scr_to_root.get(i).getData(d));

                //  if (trg_to_root.contains(scr_to_root.get(i))){
                if (trg_to_root.contains(scr_to_root.get(i))) {

                    sb.append("!down!");
                    cmidx = trg_to_root.indexOf(scr_to_root.get(i));
                    break;
                } else
                    sb.append("!up!");
            }
            for (int j = cmidx - 1; j >= 0; j--) {
                sb.append(trg_to_root.get(j).getData(d));
                sb.append("!down!");
            }





        /*
        for (int i=0;i<min;++i){
            if(scr_to_root.get(s1-1-i)==trg_to_root.get(s2-1-i))
            {
                commonIndexScr = s1-1-i;
                commonIndexTgr=s2-1-i;
                break;
            }

        }*/
        /*

        for(int i=0;i<scr_to_root.size();++i) {
            if(trg_to_root.contains(scr_to_root.get(i))){ //Always true at root (ie first index)
                commonIndexScr=i;
                commonIndexTgr = trg_to_root.indexOf(scr_to_root.get(i));
                break;
            }
        }



        for (int i=0;i<commonIndexScr; i++)
        {
    //        DependencyPathElement de = new DependencyPathElement(scr_to_root.get(i));
      //      de.setIsUp(true);
        //    dp.add(de);
            sb.append(scr_to_root.get(i).getData(d));
           sb.append("!up!");

        }
     //   DependencyPathElement der = new DependencyPathElement(scr_to_root.get(commonIndexScr));
       // dp.add(der);

        sb.append(scr_to_root.get(commonIndexScr).getData(d));
        sb.append("!down!");

        for (int i=commonIndexTgr-1;i>=0; i--)
        {
        //    DependencyPathElement de = new DependencyPathElement(trg_to_root.get(i));

          //  dp.add(de);
            sb.append("!down!");
            sb.append(trg_to_root.get(i).getData(d));
            //  sb.append(trg_to_root.get(i).getDeprel());


        }

//        dp.get(dp.size()-1).setIsEnd(true);

  //      return dp.getPath(d);
*/
        return sb.toString();


    }


/*
    private  static List<Word> getPathToRoot(Word w){
    //    Sentence s = w.getSentence();
        List<Word> arr = new ArrayList<>();
        Word tmp =w;
        while (!tmp.getHead().equals("0")){  //not root

            arr.add(tmp);
            tmp = getHead(tmp);
        }

        arr.add(tmp);
        return arr;
    }*/

    /*
    private  static Tuple2<List<Word>,List<Integer>> getPathToRoot(Word w){
        //    Sentence s = w.getSentence();
        List<Word> arr = new ArrayList<>();
        List<Integer> arrIdx = new ArrayList<>();

        Word tmp =w;
        while (!tmp.getHead().equals("0")){  //not root

            arr.add(tmp);
            arrIdx.add(tmp.getId());
            tmp = getHead(tmp);
        }

        arr.add(tmp);
        arrIdx.add(tmp.getId());
        return new Tuple2<>(arr,arrIdx);
    }
*/

    private  static List<Word> getPathToRoot(Word w){
        //    Sentence s = w.getSentence();
        List<Word> arr = new ArrayList<>();

        Word tmp =w;
        while (!tmp.getHead().equals("0")){  //not root

            arr.add(tmp);
            tmp = getHead(tmp);
        }

        arr.add(tmp);
        return arr;
    }

/*
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
    */
}
