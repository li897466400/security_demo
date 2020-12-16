package com.security.utils;

import sun.applet.Main;

/**
 * @author Li
 * @creat 2020-12-15-20:27
 */
public class Demo {

    public static void main(String[] args) {

        String s = " ss";
        for (int x =1; x< 6; x++){

            int finalX = x;
            new Thread(() -> {
                synchronized (s) {
                    Thread.currentThread().setName("线程"+ finalX);
                    try {
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(111);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

}

