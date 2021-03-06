# ThreadScheduler #

实现一个多线程调度框架，可以按照自定义的顺序控制多线程之间的调度。支持多线程在并发执行和串行执行之间的自由切换。

+ 样例代码
    
``` java
package pers.linyoubin.tools.threadscheduler.sample;

import pers.linyoubin.tools.threadscheduler.annotation.ExecuteOrder;
import pers.linyoubin.tools.threadscheduler.core.ThreadScheduler;
import pers.linyoubin.tools.threadscheduler.exception.SchException;

class Job1 {
    //使用注解ExecuteOrder定义好Job中函数的执行顺序
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
        ThreadScheduler ts = new ThreadScheduler();

        ts.addJob(new Job1());
        ts.addJob(new Job2());

        //执行job：严格按照step顺序执行，step相同的函数会并发执行
        ts.run();
    }
}

```

+ 输出结果

``` java
// step1 -> step4 严格按顺序执行
// 两个 job 的 step3 会并发执行，多次执行打印顺序会有变化
pers.linyoubin.tools.threadscheduler.sample.Job1@3215b9e0:step1
pers.linyoubin.tools.threadscheduler.sample.Job2@5a9e40d2:step2
pers.linyoubin.tools.threadscheduler.sample.Job2@5a9e40d2:step3
pers.linyoubin.tools.threadscheduler.sample.Job1@3215b9e0:step3
pers.linyoubin.tools.threadscheduler.sample.Job1@3215b9e0:step4
```