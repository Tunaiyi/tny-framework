package com.tny.game.common.reflect.javassist;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/21 11:36
 **/
public class PlayerRun implements MethodInvoker {

    @Override
    public Object invoke(Object host, Object... args) {
        return ((JavassistAccessorsTest.Player)host).getAge();
    }

}
