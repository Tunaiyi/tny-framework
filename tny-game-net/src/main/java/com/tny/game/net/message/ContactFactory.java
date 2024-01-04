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
package com.tny.game.net.message;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.application.*;

/**
 * 消息者工厂
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/21 04:19
 **/
@UnitInterface
public interface ContactFactory {

    /**
     * 创建 Contact
     *
     * @param type      消息者类型
     * @param contactId 消息者id
     * @return 返回创建的 Contact
     */
    <C extends Contact> C createContact(ContactType type, long contactId);

    /**
     * 创建 Contact
     *
     * @param contact 转发消息者
     * @return 返回创建的 Contact
     */
    <C extends Contact> C createContact(ForwardContact contact);

}
