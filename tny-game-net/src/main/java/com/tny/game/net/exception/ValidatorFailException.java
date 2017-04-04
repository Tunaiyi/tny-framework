package com.tny.game.net.exception;

import com.tny.game.LogUtils;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;

public class ValidatorFailException extends DispatchException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String account;

    private String ip;

    public ValidatorFailException(ResultCode code, String message, Throwable e) {
        super(code, LogUtils.format("!!|({}) {} | {}", code.getCode(), code.getMessage(), message), e);
    }

    public ValidatorFailException(ResultCode code, String message) {
        this(code, message, null);
    }

    public ValidatorFailException(ResultCode code) {
        this(code, "", null);
    }

    public ValidatorFailException(ResultCode code, Throwable e) {
        this(code, "", e);
    }

    public ValidatorFailException(String message, Throwable e) {
        this(CoreResponseCode.VALIDATOR_FAIL, message, e);
    }

    public ValidatorFailException(String message) {
        this(CoreResponseCode.VALIDATOR_FAIL, message);
    }

    public String getAccount() {
        return this.account;
    }

    public String getIp() {
        return this.ip;
    }

}
