package pers.linyoubin.tools.threadscheduler.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SchTestData {
    private Lock lock = new ReentrantLock();
    private List<Integer> dataList = new ArrayList<Integer>();

    public void putData(int data) {
        lock.lock();
        try {
            dataList.add(data);
        }
        finally {
            lock.unlock();
        }
    }

    // called after all threads have been finished
    public List<Integer> getDataList() {
        return dataList;
    }

    public void block() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

    public void unBlock() {
        synchronized (this) {
            this.notify();
        }
    }
}
