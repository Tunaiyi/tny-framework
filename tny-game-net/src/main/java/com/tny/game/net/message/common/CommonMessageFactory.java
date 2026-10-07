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
package com.tny.game.net.message.common;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

@Unit
public class CommonMessageFactory implements MessageFactory {

    public CommonMessageFactory() {
    }

    @Override
    public NetMessage create(long id, MessageSubject subject) {
        return new CommonMessage(new CommonMessageHead(id, subject), subject.getBody());
    }

    @Override
    public NetMessage create(NetMessageHead head, Object body) {
        return new CommonMessage(head, body);
    }

}
