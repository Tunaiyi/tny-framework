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
package com.tny.game.demo.core.common;

import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
@Component
public class DemoAuthenticationValidator implements AuthenticationValidator {

    public DemoAuthenticationValidator() {
        System.out.println("DemoAuthenticateValidator");
    }

    @Override
    public Certificate validate(Tunnel tunnel, Message message)
            throws RpcInvokeException, AuthFailedException {
        Object value = message.bodyAs(Object.class);
        if (value instanceof List) {
            List<Object> paramList = as(value);
            return Certificates.createAuthenticated(as(paramList.get(0)), as(paramList.get(1)), as(paramList.get(1)), DefaultContactType.DEFAULT_USER);
        }
        if (value instanceof LoginDTO) {
            LoginDTO dto = as(value);
            return Certificates.createAuthenticated(dto.getCertId(), dto.getUserId(), dto.getUserId(), DefaultContactType.DEFAULT_USER);
        }
        if (value instanceof LoginResultDTO) {
            LoginResultDTO dto = as(value);
            return Certificates.createAuthenticated(System.currentTimeMillis(), dto.getUserId(), dto.getUserId(), DefaultContactType.DEFAULT_USER);
        }
        System.out.println(value);
        throw new AuthFailedException("登录失败");
    }

}
