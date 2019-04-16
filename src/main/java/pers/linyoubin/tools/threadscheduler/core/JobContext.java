package pers.linyoubin.tools.threadscheduler.core;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.linyoubin.tools.threadscheduler.exception.SchException;

class JobContext {
    private static final Logger logger = LoggerFactory.getLogger(JobContext.class);

    private Lock lock = new ReentrantLock();

    private ThreadScheduler ts;
    private long maxBlockingConfirmTime;
    private int step;

    private Set<Worker> notifyWorkers;
    private Set<MethodInfo> startMethods;
    private Set<MethodInfo> finishMethods;
    private Set<MethodInfo> blockingMethods;

    private WorkErrInfo error;

    public JobContext(ThreadScheduler ts, int step, Set<Worker> notifyWorkers,
            Set<MethodInfo> startMethods, Set<MethodInfo> finishMethods,
            Set<MethodInfo> blockingMethods) {
        this.ts = ts;
        this.step = step;
        this.notifyWorkers = notifyWorkers;
        this.startMethods = startMethods;
        this.finishMethods = finishMethods;
        this.blockingMethods = blockingMethods;

        int maxTime = 0;
        for (MethodInfo m : this.blockingMethods) {
            if (maxTime < m.getConfirmTime()) {
                maxTime = m.getConfirmTime();
            }
        }

        maxBlockingConfirmTime = 1000L * maxTime;
    }

    public long getMaxBlockingConfirmTime() {
        return maxBlockingConfirmTime;
    }

    public Set<Worker> getNeedNotifyWorkers() {
        return notifyWorkers;
    }

    void startJob(int step, MethodInfo m) throws SchException {
        lock.lock();
        try {
            if (this.step != step) {
                throw new SchException("method should be started in step:" + step
                        + ", while started in step:" + this.step + ", method=" + m);
            }

            if (!startMethods.contains(m)) {
                throw new SchException("method should not be started in step:" + step + ",method="
                        + m);
            }

            startMethods.remove(m);
            checkOrNotifySchedule();
        }
        finally {
            lock.unlock();
        }
    }

    void finishJob(int step, MethodInfo m) throws SchException {
        lock.lock();
        try {
            if (this.step != step) {
                throw new SchException("method should be finished in step:" + step
                        + ", while finished in step:" + this.step + ", method=" + m);
            }

            if (!finishMethods.contains(m)) {
                throw new SchException("method should not be finished in step:" + step + ",method="
                        + m);
            }

            finishMethods.remove(m);

            checkOrNotifySchedule();
        }
        finally {
            lock.unlock();
        }
    }

    private void checkOrNotifySchedule() {
        if (startMethods.size() == 0 && finishMethods.size() == 0) {
            ts.notifySchedule();
        }
    }

    public boolean checkJobsFinished() throws Exception {
        if (null != error) {
            logger.error("execute failed:step={},method={}", error.getStep(), error.getMethod());
            throw error.getException();
        }

        lock.lock();
        try {
            if (startMethods.size() == 0 && finishMethods.size() == 0) {
                return true;
            }
        }
        finally {
            lock.unlock();
        }

        return false;
    }

    public MethodInfo displayRemainMethod() throws SchException {
        lock.lock();
        try {
            if (startMethods.size() == 0 && finishMethods.size() == 0) {
                return null;
            }

            MethodInfo lastMethod = null;
            for (MethodInfo m : startMethods) {
                lastMethod = m;
                logger.error("method is not started:m={}", m);
            }

            for (MethodInfo m : finishMethods) {
                lastMethod = m;
                logger.error("method is not finished:m={}", m);
            }

            return lastMethod;
        }
        finally {
            lock.unlock();
        }
    }

    public int getStep() {
        return step;
    }

    public void saveErrInfo(int step, MethodInfo method, Exception exception) {
        error = new WorkErrInfo(step, method, exception);
    }

}
