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
