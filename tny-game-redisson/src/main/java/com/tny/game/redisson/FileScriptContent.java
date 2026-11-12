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
