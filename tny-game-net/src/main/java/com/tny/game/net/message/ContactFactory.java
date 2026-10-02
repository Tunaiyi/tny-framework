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
