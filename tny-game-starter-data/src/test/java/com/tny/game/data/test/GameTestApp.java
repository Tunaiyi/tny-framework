package com.tny.game.data.test;

import com.tny.game.boot.launcher.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * <p>
 */
@ActiveProfiles("test")
@SpringBootApplication(
        scanBasePackages = {"com.tny.game"})
public class GameTestApp {

    public static void main(String[] args) throws InterruptedException {
        ApplicationLauncherContext.register(GameTestApp.class);
        ApplicationContext context = SpringApplication.run(GameTestApp.class, args);
        Thread.sleep(600000);
    }

}
