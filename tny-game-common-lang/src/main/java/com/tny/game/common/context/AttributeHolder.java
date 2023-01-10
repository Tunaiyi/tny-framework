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

/**
 * @author KGTny
 * @ClassName: Attributes
 * @Description: 属性对象接口
 * @date 2011-9-21 ����10:48:40
 * <p>
 * 属性对象接口
 * <p>
 * <br>
 */
public class AttributeHolder {

    private volatile transient Attributes attributes;

    private final Object holderLock = new Object();

    public Attributes attributes() {
        if (this.attributes != null) {
            return this.attributes;
        }
        synchronized (this.holderLock) {
            if (this.attributes != null) {
                return this.attributes;
            }
            return this.attributes = ContextAttributes.create();
        }
    }

}
