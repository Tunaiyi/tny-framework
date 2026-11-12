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

package com.tny.game.actor.local;

import org.slf4j.*;

/**
 * Deliver对象,负责处理消息.
 *
 * @author KGTny
 */
public class ProxyActorLifeCycle implements ActorLifeCycle {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProxyActorLifeCycle.class);

    private ActorLifeCycle lifeCycle;

    public ProxyActorLifeCycle(ActorLifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    @Override
    public void preBindWorker() {
        try {
            lifeCycle.preBindWorker();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postBindWorker() {
        try {
            lifeCycle.postBindWorker();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void preUnbindWorker() {
        try {
            lifeCycle.preUnbindWorker();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postUnbindWorker() {
        try {
            lifeCycle.postUnbindWorker();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void preTerminate() {
        try {
            lifeCycle.preTerminate();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postTerminate() {
        try {
            lifeCycle.postTerminate();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void preHandle(ActorCommand<?> command) {
        try {
            lifeCycle.preHandle(command);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postHandle(ActorCommand<?> command) {
        try {
            lifeCycle.postHandle(command);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postSucc(Object result) {
        try {
            lifeCycle.postSucc(result);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postFail(Throwable cause) {
        try {
            lifeCycle.postFail(cause);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

}