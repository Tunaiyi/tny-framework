package com.tny.game.net.dispatcher.exception;

import com.tny.game.net.base.CoreResponseCode;

public class ValidatorFailException extends DispatchException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String account;

    private String ip;

    public ValidatorFailException(String account, String ip, String message, Throwable e) {
        super(CoreResponseCode.VALIDATOR_FAIL, "!!|账号:" + account + "|" + "ip:" + ip + "|" + message, e);
    }

    public ValidatorFailException(String account, String ip, String message) {
        super(CoreResponseCode.VALIDATOR_FAIL, "!!|账号:" + account + "|" + "ip:" + ip + "|" + message);
    }

    public String getAccount() {
        return this.account;
    }

    public String getIp() {
        return this.ip;
    }

}
