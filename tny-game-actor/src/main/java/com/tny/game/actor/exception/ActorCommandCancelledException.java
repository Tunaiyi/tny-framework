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

package com.tny.game.actor.exception;

import com.tny.game.actor.local.*;

/**
 * actor线程中断异常
 * Created by Kun Yang on 16/1/17.
 */
public class ActorCommandCancelledException extends ActorCommandException {

    public ActorCommandCancelledException(ActorCommand<?> command) {
        super(command);
    }

    public ActorCommandCancelledException(ActorCommand<?> command, Throwable cause) {
        super(command, cause);
    }

    public ActorCommandCancelledException(ActorCommand<?> command, String message, Throwable cause) {
        super(command, message, cause);
    }

}
