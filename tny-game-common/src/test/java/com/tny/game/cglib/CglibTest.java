package com.tny.game.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;

public class CglibTest {

    public static class PlayerProxyHandler implements MethodInterceptor {

        private Object target;

        //		private GClass gClass;

        public PlayerProxyHandler(Object target) {
            this.target = target;
            //			this.gClass = CGlibUtils.getGClass(target.getClass());
        }

        @Override
        public Object intercept(Object paramObject, Method paramMethod, Object[] arg2, MethodProxy methodProxy) throws Throwable {
            //			GMethod method = this.gClass.getMethod(paramMethod);
            return methodProxy.invoke(target, arg2);
            //			return method.invoke(target, arg2);
            //			Save save = paramMethod.getAnnotation(Save.class);
            //			if (save != null)
            //				System.out.println(paramMethod.getAnnotation(Save.class).name());
            //			return methodProxy.invoke(target, arg2);
        }

    }

    @Test
    public void testProxy() {
        Player player = new Player("Tom");
        MethodInterceptor interceptor = new PlayerProxyHandler(player);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(player.getClass());
        enhancer.setCallback(interceptor);  // call back method
        Player playerProxy = (Player) enhancer.create();  // create proxy instance
        playerProxy.eat();
        playerProxy.walk();
        int times = 100000000;
        long now = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            playerProxy.getName();
            //			playerProxy.walk();
        }
        System.out.println(System.currentTimeMillis() - now);
        now = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            player.eat();
            //			player.walk();
        }
        System.out.println(System.currentTimeMillis() - now);
    }

}
