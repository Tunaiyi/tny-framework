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
 * 默认 app 范围
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/9 17:25
 **/
public enum DefaultAppScope implements AppScope {

    /**
     * 上线
     */
    ONLINE(1, "online"),

    /**
     * 开发
     */
    DEVELOP(2, "develop"),

    /**
     * 测试
     */
    TEST(3, "test"),

    ;

    private final int id;

    private final String scopeName;

    DefaultAppScope(int id, String scopeName) {
        this.id = id;
        this.scopeName = scopeName;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String getScopeName() {
        return scopeName;
    }

}
