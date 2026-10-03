/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
