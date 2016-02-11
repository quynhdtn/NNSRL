package liir.nlp.utils;

/**
 * Created by quynhdo on 30/09/15.
 */
import org.apache.log4j.Category;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;


//this class is used to read Brown cluster
public  class BrownClusterReader {


    static ArrayList<ArrayList<String>> clusters= new ArrayList<ArrayList<String>>();
    static HashMap<Integer,Integer> clusterid_to_clusters = new HashMap<Integer,Integer>();
    static HashMap<String,Integer> word_to_cluster = new HashMap<String,Integer>();

  //  final static Logger logger = Logger.getLogger(String.valueOf(BrownClusterReader.class));

    public BrownClusterReader (String source) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get(source), StandardCharsets.UTF_8);
        for (String l : lines){
            String[] tmps = l.split("\\s+");
            if (tmps.length==3){
                int clid = convertBitToInt(tmps[0]);
                System.out.println(clid);
                if (clusterid_to_clusters.containsKey(clid))
                {
                    clusters.get(clusterid_to_clusters.get(clid)).add(tmps[1].toLowerCase());
                    word_to_cluster.put(tmps[1].toLowerCase(), clusterid_to_clusters.get(clid));

                }
                else
                {
                    ArrayList<String> cl = new ArrayList<String>();
                    cl.add(tmps[1].toLowerCase());
                    clusters.add(cl);
                    clusterid_to_clusters.put(clid, clusters.size()-1);
                    word_to_cluster.put(tmps[1].toLowerCase(), clusterid_to_clusters.get(clid));

                }

            }
        }

    }

    public void printCluster(){

        for (ArrayList<String> cl : clusters){
            for (String w : cl)
                System.out.print(w + "\t");
            System.out.println();
        }

        System.out.println( "Total number of clusters: " + clusters.size());
    }

    private Integer convertBitToInt(String s){
        int v =0;

        for (int i = 0; i<s.length(); i++){
            v += Integer.parseInt(s.substring(s.length()-i-1, s.length()-i))*Math.pow(2,s.length()-i-1 ) ;
        }
        return v;


    }

    public Integer getCluster(String w){
        return this.word_to_cluster.get(w);
    }

    public static void main (String[] args) throws IOException {
        BrownClusterReader br =new BrownClusterReader("/Users/quynhdo/Desktop/paths");
        br.printCluster();
    }

}
