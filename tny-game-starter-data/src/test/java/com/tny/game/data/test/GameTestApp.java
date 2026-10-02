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
