package com.tny.game.suite.utils;

import com.tny.game.base.utlis.ConfigerChecker;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodeType;

public enum SuiteResultCode implements ResultCode {

    // 系统错误 用户登录相关
    AUTH_USER_LOGIN_ERROR_SID(1000, "登录服务器ID错误", ResultCodeType.ERROR),
    AUTH_USER_IS_FULL(1001, "登录服务器ID错误", ResultCodeType.ERROR),
    AUTH_SERVER_IS_OFFLINE(1002, "服务器正在维护请稍后再试", ResultCodeType.ERROR),

    ITEM_WAREHOUSE_NO_EXIST(1010, "目标不存在"),
    ITEM_WAREHOUSE_TRADE_FAILED(1011, "交易失败"),


    //region 杨焜 == 开发ID 1000_000 - 1200_000
    NAME_CONTENT_ILLEGAL(1000_000, "名字非法字符"),
    NAME_LENGTH_ILLEGAL(1000_001, "名字长度错误"),
    NAME_FILTER_WORD(1000_002, "名字带有屏蔽字"),
    //endregion [杨焜][杨焜][杨焜][杨焜][杨焜][杨焜][杨焜][杨焜][杨焜][杨焜][杨焜][杨焜][杨焜][杨焜]

    //region 晓庆 == 开发ID 1300_000 - 1400_000
    TRAINING_INVITATION_INVALID(1301_001, "突飞邀请无效"),
    GYM_ASSIST_TIME_ERROR(1301_002, "没有协助次数"),
    UNION_GYM_GRID_USEING(1301_004, "训练位已满"),
    GYM_INVITE_IN_CD(1301_005, "邀请CD中....."),

    REAL_EQUIP_EXP_GOODS_ERROR(1302_001, "此材料不能用于装备强化"),
    REAL_EQUIP_LEVEL_LIMIT(1302_003, "装备等级达上限"),
    REAL_EQUIP_TYPE_LIMIT(1302_004, "此类型的装备不能执行操作"),
    REAL_EQUIP_STAR_LIMIT(1302_005, "装备星级达上限"),;
    //endregion [晓庆][晓庆][晓庆][晓庆][晓庆][晓庆][晓庆][晓庆][晓庆][晓庆][晓庆][晓庆][晓庆][晓庆]

    //region 启荣 == 开发ID 1500_000 - 1600_000

    //endregion [启荣][启荣][启荣][启荣][启荣][启荣][启荣][启荣][启荣][启荣][启荣][启荣][启荣][启荣]
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
