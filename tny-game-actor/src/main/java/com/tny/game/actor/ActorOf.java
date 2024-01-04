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
