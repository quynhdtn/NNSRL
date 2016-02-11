package liir.nlp.nnsrl.features;

import liir.nlp.nnsrl.ml.DataSet;
import liir.nlp.utils.PredicateData;
import liir.nlp.utils.WordData;
import liir.nlp.utils.WordExtractor;
import liir.nlp.utils.WordPairData;
import liir.nlp.we.WEDictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by quynhdo on 02/10/15.
 */
public class FeatureGenerator {
    List<Feature> featureList = new ArrayList<>();
    List<Feature> cacheFeatureList = new ArrayList<>();
    public static HashMap<String,WEDictionary> dictWEDict=new HashMap<>();

    //load features from a folder
    public FeatureGenerator(String folder) throws  Exception{

        if (Files.isDirectory(Paths.get(folder))) {      // load feature values from the folder

            Files.walk(Paths.get(folder)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {

                    if (filePath.toString().endsWith("_vob.txt")) {
                        String[] tmps=filePath.getFileName().toString().split("_");
                        if (tmps.length==2) {
                            String name = filePath.getFileName().toString().split("_")[0];
                            HashMap<String, String> fea_attrs = null;
                            if (Files.exists(Paths.get(filePath.getParent() + "/" + name + "_attr.txt"))) {
                                fea_attrs = new HashMap<String, String>();
                                try {
                                    List<String> lines = Files.readAllLines(Paths.get(filePath.getParent() + "/" + name + "_attr.txt"));
                                    for (String l : lines) {
                                        String[] tmpsForAttr = l.trim().split(":"); //get the attribute of the feature: dict path for WE fea for example
                                        if (tmpsForAttr.length > 1) {
                                            fea_attrs.put(tmpsForAttr[0], tmpsForAttr[1]);
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            FeatureName fn = FeatureName.valueOf(name);
                            Feature f = getFeature(fn, fea_attrs);
                            try {
                                f.loadFromFile(filePath.toAbsolutePath().toString());
                                featureList.add(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (tmps.length==3) {
                            String name = tmps[0]+ "_" +  tmps[1];
                            HashMap<String, String> fea_attrs = null;
                            if (Files.exists(Paths.get(filePath.getParent() + "/" + name + "_attr.txt"))) {
                                fea_attrs = new HashMap<String, String>();
                                try {
                                    List<String> lines = Files.readAllLines(Paths.get(filePath.getParent() + "/" + name + "_attr.txt"));
                                    for (String l : lines) {
                                        String[] tmpsForAttr = l.trim().split(":"); //get the attribute of the feature: dict path for WE fea for example
                                        if (tmpsForAttr.length > 1) {
                                            fea_attrs.put(tmpsForAttr[0], tmpsForAttr[1]);
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            Feature f1 = getFeature(FeatureName.valueOf(tmps[0]), null);
                            Feature f2 = getFeature(FeatureName.valueOf(tmps[1]), null);
                            CojoinFeature cf = new CojoinFeature(FeatureName.valueOf(name));
                            cf.addFeature(f1);
                            cf.addFeature(f2);

                            try {
                                cf.loadFromFile(filePath.toAbsolutePath().toString());
                                featureList.add(cf);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            });
       //     Collections.sort(featureList);
      //      System.out.println("sorted");
        }
        if (Files.isRegularFile(Paths.get(folder))){    // load feature names from a file then extract features from training data


            generateFeatures(folder);
        }

    }



    private void generateFeatures(String featureFile) throws IOException {

       List<String> lines= Files.readAllLines(Paths.get(featureFile));



        for (int i=0;i<lines.size(); i++)
        {

            if (! lines.get(i).startsWith("C")) {

                String[] tmps = lines.get(i).split("\\s+");
                if (tmps.length == 1) {   // single feature
                    //    System.out.println(tmps[0]);
                    FeatureName fn = FeatureName.valueOf(tmps[0]);
                    Feature f = getFeature(fn, null);
                    featureList.add(f);
                } else {
                    HashMap<String, String> fea_attrs = new HashMap<>(); // Feature attribute
                    for (int j = 1; j < tmps.length; j++) {
                        String[] tmpsForAttr = tmps[j].split(":"); //get the attribute of the feature: dict path for WE fea for example
                        if (tmpsForAttr.length > 1) {
                            fea_attrs.put(tmpsForAttr[0], tmpsForAttr[1]);
                        }
                    }
                    FeatureName fn = FeatureName.valueOf(tmps[0]);
                    Feature f = getFeature(fn, fea_attrs);
                    featureList.add(f);

                }

            }
            else{

                String[] tmps = lines.get(i).split("\\s+");
                if (tmps.length == 2) {   // co-joint feature
                    String[] tmpsForAttr = tmps[1].split("_");
                    if (tmpsForAttr.length==2) {

                        Feature f1 = getFeature(FeatureName.valueOf(tmpsForAttr[0]), null);
                        Feature f2 = getFeature(FeatureName.valueOf(tmpsForAttr[1]), null);
                        CojoinFeature cf = new CojoinFeature(FeatureName.valueOf(tmps[1]));
                        cf.addFeature(f1);
                        cf.addFeature(f2);
                        featureList.add(cf);
                    }
                }





            }


        }


        /*
        if (prop.getProperty("cojoin_feature") != null) {
            String[] cojoinFeatureNames = prop.getProperty("cojoin_feature").split(",");

            for (int i = 0; i < cojoinFeatureNames.length; i++) {
                String[] tmps = cojoinFeatureNames[i].split(":");
                CojoinFeature cf = new CojoinFeature(FeatureName.valueOf(cojoinFeatureNames[i]));
                for (int j = 0; j < tmps.length; j++) {
                    if (!ftmps.containsKey(cojoinFeatureNames[j].trim())) {
                        FeatureName fn = FeatureName.valueOf(cojoinFeatureNames[j].trim());
                        Feature f = getFeature(fn);
                        cacheFeatureList.add(f);
                        ftmps.put(cojoinFeatureNames[j].trim(), f);
                        cf.addFeature(f);
                    } else {
                        Feature f = ftmps.get(cojoinFeatureNames[j].trim());
                        cf.addFeature(f);
                    }

                }

                featureList.add(cf);
            }


        }
        */
       // Collections.sort(this.featureList);
    }

    public List<Feature> getFeatureList() {
        return featureList;
    }


    public void extractFeature(DataSet ds){
  //      cacheFeatureList.forEach(f -> ds.extractFeature(f));

        featureList.forEach(f -> {
            long startTime = System.currentTimeMillis();
            System.out.println("Extracting feature " + f.getName().toString());
            ds.extractFeature(f);
            long endTime = System.currentTimeMillis();
            System.out.println(endTime-startTime);
        });
    }


    public void printFeatureValue(DataSet ds){

        ds.forEach(ins -> {
            ins._1().printFeatureVals(featureList);
            System.out.println("\n");
        });

    }


    public void printFeatureValueAs1HotVector(DataSet ds){

        ds.forEach(ins -> {
            ins._1().printFeatureValsAs1HotVector(featureList);
            System.out.println("\n");
        });

    }

    public void getSize(){
        featureList.forEach(f -> System.out.format(f.name + ": %d \n", f.getSize()) );
    }
    private Feature getFeature( FeatureName fn, HashMap<String, String> fea_attr){

        switch (fn){
            case Word: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.Word, 0);
            case Lemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.Word, 0);
            case Pos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.Word, 0);
            case Deprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.Word, 0);


            case ParentWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.Parent, 0);
            case ParentPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.Parent, 0);
            case ParentLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.Parent, 0);
            case ParentDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.Parent, 0);

            case RightWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.RightNeighbour, 0);
            case RightLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.RightNeighbour, 0);
            case RightPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.RightNeighbour, 0);
            case RightDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.RightNeighbour, 0);

            case LeftWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.LeftNeighbour, 0);
            case LeftLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.LeftNeighbour, 0);
            case LeftPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.LeftNeighbour, 0);
            case LeftDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.LeftNeighbour, 0);


            case RightDepWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.RightDep, 0);
            case RightDepLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.RightDep, 0);
            case RightDepPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.RightDep, 0);
            case RightDepDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.RightDep, 0);

            case LeftDepWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.LeftDep, 0);
            case LeftDepLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.LeftDep, 0);
            case LeftDepPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.LeftDep, 0);
            case LeftDepDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.LeftDep, 0);

            case RightSiblingWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.RightSibling, 0);
            case RightSiblingLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.RightSibling, 0);
            case RightSiblingPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.RightSibling, 0);
            case RightSiblingDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.RightSibling, 0);

            case LeftSiblingWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.LeftSibling, 0);
            case LeftSiblingLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.LeftSibling, 0);
            case LeftSiblingPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.LeftSibling, 0);
            case LeftSiblingDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.LeftSibling, 0);

            case PredWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.Word, 1);
            case PredLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.Word, 1);
            case PredPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.Word, 1);
            case PredDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.Word, 1);
            case PredSense: return new PredicateFeature(fn, PredicateData.sense, 1);

            case PredParentWord: return new WordFeature(fn, WordData.word, WordExtractor.TargetWord.Parent, 1);
            case PredParentPos: return new WordFeature(fn, WordData.pos, WordExtractor.TargetWord.Parent,1);
            case PredParentLemma: return new WordFeature(fn, WordData.lemma, WordExtractor.TargetWord.Parent, 1);
            case PredParentDeprel: return new WordFeature(fn, WordData.deprel, WordExtractor.TargetWord.Parent, 1);

            case Position: return new WordPairFeature(fn, WordPairData.position);
            case Distance: return new WordPairNumericFeature(fn, WordPairData.distance);
            case WordDepPath: return new WordPairFeature(fn, WordPairData.wordDepPath);
            case LemmaDepPath: return new WordPairFeature(fn, WordPairData.lemmaDepPath);
            case PosDepPath: return new WordPairFeature(fn,WordPairData.posDepPath);
            case DepDepPath: return new WordPairFeature(fn, WordPairData.deprelDepPath);

            case ChildWordSet: return new WordFeature(fn, WordData.child_word_set, WordExtractor.TargetWord.Word, 0);
            case ChildPosSet: return new WordFeature(fn, WordData.child_pos_set, WordExtractor.TargetWord.Word, 0);
            case ChildLemmaSet: return new WordFeature(fn, WordData.child_lemma_set, WordExtractor.TargetWord.Word, 0);
            case ChildDepSet: return new WordFeature(fn, WordData.child_dep_set, WordExtractor.TargetWord.Word, 0);
            case ChildWordDepSet: return new WordFeature(fn, WordData.child_word_dep_set, WordExtractor.TargetWord.Word, 0);
            case ChildPosDepSet: return new WordFeature(fn, WordData.child_pos_dep_set, WordExtractor.TargetWord.Word, 0);



            case DepSubCat: return new WordFeature(fn, WordData.dep_sub_cat, WordExtractor.TargetWord.Word, 0);







            case WordWE: {
                WordFeature wf = new WordFeature(fn, WordData.word, WordExtractor.TargetWord.Word, 0);
                WEDictionary dict = null;
                if (this.dictWEDict.containsKey(fea_attr.get("wePath")))
                    dict = this.dictWEDict.get(fea_attr.get("wePath"));
                            else
                {
                    dict = new WEDictionary(fea_attr.get("wePath"));
                    this.dictWEDict.put(fea_attr.get("wePath"),dict);
                }
                return new WEFeature(fn, dict, wf, true);
            }

            case ChildWordSetWE: {
                WordFeature wf = new WordFeature(fn, WordData.child_word_set, WordExtractor.TargetWord.Word, 0);
                WEDictionary dict = null;
                if (this.dictWEDict.containsKey(fea_attr.get("wePath")))
                    dict = this.dictWEDict.get(fea_attr.get("wePath"));
                else
                {
                    dict = new WEDictionary(fea_attr.get("wePath"));
                    this.dictWEDict.put(fea_attr.get("wePath"),dict);
                }
                return new WEFeature(fn, dict, wf, true);
            }

            case ParentWordWE:{
                WordFeature wf = new  WordFeature(fn, WordData.word, WordExtractor.TargetWord.Parent, 0);
                WEDictionary dict = null;
                if (this.dictWEDict.containsKey(fea_attr.get("wePath")))
                    dict = this.dictWEDict.get(fea_attr.get("wePath"));
                else
                {
                    dict = new WEDictionary(fea_attr.get("wePath"));
                    this.dictWEDict.put(fea_attr.get("wePath"),dict);
                }
                return new WEFeature(fn, dict, wf, true);
            }

        }

        return null;
    }
}


