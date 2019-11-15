package com.tny.game.suite.launcher;

import com.tny.game.common.runtime.*;
import com.tny.game.loader.lifecycle.*;
import com.tny.game.net.base.*;
import com.tny.game.net.telnet.*;
import com.tny.game.suite.transaction.*;
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
public class SuitApplication implements InitializingBean {

    private ApplicationContext applicationContext;

    private AppContext appContext;

    private Consumer<ApplicationContext> beforeInitServer;

    private Consumer<ApplicationContext> afterInitServer;

    private Consumer<ApplicationContext> beforeStartServer;

    private Consumer<ApplicationContext> afterStartServer;

    private Consumer<ApplicationContext> complete;

    private AppLifecycleProcessor processor = new AppLifecycleProcessor();

    private static Logger LOG = LoggerFactory.getLogger(SuitApplication.class);

    private Instant startAt;

    public SuitApplication(ApplicationContext applicationContext, AppContext appContext) {
        LOG.info("开始启动服务加载配置");
        this.applicationContext = applicationContext;
        this.appContext = appContext;
    }


    public SuitApplication start() throws Throwable {
        TransactionManager.open();
        try {
            // processor.setApplicationContext(this.appContext);
            //per initServer
            runPoint(beforeInitServer);
            Collection<ServerGuide> servers = this.initApplication();
            AppLifecycleProcessor.loadHandler(applicationContext);
            runPoint(afterInitServer);
            //post initServer

            processor.onPrepareStart(false);
            LOG.info("服务器启动服务器!");

            // per start
            runPoint(beforeStartServer);
            servers.forEach(ServerGuide::open);
            runPoint(afterStartServer);
            // post start

            // if (this.applicationContext.getBeanNamesForType(TelnetServer.class).length > 0) {
            //     TelnetServer telnetServer = this.applicationContext.getBean(TelnetServer.class);
            //     if (telnetServer != null)
            //         telnetServer.start();
            // }
            processor.onPostStart(false);
            ShutdownHook.register(() -> servers.forEach(guide -> {
                TransactionManager.open();
                try {
                    guide.close();
                } catch (Throwable throwable) {
                    LOG.error("ShutdownHook handle close", throwable);
                } finally {
                    try {
                        processor.onClosed(true);
                    } catch (Throwable throwable) {
                        LOG.error("ShutdownHook handle close", throwable);
                    }
                    TransactionManager.close();
                }
            }));

            // complete
            runPoint(complete);
            LOG.info("服务启动完成!! | 耗时 {}", System.currentTimeMillis() - startAt.toEpochMilli());
        } finally {
            TransactionManager.close();
        }
        return this;
    }

    public SuitApplication afterInitServer(Consumer<ApplicationContext> afterInitServer) {
        this.afterInitServer = afterInitServer;
        return this;
    }

    public SuitApplication afterStartServer(Consumer<ApplicationContext> afterStartServer) {
        this.afterStartServer = afterStartServer;
        return this;
    }

    public SuitApplication beforeInitServer(Consumer<ApplicationContext> beforeInitServer) {
        this.beforeInitServer = beforeInitServer;
        return this;
    }

    public SuitApplication beforeStartServer(Consumer<ApplicationContext> beforeStartServer) {
        this.beforeStartServer = beforeStartServer;
        return this;
    }

    public SuitApplication complete(Consumer<ApplicationContext> complete) {
        this.complete = complete;
        return this;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
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
                System.out.println("输入命令 (" + closeKey + " 关闭服务器) :");
                String command = inputReader.readLine();
                try {
                    if (command != null && command.toLowerCase().equals("q"))
                        System.exit(0);
                    String[] commands = StringUtils.split(command, " ");
                    if (commands == null)
                        break;
                    if (commandHolder != null && commands.length >= 1) {
                        System.out.println(commandHolder.execute(commands));
                    } else {
                        if (handler != null)
                            handler.accept(command, commands);
                        else
                            System.out.println("命令无效!!");
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
        this.processor.onStaticInit(appContext.getScanPackages());
    }
}
