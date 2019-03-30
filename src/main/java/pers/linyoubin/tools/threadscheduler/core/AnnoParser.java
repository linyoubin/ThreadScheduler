package pers.linyoubin.tools.threadscheduler.core;

import java.lang.reflect.Method;

import pers.linyoubin.tools.threadscheduler.annotation.ExecuteOrder;
import pers.linyoubin.tools.threadscheduler.annotation.ExpectBlock;
import pers.linyoubin.tools.threadscheduler.exception.SchException;

class AnnoParser {
    public static ObjectInfo parse(Object o) throws SchException {
        ObjectInfo info = new ObjectInfo();
        Class<?> c = o.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(ExecuteOrder.class)) {
                ExecuteOrder order = m.getAnnotation(ExecuteOrder.class);
                if (order.step() == 0) {
                    throw new SchException("step 0 is occupied by scheduler£º" + c + "." + m);
                }

                MethodInfo methodInfo = new MethodInfo(o, m, order.step(), order.desc());

                if (m.isAnnotationPresent(ExpectBlock.class)) {
                    ExpectBlock eb = m.getAnnotation(ExpectBlock.class);
                    methodInfo.enableExpectBlock(eb.conformTime(), eb.contOnStep());
                }

                info.addMethod(methodInfo);
            }
        }

        info.sortByStep();

        return info;
    }
}
