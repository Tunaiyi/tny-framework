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
package com.tny.game.net.application;

import com.tny.game.net.message.*;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * 服务者,接入的服务点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 15:11
 **/
public interface RpcAccessPoint extends RpcServicer, Contact, Comparable<RpcAccessPoint> {

    Comparator<RpcAccessPoint> COMPARATOR = Comparator.comparing(RpcAccessPoint::getContactId);

    @Override
    default ContactType getContactType() {
        return getServiceType();
    }

    @Override
    default int compareTo(@Nonnull RpcAccessPoint o) {
        return COMPARATOR.compare(this, o);
    }

}
