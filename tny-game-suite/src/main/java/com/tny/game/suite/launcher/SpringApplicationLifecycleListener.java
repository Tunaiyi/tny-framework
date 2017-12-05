package com.tny.game.suite.launcher;

import com.tny.game.suite.base.ExeAide;
import com.tny.game.suite.SuiteProfiles;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Kun Yang on 16/5/31.
 */
@Component
@Profile(SuiteProfiles.WEB)
public class SpringApplicationLifecycleListener implements ApplicationListener<ContextRefreshedEvent>, ServletContextListener, ApplicationContextAware {

    private static ApplicationLifecycleProcessor processor = new ApplicationLifecycleProcessor();

    private ApplicationContext context;

    private AtomicBoolean contextInit = new AtomicBoolean(false);

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (contextInit.compareAndSet(false, true)) {
            ApplicationLifecycleProcessor.loadHandler(context);
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
        ExeAide.runUnchecked(() -> processor.onStaticInit());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ExeAide.runUnchecked(() -> processor.onClosed(true));
    }

}
