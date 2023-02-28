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
package com.tny.game.redisson;

import com.tny.game.redisson.script.*;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/2/20 15:09
 **/
public class FileScriptContent implements ScriptContent {

    private final String script;

    private final String digest;

    FileScriptContent(String file) {
        this.script = LuaScriptLoader.loadScript(file);
        this.digest = DigestUtils.sha1Hex(script);
    }

    @Override
    public String getScript() {
        return script;
    }

    @Override
    public String getDigest() {
        return digest;
    }

}
