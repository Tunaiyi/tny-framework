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
