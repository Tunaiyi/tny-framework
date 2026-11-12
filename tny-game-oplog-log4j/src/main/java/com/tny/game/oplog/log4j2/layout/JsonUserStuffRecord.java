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

package com.tny.game.oplog.log4j2.layout;

import com.fasterxml.jackson.annotation.*;
import com.tny.game.oplog.*;
import com.tny.game.oplog.record.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

@JsonAutoDetect(
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonUserStuffRecord {

    @JsonProperty(index = 1)
    private long rcid;

    @JsonProperty(index = 2)
    private long uid;

    @JsonProperty(index = 3)
    private String name;

    @JsonProperty(index = 4)
    private int sid;

    @JsonProperty(index = 5)
    private List<StuffRecord> stuffs;

    public JsonUserStuffRecord() {
    }

    @SuppressWarnings("unchecked")
    public JsonUserStuffRecord(long rcid, UserStuffRecord log) {
        this.rcid = rcid;
        this.uid = log.getUserId();
        this.name = log.getName();
        this.sid = log.getServerId();
        this.stuffs = log.getStuffLogs()
                .stream()
                .map(JsonUserStuffRecord::log2Record)
                .collect(Collectors.toList());
    }

    private static StuffRecord log2Record(StuffSettleLog log) {
        if (log instanceof StuffRecord) {
            return as(log, StuffRecord.class);
        }
        return new StuffRecord(log);
    }

    public long getRecordId() {
        return rcid;
    }

    public long getUserId() {
        return uid;
    }

    public int getServerId() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public List<StuffRecord> getStuffs() {
        return stuffs;
    }

}
