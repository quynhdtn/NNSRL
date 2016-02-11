package liir.nlp.utils;

import liir.nlp.core.representation.Word;
import scala.Tuple2;

/**
 * Created by quynhdo on 02/10/15.
 */
public class DistanceUtil {
    public enum RelativePosition{
        same,
        overlap,
        before,
        after
    }
    public static RelativePosition getRelativePosition(Word scr, Word  trg){
        if (scr.getIndex() < trg.getIndex())
            return RelativePosition.before;

        if (scr.getIndex() > trg.getIndex())
            return RelativePosition.after;


        if (scr.getIndex() == trg.getIndex())
            return RelativePosition.same;

        return null;

    }

    public static int getLinearPosition(Word scr, Word  trg){
        return scr.getIndex() - trg.getIndex();
    }
}
