/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.boot.launcher;

import com.tny.game.boot.exception.*;
import com.tny.game.boot.transaction.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/28 3:26 上午
 */
public class ApplicationLauncherLifecycle implements SmartLifecycle, ApplicationContextAware {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLauncherLifecycle.class);

    private ApplicationContext context;

    private volatile boolean running;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void start() {
        TransactionManager.open();
        try {
            LOGGER.info("ApplicationLifecycleProcessor.prepareStart...");
            ApplicationLauncherContext.prepareStart(this.context);
            LOGGER.info("ApplicationLifecycleProcessor.prepareStart finish");
            List<ApplicationLauncher> applications = new ArrayList<>(this.context.getBeansOfType(
                    ApplicationLauncher.class).values());
            for (ApplicationLauncher application : applications) {
                try {
                    application.start();
                } catch (Exception exception) {
                    throw new BootApplicationException(exception);
                }
            }
            LOGGER.info("ApplicationLifecycleProcessor.postStart...");
            ApplicationLauncherContext.postStart(this.context);
            LOGGER.info("ApplicationLifecycleProcessor.postStart finish");
            this.running = true;
        } catch (Throwable e) {
            TransactionManager.rollback(e);
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    @Override
    public void stop() {
        TransactionManager.open();
        try {
            LOGGER.info("ApplicationLifecycleProcessor.close...");
            List<ApplicationLauncher> applications = new ArrayList<>(this.context.getBeansOfType(
                    ApplicationLauncher.class).values());
            for (ApplicationLauncher application : applications) {
                try {
                    application.stop();
                } catch (Exception exception) {
                    throw new BootApplicationException(exception);
                }
            }
            ApplicationLauncherContext.close();
            LOGGER.info("ApplicationLifecycleProcessor.close finish");
            this.running = false;
        } catch (Throwable e) {
            TransactionManager.rollback(e);
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }

}
