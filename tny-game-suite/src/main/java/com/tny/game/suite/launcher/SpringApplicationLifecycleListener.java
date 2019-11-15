package com.tny.game.suite.launcher;

import com.tny.game.common.utils.*;
import com.tny.game.loader.lifecycle.*;
import com.tny.game.net.base.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Kun Yang on 16/5/31.
 */
@WebListener
public class SpringApplicationLifecycleListener
        implements ApplicationListener<ContextRefreshedEvent>, ServletContextListener, ApplicationContextAware {

    private static AppLifecycleProcessor processor = new AppLifecycleProcessor();

    private ApplicationContext context;

    @Resource
    private AppContext appContext;

    private AtomicBoolean contextInit = new AtomicBoolean(false);

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (contextInit.compareAndSet(false, true)) {
            AppLifecycleProcessor.loadHandler(context);
            // processor.setApplicationContext(this.appContext);
            try {
                processor.onPrepareStart(true);
                processor.onPostStart(true);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ExeAide.runUnchecked(() -> processor.onStaticInit(appContext.getScanPackages()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ExeAide.runUnchecked(() -> processor.onClosed(true));
    }

}
