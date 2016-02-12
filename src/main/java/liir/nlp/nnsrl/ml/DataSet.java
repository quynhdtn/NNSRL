package liir.nlp.nnsrl.ml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import liir.nlp.nnsrl.features.CojoinFeature;
import liir.nlp.nnsrl.features.Feature;
import liir.nlp.nnsrl.features.WEFeature;
import scala.Tuple2;
/**
 * Created by quynhdo on 02/10/15.
 */
public class DataSet extends ArrayList<Tuple2<Instance, String>> {

    List<String> labels= new ArrayList<>();

    List<Feature> feature_set;


    public void setFeature_set(List<Feature> feature_set) {
        this.feature_set = feature_set;
    }

    public void extractFeature(Feature f){
        if (f.getValue_caches()!=null){
            if (f.getValue_caches().size() == this.size()){
                System.out.println("Loading feature value from file!");
                for (int i=0;i<this.size();i++)
                    this.get(i)._1().extractFeature(f, f.getValue_caches().get(i));
            }
        }
        else {
            final int[] c = {0};
            System.out.println("total number of instances: ");
            System.out.println(this.size());

            this.forEach(ins -> {
                ins._1().extractFeature(f);
                c[0]++;
                if (c[0] % 100000 == 0) {
                    System.out.print("Have processed ");
                    System.out.println(c[0]);
                }
            });
        }
    }


    /// path should be the directory
    public void saveData(List<Feature> fl, String path) throws IOException {
        File f = new File(path);
        if (!f.exists()){
            f.mkdir();
        }
        else
        if (!f.isDirectory()){
            System.out.println("Path should be a directory.");
            return;
        }

        ArrayList<String> lines= new ArrayList<>();
        this.forEach(t -> {
            StringBuilder sb = new StringBuilder();
            sb.append(t._1().getInfo());
            sb.append("\t");
            sb.append(t._2());
        //    sb.append("\n");
            lines.add(sb.toString());
        });


        Files.write(Paths.get(path + "/data.txt"), lines);



        //write data for each feature

        File ffolder = new File(path + "/fea");
        ffolder.mkdir();

        fl.forEach(fea -> {

            try {
                Files.write(Paths.get(path + "/fea/"+fea.getName() + "_vob.txt"), fea.getVocabulary());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fea instanceof WEFeature){

                try {
                    List<String> lines1= new ArrayList<String>();
                    lines1.add("wePath:" + ((WEFeature) fea).getWeDict().getPath());
                    Files.write(Paths.get(path + "/fea/"+fea.getName() + "_attr.txt"),lines1);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });


        fl.forEach(fea -> {
            lines.clear();


            this.forEach(t -> {

                //     lines.add(Arrays.toString(fea.asOneHotVector(t._1().featureCaches.get(fea.getName()))));
                String df = fea.asSparseFormat(t._1().featureCaches.get(fea.getName()),0);
                if (df.equals(""))

                    lines.add("-");
                else
                    lines.add(df);


            });

            try {
                Files.write(Paths.get(path + "/" + fea.getName() + ".txt"), lines);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });


    }

    public Float[][] getData(){

        ArrayList<Float[]> rs = new ArrayList<>();

        this.forEach(t -> rs.add(t._1().asFeatureArray(this.feature_set)));

        Float[][] rss = new Float[this.size()][rs.get(0).length];

        rs.toArray(rss);

        return rss;

    }

    public int[] getLabels(){
        int[] lbls = new int[this.size()];
        for (int i=0;i<this.size();i++)
            lbls[i]= labels.indexOf(this.get(i)._2());

        return lbls;
    }


    public String getLabelAsString(int i){
        return labels.get(i);
    }

    public void getDataAndLabelAsSparse(String output) throws IOException {

        Collections.sort(this.feature_set);

        List<String> lines = new ArrayList<>();

        int dim = 0;

        for (Feature f : feature_set)
        dim+=f.getSize();


        //get labels
        int[] lbls = new int[this.size()];
        for (int i=0;i<this.size();i++) {
            String la = this.get(i)._2();
            if (!labels.contains(la))
                labels.add(la);

        }

        Collections.sort(labels);

        for (int i=0;i<this.size();i++) {
            String la = this.get(i)._2();

            lbls[i]= labels.indexOf(la);



        }


        //get feature values

        for (int i = 0; i < this.size(); i++) {  //loop over all instances
            int offset = 1;
         //   String l = String.valueOf(lbls[i] + " ");
            String l=this.get(i)._2() + " ";
            for (Feature f : feature_set){

                l+= f.asSparseFormat(this.get(i)._1().featureCaches.get(f.getName()), offset);

                offset+= f.getSize();

        //        System.out.println(offset);
            }

            lines.add(l);

        }



        Files.write(Paths.get(output), lines);
    }

}
