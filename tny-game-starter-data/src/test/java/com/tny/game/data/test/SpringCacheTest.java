package com.tny.game.data.test;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/8 12:23 下午
 */
@SpringBootTest(classes = GameTestApp.class)
class SpringCacheTest {

    @Test
    private void testCache() {
        System.out.println("test");
    }

}
