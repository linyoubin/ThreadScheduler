package pers.linyoubin.tools.threadscheduler.core;

import java.lang.reflect.Method;

class StepInfo {
    private int step;
    private String desc;

    public StepInfo(int step, String desc) {
        this.step = step;
        this.desc = desc;
    }

    public int getStep() {
        return step;
    }

    public String getDesc() {
        return desc;
    }
}

class BlockInfo {
    private int contStep;
    private boolean isExpectBlock = false;
    private int blockConfirmTime;

    public BlockInfo(int blockConfirmTime, int contStep, boolean isExpectBlock) {
        this.blockConfirmTime = blockConfirmTime;
        this.contStep = contStep;
        this.isExpectBlock = isExpectBlock;
    }

    public int getContStep() {
        return contStep;
    }

    public boolean isExpectBlock() {
        return isExpectBlock;
    }

    public int getBlockConfirmTime() {
        return blockConfirmTime;
    }
}

class MethodInfo {
    private Object o;
    private Method m;

    private StepInfo stepInfo;
    private BlockInfo blockInfo;

    public MethodInfo(Object o, Method m, int step, String desc) {
        this.o = o;
        this.m = m;
        this.stepInfo = new StepInfo(step, desc);
    }

    void enableExpectBlock(int conformTime, int contStep) {
        blockInfo = new BlockInfo(conformTime, contStep, true);
    }

    void disableExpectBlock() {
        blockInfo = null;
    }

    public boolean isExpectBlock() {
        if (null != blockInfo) {
            return blockInfo.isExpectBlock();
        }

        return false;
    }

    public int getConfirmTime() {
        return blockInfo.getBlockConfirmTime();
    }

    public int getContStep() {
        return blockInfo.getContStep();
    }

    public Object getObject() {
        return o;
    }

    public Method getMethod() {
        return m;
    }

    public int getStep() {
        return stepInfo.getStep();
    }

    public String getDesc() {
        return stepInfo.getDesc();
    }

    @Override
    public int hashCode() {
        return o.hashCode() + stepInfo.getStep();
    }

    @Override
    public boolean equals(Object r) {
        if (null == r || !(r instanceof MethodInfo)) {
            return false;
        }

        MethodInfo right = (MethodInfo) r;
        return this.o == right.o && this.m == right.m
                && this.stepInfo.getStep() == this.stepInfo.getStep();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Object:").append(o).append(",");
        sb.append("Method:").append(m).append(",");
        sb.append("Step:").append(stepInfo.getStep()).append(",");
        sb.append("Desc:").append(stepInfo.getDesc());

        if (isExpectBlock()) {
            sb.append(",");
            sb.append("BlockConfirmTime:").append(blockInfo.getBlockConfirmTime()).append(",");
            sb.append("ContStep:").append(blockInfo.getContStep()).append(",");
        }

        return sb.toString();
    }

    public String simpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getSimpleName()).append(".").append(m.getName());
        sb.append(",").append(stepInfo.getDesc());

        if (isExpectBlock()) {
            sb.append(",");
            sb.append("BlockConfirmTime:").append(blockInfo.getBlockConfirmTime()).append(",");
            sb.append("ContStep:").append(blockInfo.getContStep()).append(",");
        }

        return sb.toString();
    }

}
