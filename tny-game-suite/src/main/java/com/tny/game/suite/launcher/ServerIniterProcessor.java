package com.tny.game.suite.launcher;

import com.tny.game.LogUtils;
import com.tny.game.net.initer.ServerInitialize;
import com.tny.game.net.initer.ServerPostStart;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.net.initer.StartIniter;
import com.tny.game.suite.launcher.exception.ServerInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private void processIniter(Class<? extends ServerInitialize> initerClass) throws Throwable {
        String name = initerClass.getSimpleName();
        LOG.info("服务器初始化 {} ! 初始化开始......", name);
        Map<String, ? extends ServerInitialize> initerMap = this.context.getBeansOfType(initerClass);
        Map<StartIniter, ServerInitialize> initializeMap = new HashMap<>();
        List<StartIniter> initers = initerMap.values().stream()
                .peek(i -> {
                    if (i.getClass() != i.getStartIniter().getIniterClass())
                        throw new IllegalArgumentException(LogUtils.format("{} 不符合 {}", i.getClass(), i.getStartIniter()));
                    initializeMap.put(i.getStartIniter(), i);
                })
                .map(ServerInitialize::getStartIniter)
                .sorted((o1, o2) -> 0 - (o1.getInitLevel().priority - o2.getInitLevel().priority))
                .collect(Collectors.toList());
        int index = 0;
        for (StartIniter initer : initers) {
            long current = System.currentTimeMillis();
            StartIniter currIniter = initer.head();
            while (currIniter != null) {
                ServerInitialize initialize = initializeMap.remove(currIniter);
                if (initialize != null) {
                    LOG.info("服务器初始化 {} 初始化器 [{}] : {}", name, index, currIniter);
                    initialize.initialize();
                    LOG.info("服务器初始化 {} 初始化器 [{}] : 耗时 {} -> {} 完成", name, index, System.currentTimeMillis() - current, currIniter);
                    index++;
                }
                currIniter = currIniter.getNext();
            }
        }
        for (ServerInitialize initer : initerMap.values()) {
            if (!initer.waitInitialized()) {
                throw new ServerInitException(LogUtils.format("{} 初始化失败", initer.getStartIniter()));
            }
        }
        LOG.info("服务器初始化 {} 完成! 共 {} 个初始化器!", name, index);
    }
}
