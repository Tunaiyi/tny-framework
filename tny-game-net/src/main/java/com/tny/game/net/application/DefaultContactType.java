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

package com.tny.game.net.application;

/**
 * 默认用户类型
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/6 15:18
 **/
public enum DefaultContactType implements ContactType {

    /**
     * 匿名
     */
    ANONYMITY(0, ANONYMITY_USER_TYPE),

    /**
     * 默认用户
     */
    DEFAULT_USER(1, DEFAULT_USER_TYPE),
    //

    ;

    private final int id;

    private final String group;

    DefaultContactType(int id, String group) {
        this.id = id;
        this.group = group;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String getGroup() {
        return group;
    }
}
