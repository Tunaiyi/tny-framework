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

package com.tny.game.net.message;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.application.*;

/**
 * 玩家附件
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 05:09
 **/
@ProtobufClass
public class ForwardContact implements Contact {

    @Protobuf(order = 1)
    private long contactId;

    @Protobuf(order = 2)
    private int contactTypeId;

    @Ignore
    private ContactType contactType;

    public ForwardContact() {
    }

    public ForwardContact(Contact contact) {
        this.contactId = contact.getContactId();
        this.contactType = contact.getContactType();
        this.contactTypeId = this.contactType.id();
    }

    @Override
    public long getContactId() {
        return contactId;
    }

    public int getContactTypeId() {
        return contactTypeId;
    }

    @Override
    public ContactType getContactType() {
        if (contactType == null) {
            contactType = ContactTypes.of(contactTypeId);
        }
        return contactType;
    }

}
