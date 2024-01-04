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

package com.tny.game.common.context;

public class ContextAttributes extends AbstractAttributes {

    private static final Attributes EMPTY_ONE = new EmptyAttributes();

    public static Attributes empty() {
        return EMPTY_ONE;
    }

    public static Attributes create() {
        return new ContextAttributes();
    }

    public static Attributes create(AttrEntry<?>... entries) {
        return new ContextAttributes(entries);
    }

    private ContextAttributes(AttrEntry<?>... entries) {
        super(false);
        this.setAttribute(entries);
    }

}
