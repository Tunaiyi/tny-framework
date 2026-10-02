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
package com.tny.game.net.session;

import com.tny.game.common.enums.*;

public enum TunnelConnectorStatus implements IntEnumerable {

    INITIAL(0),
    CONNECTING(1),
    RECONNECTING(2),
    CONNECTED(3),
    DISCONNECT(4),
    CLOSE(5),

    //
    ;

    private final int id;


    private TunnelConnectorStatus(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

}
