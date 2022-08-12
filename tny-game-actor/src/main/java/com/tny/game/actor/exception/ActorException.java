/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.actor.exception;

public class ActorException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ActorException() {

    }

    public ActorException(String message) {
        super(message);
    }

    public ActorException(Throwable cause) {
        super(cause);
    }

    public ActorException(String message, Throwable cause) {
        super(message, cause);
    }

}
