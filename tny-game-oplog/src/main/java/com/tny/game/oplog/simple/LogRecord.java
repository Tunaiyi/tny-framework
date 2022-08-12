/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog.simple;

import com.fasterxml.jackson.annotation.*;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class LogRecord {

    @JsonProperty(index = 1)
    private String id;

    @JsonProperty(index = 2)
    private String type;

    @JsonProperty(index = 3)
    private String content;

    public LogRecord(String id, String type, String content) {
        this.id = id;
        this.type = type;
        this.content = content;
    }

    public LogRecord() {
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

}
