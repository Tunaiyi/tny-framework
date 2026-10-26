/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                        (method, args, target, cause) -> System.out.println(
                                target.getClass() + " -- afterThrowing -- " + method + "by cause - " + cause)).build();
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
