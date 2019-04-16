package pers.linyoubin.tools.threadscheduler.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchTool {
    private static final Logger logger = LoggerFactory.getLogger(SchTool.class);

    public static void sleepSilence(long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            logger.warn("sleep {} failed", ms, e);
        }
    }
}
