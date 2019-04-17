package pers.linyoubin.tools.threadscheduler.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pers.linyoubin.tools.threadscheduler.annotation.ExecuteOrder;
import pers.linyoubin.tools.threadscheduler.annotation.ExpectBlock;
import pers.linyoubin.tools.threadscheduler.core.ThreadScheduler;
import pers.linyoubin.tools.threadscheduler.exception.SchException;

class Block1 {
    private SchTestData data;

    public Block1(SchTestData data) {
        this.data = data;
    }

    @ExecuteOrder(step = 1)
    @ExpectBlock(conformTime = 2, contOnStep = 2)
    public void step1() throws InterruptedException {
        data.putData(1);
        data.block();
    }

    @ExecuteOrder(step = 3)
    public void step3() {
        data.putData(3);
    }
}

class Block2 {
    private SchTestData data;

    public Block2(SchTestData data) {
        this.data = data;
    }

    @ExecuteOrder(step = 2)
    public void step2() {
        data.putData(2);
        data.unBlock();
    }

    @ExecuteOrder(step = 4)
    public void step4() {
        data.putData(4);
    }
}

public class TestBlock {
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
        ts.addJob(new Block1(data));
        ts.addJob(new Block2(data));
        ts.run();

        List<Integer> dataList = data.getDataList();

        Assert.assertTrue(TestTools.compare(dataList, expectDataList));
    }
}
