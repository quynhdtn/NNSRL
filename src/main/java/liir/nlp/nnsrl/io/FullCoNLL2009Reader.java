package liir.nlp.nnsrl.io;

import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Word;
import liir.nlp.core.representation.Predicate;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by quynhdo on 10/11/15.
 */
public class FullCoNLL2009Reader  implements Iterable<Sentence> {

    protected static final Pattern NEWLINE_PATTERN= Pattern.compile("\n");
    protected static final Pattern WHITESPACE_PATTERN=Pattern.compile("\\s+");

    protected BufferedReader in;
    protected Sentence nextSen;
    protected File file;


    public FullCoNLL2009Reader(File file) {
        this.file=file;
        open();
    }


    protected void restart() {
        try {
            in.close();
            open();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    protected void open(){
        System.out.println("Opening reader for "+file+"...");
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));

            readNextSentence();
        } catch (IOException e) {
            System.out.println("Failed: "+e.toString());
            System.exit(1);
        }
    }

    protected void readNextSentence() throws IOException{
        String str;
        Sentence sen=null;
        StringBuilder senBuffer=new StringBuilder();


        while ((str = in.readLine()) != null) {
            if(!str.trim().equals("")) {
                senBuffer.append(str).append("\n");
            } else {
                sen=newSentence((NEWLINE_PATTERN.split(senBuffer.toString())));
                break;
            }
        }
        if(sen==null){
            nextSen=null;
            in.close();
        } else {
            nextSen=sen;
        }



    }



    protected void buildSemanticTree(Sentence s, List<String[]> argscache){
        for(int i=0;i<s.getPredicates().size();++i){
            Predicate pred=s.getPredicates().get(i);
            for(int j=0;j<s.size();++j){
                String arg=argscache.get(j)[i];
                if(!arg.equals("_"))
                    pred.addArgument(String.valueOf(j), arg); //store the index of the argument word in pred argumnet, it may be different when we move to another dataset
            }
        }

        argscache.clear();


    }

    protected Sentence getSentence(){
        Sentence ret=nextSen;
        try {
            readNextSentence();
        } catch(IOException e){
            System.out.println("Failed to read from corpus file... exiting.");
            System.exit(1);
        }
        return ret;
    }


    public List<Sentence> readAll() {
        ArrayList<Sentence> ret=new ArrayList<Sentence>();
        int i=0;
        for(Sentence s:this) {
            i++;
            s.setId(String.valueOf(i));

            ret.add(s);
        }
        ret.trimToSize();
        return ret;
    }

    public Iterator<Sentence> iterator() {
        if(nextSen==null)
            restart();
        return new SentenceIterator();
    }


    public void close()  {
        try {
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected class SentenceIterator implements Iterator<Sentence> {
        @Override
        public boolean hasNext() {
            return nextSen!=null;
        }

        @Override
        public Sentence next() {
            return getSentence();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not implemented");
        }

    }

    public Sentence newSentence(String[] lines){


        Sentence ret=new Sentence();
        Word nextWord;
        int ix=1;

        List<String[]> argscache = new ArrayList<>(); // store argument information

        for(String line:lines){
            String[] cols=WHITESPACE_PATTERN.split(line);
            if(cols[12].charAt(0)=='Y'){
                Word w = new Word();
                w.setId(cols[0]);
                w.setStr(cols[1]);

                w.setLemma(cols[3]);
                w.setPos(cols[5]);
                w.setHead(cols[9]);
                w.setDeprel(cols[11]);


                w.setGoldLemma(cols[2]);
                w.setGoldPos(cols[4]);
                w.setGoldHead(cols[8]);
                w.setGoldDeprel(cols[10]);

                Predicate pred=new Predicate(w);
                nextWord=pred;


                pred.setSense(cols[13].split("\\.")[1]);

                if(cols.length>=14){

                    String[]  args=new String[cols.length-14];

                    for(int i=0;i<cols.length-14;++i){
                        args[i]=cols[14+i];
                    }

                    argscache.add(args);
                }

            } else {
                nextWord = new Word();
                nextWord.setId(cols[0]);
                nextWord.setStr(cols[1]);
                nextWord.setLemma(cols[3]);
                nextWord.setPos(cols[5]);
                nextWord.setHead(cols[9]);
                nextWord.setDeprel(cols[11]);

                nextWord.setGoldLemma(cols[2]);
                nextWord.setGoldPos(cols[4]);
                nextWord.setGoldHead(cols[8]);
                nextWord.setGoldDeprel(cols[10]);
                if(cols.length>=14){

                    String[]  args=new String[cols.length-14];

                    for(int i=0;i<cols.length-14;++i){
                        args[i]=cols[14+i];
                    }

                    argscache.add(args);
                }
            }

            ret.add(nextWord);
            nextWord.setSentence(ret);
        }
             buildDependencyTree(ret);
        buildSemanticTree(ret, argscache);

        return ret;
    }

    protected void buildDependencyTree(Sentence s){
        for(int i=0;i<s.size();++i){
            int hIdx = Integer.parseInt(s.get(i).getHead());
            if (hIdx>=1)
            {
                s.get(i).setHeadWord(s.get(hIdx-1));
                s.get(hIdx-1).addChild(s.get(i));
            }

        }

    }

}
