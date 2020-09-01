package com.tny.game.suite.lifecycle;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.loader.lifecycle.*;
import com.tny.game.net.base.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.*;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Kun Yang on 16/5/31.
 */
@WebListener
public class SpringApplicationLifecycleListener
        implements ApplicationListener<ContextRefreshedEvent>, ServletContextListener, ApplicationContextAware {

    private static final AppLifecycleProcessor PROCESSOR = new AppLifecycleProcessor();

    private ApplicationContext context;

    @Resource
    private AppContext appContext;

    private final AtomicBoolean contextInit = new AtomicBoolean(false);

    @Override
    public void setApplicationContext(@Nullable ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
        if (this.contextInit.compareAndSet(false, true)) {
            AppLifecycleProcessor.loadHandler(this.context);
            // processor.setApplicationContext(this.appContext);
            try {
                PROCESSOR.onPrepareStart(true);
                PROCESSOR.onPostStart(true);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ExeAide.runUnchecked(() -> PROCESSOR.onStaticInit(this.appContext.getScanPackages()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ExeAide.runUnchecked(() -> PROCESSOR.onClosed(true));
    }

}
