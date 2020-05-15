package com.tny.game.starter.net.netty4.appliaction;

import com.tny.game.common.runtime.*;
import com.tny.game.loader.lifecycle.*;
import com.tny.game.net.base.*;
import com.tny.game.net.telnet.*;
import com.tny.game.starter.common.transaction.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class NetApplication implements InitializingBean {

    private ApplicationContext applicationContext;

    private AppContext appContext;

    private Consumer<ApplicationContext> beforeInitServer;

    private Consumer<ApplicationContext> afterInitServer;

    private Consumer<ApplicationContext> beforeStartServer;

    private Consumer<ApplicationContext> afterStartServer;

    private Consumer<ApplicationContext> complete;

    private AppLifecycleProcessor processor = new AppLifecycleProcessor();

    private static Logger LOG = LoggerFactory.getLogger(NetApplication.class);

    private Instant startAt;

    public NetApplication(ApplicationContext applicationContext, AppContext appContext) {
        LOG.info("开始启动服务加载配置");
        this.applicationContext = applicationContext;
        this.appContext = appContext;
    }


    public NetApplication start() throws Throwable {
        TransactionManager.open();
        try {
            // processor.setApplicationContext(this.appContext);
            //per initServer
            runPoint(this.beforeInitServer);
            Collection<ServerGuide> servers = this.initApplication();
            AppLifecycleProcessor.loadHandler(this.applicationContext);
            runPoint(this.afterInitServer);
            //post initServer

            this.processor.onPrepareStart(false);
            LOG.info("服务器启动服务器!");

            // per start
            runPoint(this.beforeStartServer);
            servers.forEach(ServerGuide::open);
            runPoint(this.afterStartServer);
            // post start

            // if (this.applicationContext.getBeanNamesForType(TelnetServer.class).length > 0) {
            //     TelnetServer telnetServer = this.applicationContext.getBean(TelnetServer.class);
            //     if (telnetServer != null)
            //         telnetServer.start();
            // }
            this.processor.onPostStart(false);
            ShutdownHook.register(() -> servers.forEach(guide -> {
                TransactionManager.open();
                try {
                    guide.close();
                } catch (Throwable throwable) {
                    LOG.error("ShutdownHook handle close", throwable);
                } finally {
                    try {
                        this.processor.onClosed(true);
                    } catch (Throwable throwable) {
                        LOG.error("ShutdownHook handle close", throwable);
                    }
                    TransactionManager.close();
                }
            }));

            // complete
            runPoint(this.complete);
            LOG.info("服务启动完成!! | 耗时 {}", System.currentTimeMillis() - this.startAt.toEpochMilli());
        } finally {
            TransactionManager.close();
        }
        return this;
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
                    if (command != null && command.toLowerCase().equals("q"))
                        System.exit(0);
                    String[] commands = StringUtils.split(command, " ");
                    if (commands == null)
                        break;
                    if (commandHolder != null && commands.length >= 1) {
                        LOG.info(commandHolder.execute(commands));
                    } else {
                        if (handler != null)
                            handler.accept(command, commands);
                        else
                            LOG.warn("命令无效!!");
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
        if (fn != null)
            fn.accept(this.applicationContext);
    }

    @Override
    public void afterPropertiesSet() {
        this.startAt = Instant.now();
        this.processor.onStaticInit(this.appContext.getScanPackages());
    }
}