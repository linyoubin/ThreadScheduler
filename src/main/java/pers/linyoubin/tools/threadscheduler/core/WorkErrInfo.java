package pers.linyoubin.tools.threadscheduler.core;

public class WorkErrInfo {
    private int step;
    private MethodInfo method;
    private Exception exception;

    public WorkErrInfo(int step, MethodInfo method, Exception exception) {
        this.step = step;
        this.method = method;
        this.exception = exception;
    }

    public int getStep() {
        return step;
    }

    public MethodInfo getMethod() {
        return method;
    }

    public Exception getException() {
        return exception;
    }
}
