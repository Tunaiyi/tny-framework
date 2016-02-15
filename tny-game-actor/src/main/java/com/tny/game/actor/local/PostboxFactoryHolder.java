package com.tny.game.actor.local;

/**
 * 消息箱工厂接口
 * Created by Kun Yang on 16/1/19.
 */
public interface PostboxFactoryHolder {

    PostboxFactory getFactoryByName(String name);

    PostboxFactory getFactoryByClass(Class<? extends PostboxFactory> clazz);

}
