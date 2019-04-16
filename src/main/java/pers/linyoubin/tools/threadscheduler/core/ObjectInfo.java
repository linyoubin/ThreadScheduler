package pers.linyoubin.tools.threadscheduler.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pers.linyoubin.tools.threadscheduler.exception.SchException;

class ObjectInfo {
    private List<MethodInfo> methodList = new ArrayList<>();

    void addMethod(MethodInfo methodInfo) {
        methodList.add(methodInfo);
    }

    List<MethodInfo> getMothList() {
        return methodList;
    }

    void sortByStep() throws SchException {
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

        if (methodList.size() > 0) {
            int prevStep = methodList.get(0).getStep();
            for (int i = 1; i < methodList.size(); i++) {
                MethodInfo m = methodList.get(i);
                int step = m.getStep();
                if (prevStep == step) {
                    throw new SchException("can't exist same step in one Object:Object="
                            + m.getObject() + ",step=" + step);
                }

                prevStep = step;
            }
        }
    }
}
