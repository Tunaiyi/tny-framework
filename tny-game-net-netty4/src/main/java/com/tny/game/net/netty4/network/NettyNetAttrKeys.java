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

package com.tny.game.net.netty4.network;

import com.tny.game.net.codec.*;
import com.tny.game.net.transport.*;
import io.netty.util.AttributeKey;

public interface NettyNetAttrKeys {

    AttributeKey<NetTunnel> TUNNEL = AttributeKey.valueOf(NettyNetAttrKeys.class.getName() + ".TUNNEL");

    AttributeKey<DataPackageContext> WRITE_PACKAGER = AttributeKey.valueOf(NettyNetAttrKeys.class, "WRITE_PACKAGER");
    AttributeKey<DataPackageContext> READ_PACKAGER = AttributeKey.valueOf(NettyNetAttrKeys.class, "READ_PACKAGER");

}
