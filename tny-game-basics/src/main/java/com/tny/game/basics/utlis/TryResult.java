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

package com.tny.game.basics.utlis;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.result.*;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/7/29.
 */
public interface TryResult<M> extends DoneResult<M> {

    boolean isFailedToTry();

    TryToDoResult getResult();

    void ifFailedToTry(Consumer<DefaultTryResult<M>> consumer);

    void ifSucceedToTry(Consumer<DefaultTryResult<M>> consumer);

}
