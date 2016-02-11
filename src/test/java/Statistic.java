import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Word;
import liir.nlp.nnsrl.io.FullCoNLL2009Reader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quynhdo on 10/11/15.
 */
public class Statistic {


    public static int[] getPOSStatistic(String input){
        FullCoNLL2009Reader cr = new FullCoNLL2009Reader(new File(input));
        int countN=0;
        int correctN=0;
        final int[] count = {0,0,0,0,0,0};
        int correctV=0;

        List<Sentence> sens= cr.readAll();
        sens.forEach(s -> {
            s.getPredicates().forEach(w -> {

                if (((Word) w).getGoldPos().startsWith("V")) {
                    count[0]++;
                    if (((Word) w).getPos().startsWith("V"))
                        count[1]++;
                } else {
                    if (((Word) w).getPos().startsWith("V"))
                        count[2]++;
                }


                if (((Word) w).getGoldPos().startsWith("N")) {
                    count[3]++;
                    if (((Word) w).getPos().startsWith("N"))
                        count[4]++;
                } else {
                    if (((Word) w).getPos().startsWith("N"))
                        count[5]++;
                }

            });
        });

       return count;
    }


    public static  void main(String[] args){
        String input = "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-evaluation-ood.txt";
      System.out.println(  Arrays.toString(Statistic.getPOSStatistic(input)));

    }

}
