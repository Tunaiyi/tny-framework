package com.tny.game.net.demo;

import com.tny.game.common.runtime.*;

import java.net.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/15 2:16 上午
 */
public class TestHostname {

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        InetAddress.getByName("192.168.2.101");
        ProcessWatcher watcher = ProcessWatcher.getDefault();
        InetAddress address = null;
        for (int times = 0; times < 1000; times++) {
            for (int index = 0; index < 255; index++) {
                ProcessTracer tracer = watcher.trace();
                address = InetAddress.getByName("192.168.2." + index);
                tracer.done();
            }
        }
        System.out.println(address);
        watcher.statisticsLog();
        Thread.sleep(3000);
    }

}
