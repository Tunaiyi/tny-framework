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
