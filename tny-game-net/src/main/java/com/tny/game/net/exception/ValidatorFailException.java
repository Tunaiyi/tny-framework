package com.tny.game.net.exception;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;

public class ValidatorFailException extends DispatchException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String account;

    private String ip;

    public ValidatorFailException(ResultCode code, String account, String ip, String message, Throwable e) {
        super(code, "!!|账号:" + account + "|" + "ip:" + ip + "|" + (message == null ? code.getMessage() : message));
    }

    public ValidatorFailException(ResultCode code, String account, String ip, String message) {
        super(code, "!!|账号:" + account + "|" + "ip:" + ip + "|" + (message == null ? code.getMessage() : message));
    }

    public ValidatorFailException(ResultCode code, String account, String ip, Throwable e) {
        this(code, account, ip, code.getMessage(), e);
    }

    public ValidatorFailException(ResultCode code, String account, String ip) {
        this(code, account, ip, code.getMessage());
    }

    public ValidatorFailException(String account, String ip, String message, Throwable e) {
        this(CoreResponseCode.VALIDATOR_FAIL, account, ip, message, e);
    }

    public ValidatorFailException(String account, String ip, String message) {
        this(CoreResponseCode.VALIDATOR_FAIL, account, ip, message);
    }

    public String getAccount() {
        return this.account;
    }

    public String getIp() {
        return this.ip;
    }

}
