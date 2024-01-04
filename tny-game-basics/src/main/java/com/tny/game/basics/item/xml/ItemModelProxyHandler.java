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

package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;
import net.sf.cglib.proxy.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.*;

/**
 * 事物模型代理对象
 *
 * @author KGTny
 */
public class ItemModelProxyHandler implements MethodInterceptor, InvocationHandler {

    public volatile ItemModel itemModel;

    public ItemModelProxyHandler(ItemModel itemModel) {
        super();
        this.itemModel = itemModel;
    }

    public int getModelId() {
        return this.itemModel.getId();
    }

    public void setModel(ItemModel itemModel) {
        this.itemModel = itemModel;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(itemModel, args);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxy.invoke(itemModel, args);
    }

}
