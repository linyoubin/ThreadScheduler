package pers.linyoubin.tools.threadscheduler.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.linyoubin.tools.threadscheduler.exception.SchException;
import pers.linyoubin.tools.threadscheduler.tool.SchTool;

public class ThreadScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ThreadScheduler.class);

    private static final long PERIOD_WAIT_TIME = 1000;
    private static final long DEFAULT_MAX_RUNTIME_PER_STEP = 120000;

    private JobInfo jobInfo = new JobInfo();
    private JobContext context;

    private long maxStepRuntime;

    public ThreadScheduler(long maxStepRuntime) {
        this.maxStepRuntime = maxStepRuntime;
    }

    public ThreadScheduler() {
        this(DEFAULT_MAX_RUNTIME_PER_STEP);
    }

    public void addJob(Object o) throws SchException {
        ObjectInfo info = AnnoParser.parse(o);
        // add step 0 for job's same starting line
        info.addMethod(new MethodInfo(o, null, 0, "innerStep"));

        Worker w = new Worker(this, o, info);
        jobInfo.addJob(o, info, w);
    }

    public void run() throws SchException {
        jobInfo.validCheck();
        // start all workers
        ScheduledExecutorService service = Executors.newScheduledThreadPool(jobInfo.getJobCount());

        try {
            for (Worker w : jobInfo.getWorkers()) {
                service.execute(w);
            }

            // start to do jobs step by step
            List<Integer> stepList = jobInfo.getStepList();
            for (Integer step : stepList) {
                logger.debug("step {}:", step);
                context = jobInfo.createJobContext(this, step);
                if (logger.isDebugEnabled()) {
                    // TODO: context.toString()
                    logger.debug("\n{}", context);
                }

                Set<Worker> workers = context.getNeedNotifyWorkers();
                notifyWorkers(workers);

                long waitWorkTime = waitWorkersFinish(maxStepRuntime);

                long confirmTime = context.getMaxBlockingConfirmTime();
                if (confirmTime > waitWorkTime) {
                    long sleepTime = confirmTime - waitWorkTime;
                    logger.info("sleep {}ms to confirm block methods", sleepTime);
                    SchTool.sleepSilence(sleepTime);
                }
            }
        } catch (Exception e) {
            notifyAllWorkerQuit();
            logger.error("run schedule failed", e);
            throw e;
        } finally {
            logger.info("shutting down all threads");
            service.shutdown();
            SchTool.sleepSilence(100);
            while (!service.isTerminated()) {
                SchTool.sleepSilence(2000);
                logger.warn("wait all workers quit");
            }

            logger.info("all threads are shutted down");
        }
    }

    private void notifyAllWorkerQuit() {
        Set<Worker> workers = jobInfo.getWorkers();
        for (Worker w : workers) {
            w.stopRunning();
        }

        notifyWorkers(workers);
    }

    private long waitWorkersFinish(long maxTime) throws SchException {
        long actualWaitTime = 0;
        long waitCount = maxTime / PERIOD_WAIT_TIME;
        if (waitCount <= 0) {
            waitCount = 1;
        }

        try {
            for (int i = 0; i < waitCount; i++) {
                waitSchedule(PERIOD_WAIT_TIME);

                if (context.checkJobsFinished()) {
                    return actualWaitTime;
                }

                actualWaitTime += PERIOD_WAIT_TIME;
            }
        } catch (SchException e) {
            throw e;
        } catch (Exception e) {
            throw new SchException("wait failed", e);
        }

        MethodInfo remainMethod = context.displayRemainMethod();
        if (null != remainMethod) {
            throw new SchException(
                    "wait worker timeout in step " + context.getStep() + ",method=" + remainMethod);
        }

        return actualWaitTime;
    }

    private void notifyWorkers(Set<Worker> workers) {
        for (Worker w : workers) {
            notifyWorker(w);
        }
    }

    private void notifyWorker(Worker w) {
        synchronized (w) {
            w.notify();
        }
    }

    void notifySchedule() {
        synchronized (this) {
            this.notify();
        }
    }

    private void waitSchedule(long time) throws InterruptedException {
        synchronized (this) {
            this.wait(time);
        }
    }

    void startJob(int step, MethodInfo m) throws SchException {
        context.startJob(step, m);
    }

    void finishJob(int step, MethodInfo m) throws SchException {
        context.finishJob(step, m);
    }

    void saveErrInfo(int step, MethodInfo method, Exception exception) {
        context.saveErrInfo(step, method, exception);
    }

    int getStep() {
        if (null == context) {
            return -1;
        }

        return context.getStep();
    }
}
