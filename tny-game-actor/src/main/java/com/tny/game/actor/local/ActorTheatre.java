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

package com.tny.game.actor.local;

/**
 * 剧场
 * Created by Kun Yang on 16/4/28.
 */
public interface ActorTheatre {

    /**
     * @return 剧场名字
     */
    String getName();

    /**
     * 接管指定actor
     *
     * @param actor 托管的actor
     * @return 返回是否接管成功
     */
    boolean takeOver(LocalActor<?, ?> actor);

}
