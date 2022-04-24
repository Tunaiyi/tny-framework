package com.tny.game.basics.exception;

import com.tny.game.common.result.*;

public enum ItemResultCode implements ResultCode {

    //	SUCCESS(100, "请求处理成功"),

    ACTION_NO_EXIST(1000, "不存在此操作"),

    BEHAVIOR_NO_EXIST(1001, "不存在此行为"),

    ABILITY_NO_EXIST(1002, "不存在此能力"),

    MODEL_NO_EXIST(1003, "不存在此模型"),

    OPTION_NO_EXIST(1004, "不存在此操作选项"),

    LACK_NUMBER(1010, "物品数量不足"),

    FULL_NUMBER(1011, "物品数量超过上线"),

    NO_CONSUME(1012, "物品不可消耗"),

    NO_RECEIVE(1013, "物品不可获取"),

    TRY_TO_DO_FAIL(1020, "不足条件执行操作"),

    ROLE_NO_EXIST(1030, "角色不存在"),

    //
    ;

    private final int code;

    private final String message;

    private final ResultLevel type;

    ItemResultCode(int code, String message) {
        this.code = code;
        this.message = message;
        this.type = ResultLevel.GENERAL;
        this.registerSelf();
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public ResultLevel getLevel() {
        return type;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
