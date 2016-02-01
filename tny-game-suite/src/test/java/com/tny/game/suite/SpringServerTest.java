package com.tny.game.suite;

import com.tny.game.suite.launcher.ServerLauncher;
import org.junit.Test;

public class SpringServerTest {

    @Test
    public void testStart() throws Throwable {
        new ServerLauncher("application.xml")
                .start();
    }

}