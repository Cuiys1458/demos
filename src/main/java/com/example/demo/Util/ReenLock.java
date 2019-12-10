package com.example.demo.Util;

import java.util.concurrent.locks.ReentrantLock;

public class ReenLock implements Runnable {
    private ReentrantLock lock = new ReentrantLock(true);

    @Override
    public void run() {
        lock.lock();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
