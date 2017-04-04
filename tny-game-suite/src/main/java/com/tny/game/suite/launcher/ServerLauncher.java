package com.tny.game.suite.launcher;

import com.tny.game.net.listener.SeverClosedListener;
import com.tny.game.net.netty.NettyServer;
import com.tny.game.net.session.holder.SessionHolder;
import com.tny.game.net.session.holder.listener.SessionHolderListener;
import com.tny.game.suite.transaction.TransactionManager;
import com.tny.game.suite.utils.Configs;
import com.tny.game.telnet.command.TelnetCommandHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class ServerLauncher {

    private ConfigurableApplicationContext context;

    private Consumer<ApplicationContext> beforeInitServer;

    private Consumer<ApplicationContext> afterInitServer;

    private Consumer<ApplicationContext> beforeStartServer;

    private Consumer<ApplicationContext> afterStartServer;

    private Consumer<ApplicationContext> complete;

    private ApplicationLifecycleProcessor processor = new ApplicationLifecycleProcessor();

    private static Logger LOG = LoggerFactory.getLogger(ServerLauncher.class);

    public ServerLauncher(String... contextFile) throws Throwable {
        this.processor.onStaticInit();
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        String profiles = Configs.SUITE_CONFIG.getStr(Configs.SUITE_LAUNCHER_PROFILES);
        context.getEnvironment().setActiveProfiles(StringUtils.split(profiles, ","));
        LOG.info("服务器启动配置Profiles : {}", profiles);
        context.load(contextFile);
        init(context);
    }

    private void init(ConfigurableApplicationContext context) {
        this.context = context;
        this.context.refresh();
    }

    public ServerLauncher(ConfigurableApplicationContext context) {
        init(context);
    }

    public ServerLauncher start() throws Throwable {
        TransactionManager.open();
        try {
            // processor.setApplicationContext(this.appContext);
            //per initServer
            runPoint(beforeInitServer);
            NettyServer server = this.initServer();
            ApplicationLifecycleProcessor.loadHandler(context);
            runPoint(afterInitServer);
            //post initServer

            processor.onPrepareStart(false);
            LOG.info("服务器启动服务器!");

            // per start
            runPoint(beforeStartServer);
            server.open();
            runPoint(afterStartServer);
            // post start

            // if (this.context.getBeanNamesForType(TelnetServer.class).length > 0) {
            //     TelnetServer telnetServer = this.context.getBean(TelnetServer.class);
            //     if (telnetServer != null)
            //         telnetServer.start();
            // }
            processor.onPostStart(false);

            Runtime.getRuntime().addShutdownHook(new Thread() {

                {
                    this.setName("ApplicationLifecycleShutdownHook");
                }

                @Override
                public void run() {
                    TransactionManager.open();
                    try {
                        server.close();
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
                }

            });
            // complete
            runPoint(complete);
        } finally {
            TransactionManager.close();
        }
        return this;
    }

    public ServerLauncher afterInitServer(Consumer<ApplicationContext> afterInitServer) {
        this.afterInitServer = afterInitServer;
        return this;
    }

    public ServerLauncher afterStartServer(Consumer<ApplicationContext> afterStartServer) {
        this.afterStartServer = afterStartServer;
        return this;
    }

    public ServerLauncher beforeInitServer(Consumer<ApplicationContext> beforeInitServer) {
        this.beforeInitServer = beforeInitServer;
        return this;
    }

    public ServerLauncher beforeStartServer(Consumer<ApplicationContext> beforeStartServer) {
        this.beforeStartServer = beforeStartServer;
        return this;
    }

    public ServerLauncher complete(Consumer<ApplicationContext> complete) {
        this.complete = complete;
        return this;
    }

    public void waitForConsole(String closeKey) {
        Map<String, TelnetCommandHolder> map = this.context.getBeansOfType(TelnetCommandHolder.class);
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

    private NettyServer initServer() {
        NettyServer server = this.context.getBean(NettyServer.class);
        server.addClosedListeners(this.context.getBeansOfType(SeverClosedListener.class).values());
        SessionHolder sessionHolder = this.context.getBean(SessionHolder.class);
        sessionHolder.addListener(this.context.getBeansOfType(SessionHolderListener.class).values());
        LOG.info("服务器实例化完成!");
        return server;
    }

    private void runPoint(Consumer<ApplicationContext> fn) {
        if (fn != null)
            fn.accept(this.context);
    }

}
