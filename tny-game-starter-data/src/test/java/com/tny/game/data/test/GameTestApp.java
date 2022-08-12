/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
