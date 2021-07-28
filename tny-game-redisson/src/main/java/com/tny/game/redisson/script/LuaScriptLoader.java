package com.tny.game.redisson.script;

import com.tny.game.common.io.config.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.*;

import java.io.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 12:18 下午
 */
public class LuaScriptLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuaScriptLoader.class);

    public static String loadScript(String file) {
        try (InputStream inputStream = FileIOAide.openInputStream(file)) {
            String script = String.join("\n", IOUtils.readLines(inputStream, "utf-8"));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{} file : \n {}}", file, script);
            }
            return script;
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new IllegalArgumentException(format("load {} scrip error", file), e);
        }
    }

}
