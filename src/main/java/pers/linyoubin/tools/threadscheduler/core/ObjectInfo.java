package pers.linyoubin.tools.threadscheduler.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ObjectInfo {
    List<MethodInfo> methodList = new ArrayList<>();

    void addMethod(MethodInfo methodInfo) {
        methodList.add(methodInfo);
    }

    void sortByStep() {
        Collections.sort(methodList, new Comparator<MethodInfo>() {
            @Override
            public int compare(MethodInfo l, MethodInfo r) {
                if (l.getStep() > r.getStep()) {
                    return 1;
                }
                else if (l.getStep() < r.getStep()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
    }
}
