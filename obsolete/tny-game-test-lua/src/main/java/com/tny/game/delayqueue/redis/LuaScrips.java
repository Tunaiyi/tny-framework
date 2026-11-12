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
package com.tny.game.delayqueue.redis;

import com.tny.game.common.config.*;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 12:18 下午
 */
public class LuaScrips {

    public static void scrips() {
        System.out.println(ConfigLoader.loadFile("ack_message.lua"));
        try (InputStream inputStream = ConfigLoader.loadInputStream("ack_message.lua")) {
            System.out.println(String.join("\n", IOUtils.readLines(inputStream, "utf-8")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

}
