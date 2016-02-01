package com.tny.game.common.reflect.javassist;

public interface ConstructInvoker {

    /**
     * 调用方法
     *
     * @param host 执行对象
     * @param args 执行参数
     * @return
     */
    Object newInstance(Object... args);

}
