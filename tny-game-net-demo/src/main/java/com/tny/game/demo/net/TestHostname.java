/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
