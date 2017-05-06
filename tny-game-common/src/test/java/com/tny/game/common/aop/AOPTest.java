package com.tny.game.common.aop;

import com.tny.game.common.reflect.aop.AOPerBuilder;
import com.tny.game.common.reflect.aop.AfterReturningAdvice;
import com.tny.game.common.reflect.aop.BeforeAdvice;
import com.tny.game.common.reflect.aop.ThrowsAdvice;
import com.tny.game.common.reflect.proxy.WrapperProxy;
import com.tny.game.common.reflect.proxy.WrapperProxyFactory;
import org.junit.Test;

import java.lang.reflect.Method;

public class AOPTest {

    @Test
    public void testPlayerProxy() throws InstantiationException, IllegalAccessException {
        Player player = new PlayerProxy();
        Player playerProxy = AOPerBuilder.newBuilder(Player.class)
                .setAfterReturningAdvice(new AfterReturningAdvice() {

                    @Override
                    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
                        System.out.println(target.getClass() + " -- afterReturning -- " + method + " resut = " + returnValue);
                    }
                }).setBeforeAdvice(new BeforeAdvice() {

                    @Override
                    public void before(Method method, Object[] args, Object target) throws Throwable {
                        System.out.println(target.getClass() + " -- before -- " + method);
                    }
                }).setThrowsAdvice(new ThrowsAdvice() {

                    @Override
                    public void afterThrowing(Method method, Object[] args, Object target, Throwable cause) {
                        System.out.println(target.getClass() + " -- afterThrowing -- " + method + "by cause - " + cause);
                    }
                }).build();
        playerProxy.callName();
        playerProxy.getName();
        playerProxy.friend(20, player, 100L);
        try {
            playerProxy.tryException();
        } catch (Exception e) {
        }

        WrapperProxy<Player> wrapperProxy = WrapperProxyFactory.createWrapper(player);
        Player wrapperPlayer = wrapperProxy.get$Wrapper();
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
    public void testPlayer() throws InstantiationException, IllegalAccessException {
        Player player = new Player();
        Player playerProxy = AOPerBuilder.newBuilder(Player.class)
                .setAfterReturningAdvice((returnValue, method, args, target) -> System.out.println(target.getClass() + " -- afterReturning -- " + method + " resut = " + returnValue))
                .setBeforeAdvice((method, args, target) -> System.out.println(target.getClass() + " -- before -- " + method))
                .setThrowsAdvice((method, args, target, cause) -> System.out.println(target.getClass() + " -- afterThrowing -- " + method + "by cause - " + cause))
                .build();
        playerProxy.callName();
        playerProxy.getName();
        playerProxy.callProtected();
        playerProxy.friend(20, player, 100L);
        try {
            playerProxy.tryException();
        } catch (Exception e) {
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

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {

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
