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

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.rpc.*;

import java.util.Optional;

/**
 * 会话管理者
 * <p>
 */
@UnitInterface
public interface SessionKeeperManager {

    SessionKeeper loadKeeper(ContactType userType, NetAccessMode accessMode);

    Optional<SessionKeeper> getKeeper(ContactType userType);

}
