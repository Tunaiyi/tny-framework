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

package com.tny.game.actor;

public interface ActorOf<ID, AC extends Actor<ID, ?>> {

    /**
     * 构建ActorRef
     *
     * @param path actor路径
     * @param id   actor名字
     * @return 返回ActorRef
     */
    AC actorOf(ActorURL path, ID id);

    /**
     * 构建ActorRef
     *
     * @param id actor名字
     * @return 返回ActorRef
     */
    AC actorOf(ID id);

    /**
     * 停止关闭指定的ActorRet
     *
     * @param actor 关闭的Actor
     */
    void stop(Actor<?, ?> actor);

}
