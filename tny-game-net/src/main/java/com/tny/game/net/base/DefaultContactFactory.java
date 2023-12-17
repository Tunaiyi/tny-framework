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
package com.tny.game.net.base;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 默认Contact工厂
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/22 05:16
 **/
@Unit
public class DefaultContactFactory implements ContactFactory {

    @Override
    public <M extends Contact> M createContact(ContactType type, long contactId) {
        return as(new DefaultContact(type, contactId));
    }

    @Override
    public <M extends Contact> M createContact(ForwardContact contact) {
        return as(contact);
    }

    private record DefaultContact(ContactType contactType, long contactId) implements Contact {

        @Override
        public long getContactId() {
            return contactId;
        }

        @Override
        public ContactType getContactType() {
            return contactType;
        }
    }

}
