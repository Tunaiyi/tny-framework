package com.tny.game.net.exception;

import com.tny.game.common.utils.Logs;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.NetResponseCode;

public class ValidatorFailException extends DispatchException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String account;

    private String ip;

    public ValidatorFailException(ResultCode code, String message, Throwable e) {
        super(code, Logs.format("!!|({}) {} | {}", code.getCode(), code.getMessage(), message), e);
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
        this(NetResponseCode.VALIDATOR_FAIL, message, e);
    }

    public ValidatorFailException(String message) {
        this(NetResponseCode.VALIDATOR_FAIL, message);
    }

    public String getAccount() {
        return this.account;
    }

    public String getIp() {
        return this.ip;
    }

}
