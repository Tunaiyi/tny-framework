/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.configuration.application;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.base.*;
import com.tny.game.net.telnet.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class NetApplication {

    private final ApplicationContext applicationContext;

    private final NetAppContext appContext;

    private Consumer<ApplicationContext> beforeInitServer;

    private Consumer<ApplicationContext> afterInitServer;

    private Consumer<ApplicationContext> beforeStartServer;

    private Consumer<ApplicationContext> afterStartServer;

    private Consumer<ApplicationContext> complete;

    private Collection<ServerGuide> servers = ImmutableList.of();

    //    private final AppLifecycleProcessor processor = new AppLifecycleProcessor();

    private static Logger LOG = LoggerFactory.getLogger(NetApplication.class);

    private Instant startAt;

    public NetApplication(ApplicationContext applicationContext, NetAppContext appContext) {
        LOG.info("开始启动服务加载配置");
        this.applicationContext = applicationContext;
        this.appContext = appContext;
    }

    public NetApplication start() throws Throwable {
        // processor.setApplicationContext(this.appContext);
        //per initServer
        this.startAt = Instant.now();
        runPoint(this.beforeInitServer);
        this.servers = this.initApplication();
        //            ApplicationLifecycleProcessor.loadHandler(this.applicationContext);
        runPoint(this.afterInitServer);
        //post initServer

        //            this.processor.onPrepareStart(false);
        LOG.info("服务器启动服务器!");

        // per start
        runPoint(this.beforeStartServer);
        this.servers.forEach(ServerGuide::open);
        runPoint(this.afterStartServer);

        // if (this.applicationContext.getBeanNamesForType(TelnetServer.class).length > 0) {
        //     TelnetServer telnetServer = this.applicationContext.getBean(TelnetServer.class);
        //     if (telnetServer != null)
        //         telnetServer.start();
        // }
        //            this.processor.onPostStart(false);
        //            ShutdownHook.register(() -> this.servers.forEach(guide -> {
        //                TransactionManager.open();
        //                try {
        //                    guide.close();
        //                } catch (Throwable throwable) {
        //                    LOG.error("ShutdownHook handle close", throwable);
        //                } finally {
        //                    TransactionManager.close();
        //                }
        //            }));
        // complete
        runPoint(this.complete);
        LOG.info("服务启动完成!! | 耗时 {}", System.currentTimeMillis() - this.startAt.toEpochMilli());
        return this;
    }

    public NetAppContext getAppContext() {
        return this.appContext;
    }

    public Instant getStartAt() {
        return this.startAt;
    }

    public void close() {
        this.servers.forEach(guide -> {
            try {
                guide.close();
            } catch (Throwable throwable) {
                LOG.error("ShutdownHook handle close", throwable);
            }
        });
    }

    public NetApplication afterInitServer(Consumer<ApplicationContext> afterInitServer) {
        this.afterInitServer = afterInitServer;
        return this;
    }

    public NetApplication afterStartServer(Consumer<ApplicationContext> afterStartServer) {
        this.afterStartServer = afterStartServer;
        return this;
    }

    public NetApplication beforeInitServer(Consumer<ApplicationContext> beforeInitServer) {
        this.beforeInitServer = beforeInitServer;
        return this;
    }

    public NetApplication beforeStartServer(Consumer<ApplicationContext> beforeStartServer) {
        this.beforeStartServer = beforeStartServer;
        return this;
    }

    public NetApplication complete(Consumer<ApplicationContext> complete) {
        this.complete = complete;
        return this;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void waitForConsole(String closeKey) {
        this.waitForConsole(closeKey, null);
    }

    public void waitForConsole(String closeKey, BiConsumer<String, String[]> handler) {
        Map<String, TelnetCommandHolder> map = this.applicationContext.getBeansOfType(TelnetCommandHolder.class);
        TelnetCommandHolder commandHolder = null;
        for (TelnetCommandHolder holder : map.values()) {
            commandHolder = holder;
            break;
        }
        try (InputStreamReader systemIn = new InputStreamReader(System.in);
             BufferedReader inputReader = new BufferedReader(systemIn)) {
            while (true) {
                LOG.warn("输入命令 ({} 关闭服务器) :", closeKey);
                String command = inputReader.readLine();
                try {
                    if (command != null && command.equalsIgnoreCase(closeKey)) {
                        System.exit(0);
                    }
                    String[] commands = StringUtils.split(command, " ");
                    if (commands == null) {
                        break;
                    }
                    if (commandHolder != null && commands.length >= 1) {
                        LOG.info(commandHolder.execute(commands));
                    } else {
                        if (handler != null) {
                            handler.accept(command, commands);
                        } else {
                            LOG.warn("命令无效!!");
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Collection<ServerGuide> initApplication() {
        Map<String, ServerGuide> servers = this.applicationContext.getBeansOfType(ServerGuide.class);
        LOG.info("服务器实例化完成!");
        return servers.values();
    }

    private void runPoint(Consumer<ApplicationContext> fn) {
        if (fn != null) {
            fn.accept(this.applicationContext);
        }
    }

}
