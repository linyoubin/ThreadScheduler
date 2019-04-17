package pers.linyoubin.tools.threadscheduler.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pers.linyoubin.tools.threadscheduler.exception.SchException;

class JobInfo {
    // step <-> methods
    private Map<Integer, Set<MethodInfo>> methodMap = new HashMap<>();

    // contStep <-> methods
    private Map<Integer, Set<MethodInfo>> contMethodsMap = new HashMap<>();

    private Map<Object, Worker> object2Worker = new HashMap<>();

    public JobInfo() {
    }

    public List<Integer> getStepList() {
        List<Integer> stepList = new ArrayList<>();
        for (Integer step : methodMap.keySet()) {
            stepList.add(step);
        }

        return stepList;
    }

    public JobContext createJobContext(ThreadScheduler ts, int step) {
        Set<MethodInfo> startMethods = new HashSet<>();
        Set<MethodInfo> finishMethods = new HashSet<>();
        Set<MethodInfo> blockingMethods = new HashSet<>();
        Set<Worker> notifyWorkers = new HashSet<>();

        Set<MethodInfo> stepMethods = methodMap.get(step);
        if (null != stepMethods) {
            for (MethodInfo m : stepMethods) {
                startMethods.add(m);
                notifyWorkers.add(object2Worker.get(m.getObject()));

                if (!m.isExpectBlock()) {
                    finishMethods.add(m);
                }
                else {
                    blockingMethods.add(m);
                }
            }
        }

        Set<MethodInfo> contMethods = contMethodsMap.get(step);
        if (null != contMethods) {
            for (MethodInfo m : contMethods) {
                finishMethods.add(m);
            }
        }

        JobContext context = new JobContext(ts, step, notifyWorkers, startMethods, finishMethods,
                blockingMethods);
        return context;
    }

    public void addJob(Object o, ObjectInfo info, Worker worker) throws SchException {
        object2Worker.put(o, worker);

        info.sortByStep();
        for (MethodInfo mInfo : info.getMothList()) {
            addToMethodMap(mInfo);

            if (mInfo.isExpectBlock()) {
                addToContMap(mInfo);
            }
        }
    }

    private void addToMethodMap(MethodInfo mInfo) {
        Set<MethodInfo> methodSet = methodMap.get(mInfo.getStep());
        if (null == methodSet) {
            methodSet = new HashSet<>();
            methodMap.put(mInfo.getStep(), methodSet);
        }
        methodSet.add(mInfo);
    }

    public void validCheck() throws SchException {
        for (Set<MethodInfo> methodSet : contMethodsMap.values()) {
            Iterator<MethodInfo> iter = methodSet.iterator();
            while (iter.hasNext()) {
                MethodInfo m = iter.next();
                checkExistFinishStep(m);
            }
        }
    }

    private void checkExistFinishStep(MethodInfo checkingMethod) throws SchException {
        Set<MethodInfo> methodSet = methodMap.get(checkingMethod.getContStep());
        Iterator<MethodInfo> iter = methodSet.iterator();
        while (iter.hasNext()) {
            MethodInfo m = iter.next();
            if (!m.isExpectBlock()) {
                return;
            }
        }

        throw new SchException("method's contStep is not exist:method=" + checkingMethod);
    }

    Set<MethodInfo> getStartMethods(int step) {
        return methodMap.get(step);
    }

    Set<MethodInfo> getContMethods(int step) {
        return contMethodsMap.get(step);
    }

    private void addToContMap(MethodInfo mInfo) {
        Set<MethodInfo> methodSet = contMethodsMap.get(mInfo.getContStep());
        if (null == methodSet) {
            methodSet = new HashSet<>();
            contMethodsMap.put(mInfo.getContStep(), methodSet);
        }

        methodSet.add(mInfo);
    }

    public int getJobCount() {
        return object2Worker.size();
    }

    public Set<Worker> getWorkers() {
        Set<Worker> workerSet = new HashSet<>();
        for (Worker w : object2Worker.values()) {
            workerSet.add(w);
        }

        return workerSet;
    }

}
