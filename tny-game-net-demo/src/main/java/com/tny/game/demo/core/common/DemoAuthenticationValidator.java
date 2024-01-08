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
