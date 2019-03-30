package pers.linyoubin.tools.threadscheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ExpectBlock {
    public boolean expectBlock() default true;

    // time(seconds) to confirm this step is blocking
    public int conformTime();

    // this blocking step will continue on the step(contOnStep())
    public int contOnStep();
}
