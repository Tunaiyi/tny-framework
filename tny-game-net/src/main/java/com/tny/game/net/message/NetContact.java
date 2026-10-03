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

import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/2/9 20:03
 **/
public interface NetContact extends AddressPeer, Contact {

    /**
     * @return 接入模式
     */
    NetAccessMode getAccessMode();

}
