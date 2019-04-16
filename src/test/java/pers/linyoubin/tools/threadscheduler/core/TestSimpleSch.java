package pers.linyoubin.tools.threadscheduler.core;

import pers.linyoubin.tools.threadscheduler.annotation.ExecuteOrder;
import pers.linyoubin.tools.threadscheduler.exception.SchException;

class printJob {
    @ExecuteOrder(step = 1)
    public void step1() {
        System.out.println(this + " step1");
    }

    public void step3() {
        System.out.println("step3");
    }
}

class printJob2 {
    @ExecuteOrder(step = 1)
    public void step1() {
        System.out.println(this + " step1");
    }

    @ExecuteOrder(step = 3)
    public void step3() {
        System.out.println(this + " step3");
    }
}

public class TestSimpleSch {
    public static void main(String[] args) throws SchException {
        ThreadScheduler ts = new ThreadScheduler();
        ts.addJob(new printJob());
        ts.addJob(new printJob2());
        ts.run();
    }
}
