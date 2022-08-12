/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.demo.net;

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
