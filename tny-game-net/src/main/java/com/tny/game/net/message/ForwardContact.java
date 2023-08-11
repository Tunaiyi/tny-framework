/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.message;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.base.*;

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
        this.contactId = contact.contactId();
        this.contactType = contact.contactType();
        this.contactTypeId = this.contactType.id();
    }

    @Override
    public long contactId() {
        return contactId;
    }

    public int getContactTypeId() {
        return contactTypeId;
    }

    @Override
    public ContactType contactType() {
        if (contactType == null) {
            contactType = ContactTypes.of(contactTypeId);
        }
        return contactType;
    }

}
