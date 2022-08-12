/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.demo.core.client.controller;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
//@RpcController
//@AuthenticationRequired(Certificates.DEFAULT_USER_TYPE)
//@BeforePlugin(SpringBootParamFilterPlugin.class)
////@MessageFilter(modes = {RESPONSE, PUSH})
//public class GameClientLoginController {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(GameClientLoginController.class);
//
//	@RpcResponse(CtrlerIds.GAME_LOGIN$LOGIN)
//	@BeforePlugin(SpringBootParamFilterPlugin.class)
//	@AuthenticationRequired(value = Certificates.DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
//	public void login(@MsgCode int code, @MsgBody LoginResultDTO dto) {
//		if (!ResultCodes.isSuccess(code)) {
//			LOGGER.info("Login failed : {}", code);
//		} else {
//			LOGGER.info("{} Login finish", dto.getUserId());
//		}
//	}
//
//}
