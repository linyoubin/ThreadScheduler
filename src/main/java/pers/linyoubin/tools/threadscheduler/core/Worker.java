package pers.linyoubin.tools.threadscheduler.core;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.linyoubin.tools.threadscheduler.exception.SchException;

class Worker extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private ThreadScheduler ts;
    private Object o;
    private ObjectInfo oInfo;
    private boolean runningFlag = true;
    private int step;
    private MethodInfo method;

    public Worker(ThreadScheduler ts, Object o, ObjectInfo oInfo) {
        this.ts = ts;
        this.o = o;
        this.oInfo = oInfo;
    }

    public Object getObject() {
        return o;
    }

    public ObjectInfo getObjectInfo() {
        return oInfo;
    }

    public void stopRunning() {
        runningFlag = false;
    }

    private void innerRun() throws SchException {
        for (int i = 0; i < oInfo.getMothList().size(); i++) {
            method = oInfo.getMothList().get(i);
            step = method.getStep();
            waitForMyStep(step);

            if (!runningFlag) {
                logger.warn("stop while running flag is false");
                break;
            }

            ts.startJob(method.getStep(), method);
            executeMethod(o, method.getMethod());
            if (!method.isExpectBlock()) {
                ts.finishJob(method.getStep(), method);
            }
            else {
                ts.finishJob(method.getContStep(), method);
            }
        }
    }

    private void executeMethod(Object o, Method m) throws SchException {
        if (null == m) {
            return;
        }

        try {
            m.setAccessible(true);
            m.invoke(o);
        }
        catch (Exception e) {
            throw new SchException("invoke " + o + "." + m.getName() + " failed", e);
        }
    }

    private void waitForMyStep(int step) throws SchException {
        try {
            synchronized (this) {
                if (ts.getStep() == step) {
                    return;
                }

                if (!runningFlag) {
                    return;
                }

                this.wait();

                if (!runningFlag) {
                    return;
                }
            }

            if (ts.getStep() != step) {
                throw new SchException("step is mismatch:schedule's step:" + ts.getStep()
                        + ",myStep:" + step);
            }
        }
        catch (Exception e) {
            throw new SchException("wait for step failed:step=" + step, e);
        }
    }

    @Override
    public void run() {
        try {
            innerRun();
        }
        catch (Exception e) {
            logger.error("execute worker failed:worker=\n{}", this, e);
            ts.saveErrInfo(step, method, e);
        }
    }
}
