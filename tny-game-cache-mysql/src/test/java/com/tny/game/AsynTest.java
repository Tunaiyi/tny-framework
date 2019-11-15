package com.tny.game;

import com.tny.game.cache.async.*;
import com.tny.game.cache.testclass.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AsynTest {

    private static ApplicationContext ctx;

    public static void main(String[] args) {
        ctx = new ClassPathXmlApplicationContext("application.xml");
        AsyncCache cache = ctx.getBean(AsyncCache.class);
        cache.flushAll();
        for (int pIndex = 0; pIndex < 10000; pIndex++) {
            cache.addObject(new TestPlayer(pIndex));
        }
    }
}
