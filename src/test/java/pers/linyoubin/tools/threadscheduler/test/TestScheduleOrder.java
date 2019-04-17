package pers.linyoubin.tools.threadscheduler.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pers.linyoubin.tools.threadscheduler.annotation.ExecuteOrder;
import pers.linyoubin.tools.threadscheduler.core.ThreadScheduler;
import pers.linyoubin.tools.threadscheduler.exception.SchException;

class printJob {
    private SchTestData data;

    public printJob(SchTestData data) {
        this.data = data;
    }

    @ExecuteOrder(step = 1)
    public void step1() {
        data.putData(1);
    }

    @ExecuteOrder(step = 3)
    public void step3() {
        data.putData(3);
    }
}

class printJob2 {
    private SchTestData data;

    public printJob2(SchTestData data) {
        this.data = data;
    }

    @ExecuteOrder(step = 2)
    public void step2() {
        data.putData(2);
    }

    @ExecuteOrder(step = 4)
    public void step4() {
        data.putData(4);
    }
}

public class TestScheduleOrder {
    private List<Integer> expectDataList = new ArrayList<Integer>();

    @BeforeClass
    public void setUp() {
        expectDataList.add(1);
        expectDataList.add(2);
        expectDataList.add(3);
        expectDataList.add(4);
    }

    @AfterClass
    public void tearDown() {

    }

    @Test
    public void test() throws SchException {
        ThreadScheduler ts = new ThreadScheduler();

        SchTestData data = new SchTestData();
        ts.addJob(new printJob(data));
        ts.addJob(new printJob2(data));
        ts.run();

        List<Integer> dataList = data.getDataList();

        Assert.assertTrue(TestTools.compare(dataList, expectDataList));
    }
}
