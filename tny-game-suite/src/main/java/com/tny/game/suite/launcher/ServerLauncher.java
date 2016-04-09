package com.tny.game.suite.launcher;

import com.tny.game.LogUtils;
import com.tny.game.net.NetServer;
import com.tny.game.net.base.listener.SessionListener;
import com.tny.game.net.dispatcher.SessionHolder;
import com.tny.game.net.initer.ServerIniter;
import com.tny.game.net.initer.ServerPostStart;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.net.listener.ServerClosedListener;
import com.tny.game.suite.initer.ProtoExSchemaIniter;
import com.tny.game.suite.launcher.exception.ServerInitException;
import com.tny.game.suite.utils.Configs;
import com.tny.game.telnet.TelnetServer;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private static Logger LOG = LoggerFactory.getLogger(ServerLauncher.class);


    public ServerLauncher(String... contextFile) {
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
        //per initServer
        runPoint(beforeInitServer);
        NetServer server = this.initServer();
        runPoint(afterInitServer);
        //post initServer

        this.processIniter(ServerPreStart.class);
        LOG.info("服务器启动服务器!");

        // per start
        runPoint(beforeStartServer);
        server.start();
        runPoint(afterStartServer);
        // post start

        if (this.context.getBeanNamesForType(TelnetServer.class).length > 0) {
            TelnetServer telnetServer = this.context.getBean(TelnetServer.class);
            if (telnetServer != null)
                telnetServer.start();
        }
        ProtoExSchemaIniter schemaIniter = this.context.getBean(ProtoExSchemaIniter.class);
        if (!schemaIniter.waitInitialized())
            throw schemaIniter.getException();
        this.processIniter(ServerPostStart.class);

        // complete
        runPoint(complete);
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

    private NetServer initServer() {
        NetServer server = this.context.getBean(NetServer.class);
        server.addServerClosedListeners(this.context.getBeansOfType(ServerClosedListener.class).values());
        SessionHolder sessionHolder = this.context.getBean(SessionHolder.class);
        sessionHolder.addSessionListener(this.context.getBeansOfType(SessionListener.class).values());
        LOG.info("服务器实例化完成!");
        return server;
    }

    private void processIniter(Class<? extends ServerIniter> initerClass) throws Throwable {
        String[] names = StringUtils.split(initerClass.getName(), ".");
        String name = names[names.length - 1];
        LOG.info("初始化服务器启动 {} 初始化器......", name);
        Map<String, ? extends ServerIniter> initerMap = this.context.getBeansOfType(initerClass);
        List<ServerIniter> initers = new ArrayList<>();
        initers.addAll(initerMap.values());
        Collections.sort(initers, (o1, o2) -> 0 - (o1.getInitLevel().priority - o2.getInitLevel().priority));
        for (ServerIniter initer : initers) {
            long current = System.currentTimeMillis();
            LOG.info("初始化 服务器启动 {} 初始化器 : {}", name, initer.getClass());
            initer.initialize();
            LOG.info("初始化 服务器启动 {} 初始化器 : 耗时 {} -> {} 完成", name, System.currentTimeMillis() - current, initer.getClass());
        }
        for (ServerIniter initer : initers) {
            if (!initer.waitInitialized()) {
                throw new ServerInitException(LogUtils.format("{} 初始化失败", initer.getClass()));
            }
        }
        LOG.info("初始化服务器启动 {} 初始化器!", name);
    }

    private void runPoint(Consumer<ApplicationContext> fn) {
        if (fn != null)
            fn.accept(this.context);
    }

}
