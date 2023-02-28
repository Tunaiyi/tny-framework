/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.redisson.script;

import com.tny.game.common.io.config.*;
import org.apache.commons.io.*;
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
            String script = IOUtils.toString(inputStream, Charsets.toCharset("utf-8"));
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
