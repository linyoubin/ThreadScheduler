package pers.linyoubin.tools.threadscheduler.test;

import java.util.List;

public class TestTools {
    public static <T> boolean compare(List<T> leftList, List<T> rightList) {
        if (null == leftList) {
            if (null == rightList) {
                return true;
            }
            else {
                return false;
            }
        }
        else if (null == rightList) {
            return false;
        }

        if (leftList.size() != rightList.size()) {
            return false;
        }

        for (int i = 0; i < leftList.size(); i++) {
            T left = leftList.get(i);
            T right = rightList.get(i);

            if (!left.equals(right)) {
                return false;
            }
        }

        return true;
    }
}
