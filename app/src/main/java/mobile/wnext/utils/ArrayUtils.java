package mobile.wnext.utils;

import java.util.ArrayList;

/**
 * Created by Nnguyen on 22/01/2015.
 */
public final class ArrayUtils {
    public static <T> ArrayList<T> toList(final T[] list) {
        return new ArrayList<T>(){{
            for(int i=0;i<list.length;i++) {
                add(list[i]);
            }
        }};
    }
}
