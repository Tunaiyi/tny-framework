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
