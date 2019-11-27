package pers.linyoubin.tools.threadscheduler.sample;

import pers.linyoubin.tools.threadscheduler.annotation.ExecuteOrder;
import pers.linyoubin.tools.threadscheduler.core.ThreadScheduler;
import pers.linyoubin.tools.threadscheduler.exception.SchException;

class Job1 {
    // 使用注解ExecuteOrder定义好Job中函数的执行顺序
    @ExecuteOrder(step = 1)
    public void step1() {
        System.out.println(this + ":step1");
    }

    @ExecuteOrder(step = 3)
    public void step3() {
        System.out.println(this + ":step3");
    }

    @ExecuteOrder(step = 4)
    public void step4() {
        System.out.println(this + ":step4");
    }
}

class Job2 {
    @ExecuteOrder(step = 2)
    public void step2() {
        System.out.println(this + ":step2");
    }

    @ExecuteOrder(step = 3)
    public void step3() {
        System.out.println(this + ":step3");
    }
}

public class ScheduleSample {
    public static void main(String[] args) throws SchException {
        for (int i = 0; i < 100; ++i) {
            ThreadScheduler ts = new ThreadScheduler();

            ts.addJob(new Job1());
            ts.addJob(new Job2());

            // 执行job：严格按照step顺序执行，step相同的函数会并发执行
            ts.run();
        }
    }
}
