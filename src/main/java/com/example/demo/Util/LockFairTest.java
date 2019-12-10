package com.example.demo.Util;


import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class LockFairTest implements Runnable {
    //创建公平锁
    private static ReentrantLock lock = new ReentrantLock(true);

    public void run() {

        try {
            lock.lock();
            for (int i = 1; i <= 10000; i++) {
                System.out.println(Thread.currentThread().getName() + "获得锁" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
       /* while (true) {
            lock.lock();
            try {

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }*/
    }

    public static void main(String[] args) throws Exception {
        double response = Util.getAngle( "172.16.1.52", "admin", "admin12345");
        System.out.println(response);
    }

    
}