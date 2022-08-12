/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.common;

import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;

public enum BasicResultCode implements ResultCode {

    // 系统错误 用户登录相关
    AUTH_USER_LOGIN_ERROR_SID(2000, "登录服务器ID错误", ResultLevel.ERROR),
    AUTH_USER_IS_FULL(2001, "登录服务器ID错误", ResultLevel.ERROR),
    AUTH_SERVER_IS_OFFLINE(2002, "服务器正在维护请稍后再试", ResultLevel.ERROR),
    AUTH_NO_TICKET(2003, "登陆票据不存在", ResultLevel.ERROR),
    AUTH_ERROR(2004, "登陆认证错误", ResultLevel.ERROR),
    AUTH_NO_ACCOUNT(2005, "登陆认证账号不存在", ResultLevel.ERROR),
    AUTH_TICKET_TIMEOUT(2006, "登陆票据超时", ResultLevel.ERROR),

    ITEM_WAREHOUSE_NO_EXIST(2010, "目标不存在"),
    ITEM_WAREHOUSE_TRADE_FAILED(2011, "交易失败"),

    NAME_CONTENT_ILLEGAL(2020, "名字非法字符"),
    NAME_LENGTH_ILLEGAL(2021, "名字长度错误"),
    NAME_FILTER_WORD(2022, "名字带有屏蔽字"),

    TEXT_LENGTH_ILLEGAL(2024, "文本长度错误"),
    TEXT_FILTER_WORD(2025, "文本带有屏蔽字"),

    //
    ;

    private final int code;

    private final String message;

    private final ResultLevel type;

    BasicResultCode(int code, String message) {
        this(code, message, ResultLevel.GENERAL);
    }

    BasicResultCode(int code, String message, ResultLevel type) {
        this.code = code;
        this.message = message;
        this.type = type;
        ConfigChecker.check(this.getClass(), code, "{}-[{}-ID:{}]发生重复", this.getClass(), this, code);
        this.registerSelf();
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public ResultLevel getLevel() {
        return this.type;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
