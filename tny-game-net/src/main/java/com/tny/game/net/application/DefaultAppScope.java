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
