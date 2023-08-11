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
package com.tny.game.common.aop;

import com.tny.game.common.reflect.aop.*;
import com.tny.game.common.reflect.proxy.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.Objects;

class AOPTest {

    @Test
    void testPlayerProxy() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Player player = new PlayerProxy();
        Player playerProxy = AoperBuilder.newBuilder(Player.class)
                .setAfterReturningAdvice(new AfterReturningAdvice() {

                    @Override
                    public void doAfterReturning(Object returnValue, Method method, Object[] args, Object target) {
                        System.out
                                .println(target.getClass() + " -- afterReturning -- " + method + " resut = " + returnValue);
                    }
                }).setBeforeAdvice(new BeforeAdvice() {

                    @Override
                    public void doBefore(Method method, Object[] args, Object target) throws Throwable {
                        System.out.println(target.getClass() + " -- before -- " + method);
                    }
                }).setThrowsAdvice(
                        (method, args, target, cause) -> System.out.println(target.getClass() + " -- afterThrowing -- " + method + "by cause - " + cause)).build();
        playerProxy.callName();
        playerProxy.getName();
        playerProxy.friend(20, player, 100L);
        try {
            playerProxy.tryException();
        } catch (Exception ignored) {
        }

        WrapperProxy<Player> wrapperProxy = WrapperProxyFactory.createWrapper(player);
        Player wrapperPlayer = Objects.requireNonNull(wrapperProxy).get$Wrapper();
        wrapperPlayer.callName();
        wrapperPlayer.getName();
        wrapperPlayer.friend(20, player, 100L);
        try {
            wrapperPlayer.tryException();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("wraperPlayer " + e);
        }

    }

    @Test
    void testPlayer() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Player player = new Player();
        Player playerProxy = AoperBuilder.newBuilder(Player.class)
                .setAfterReturningAdvice((returnValue, method, args, target) -> System.out
                        .println(target.getClass() + " -- afterReturning -- " + method + " resut = " + returnValue))
                .setBeforeAdvice((method, args, target) -> System.out.println(target.getClass() + " -- before -- " + method))
                .setThrowsAdvice((method, args, target, cause) -> System.out
                        .println(target.getClass() + " -- afterThrowing -- " + method + "by cause - " + cause))
                .build();
        playerProxy.callName();
        playerProxy.getName();
        playerProxy.callProtected();
        playerProxy.friend(20, player, 100L);
        try {
            playerProxy.tryException();
        } catch (Exception ignored) {
        }
        WrapperProxy<Player> wrapperProxy = WrapperProxyFactory.createWrapper(player);
        Player wraperPlayer = wrapperProxy.get$Wrapper();
        wraperPlayer.callName();
        wraperPlayer.getName();
        wraperPlayer.friend(20, player, 100L);
        try {
            wraperPlayer.tryException();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("wraperPlayer " + e);
        }

    }

    static void main(String[] args) throws InstantiationException, IllegalAccessException {

        //		int time = 1000000000;
        //		long now = System.currentTimeMillis();
        //		for (int index = 0; index < time; index++) {
        //			player.friend(20, player, 100L);
        //		}
        //		System.out.println("player =>> " + (System.currentTimeMillis() - now));
        //		now = System.currentTimeMillis();
        //		for (int index = 0; index < time; index++) {
        //			playerProxy.friend(20, player, 100L);
        //		}
        //		System.out.println("playerProxy =>> " + (System.currentTimeMillis() - now));
    }

}
