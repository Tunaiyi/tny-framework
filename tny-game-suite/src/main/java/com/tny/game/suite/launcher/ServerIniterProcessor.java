package com.tny.game.suite.launcher;

import com.tny.game.LogUtils;
import com.tny.game.net.initer.ServerIniter;
import com.tny.game.net.initer.ServerPostStart;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.suite.launcher.exception.ServerInitException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Kun Yang on 16/5/31.
 */
public class ServerIniterProcessor implements ApplicationContextAware {

    private ApplicationContext context;

    private static Logger LOG = LoggerFactory.getLogger(ServerLauncher.class);

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public void initPreStart() throws Throwable {
        this.processIniter(ServerPreStart.class);
    }

    public void initPostStart() throws Throwable {
        this.processIniter(ServerPostStart.class);
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
}
