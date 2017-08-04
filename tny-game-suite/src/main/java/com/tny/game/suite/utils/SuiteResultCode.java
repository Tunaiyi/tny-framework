package com.tny.game.suite.utils;

import com.tny.game.base.utlis.ConfigerChecker;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodeType;

public enum SuiteResultCode implements ResultCode {

    // 系统错误 用户登录相关
    AUTH_USER_LOGIN_ERROR_SID(2000, "登录服务器ID错误", ResultCodeType.ERROR),
    AUTH_USER_IS_FULL(2001, "登录服务器ID错误", ResultCodeType.ERROR),
    AUTH_SERVER_IS_OFFLINE(2002, "服务器正在维护请稍后再试", ResultCodeType.ERROR),
    AUTH_NO_TICKET(2003, "登陆票据不存在", ResultCodeType.ERROR),
    AUTH_ERROR(2004, "登陆认证错误", ResultCodeType.ERROR),
    AUTH_NO_ACCOUNT(2005, "登陆认证账号不存在", ResultCodeType.ERROR),

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
    private final ResultCodeType type;

    SuiteResultCode(int code, String message) {
        this(code, message, ResultCodeType.GENERAL);
    }

    SuiteResultCode(int code, String message, ResultCodeType type) {
        this.code = code;
        this.message = message;
        this.type = type;
        ConfigerChecker.check(this.getClass(), code, "{}-[{}-ID:{}]发生重复", this.getClass(), this, code);
        this.registerSelf();
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public ResultCodeType getType() {
        return this.type;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
