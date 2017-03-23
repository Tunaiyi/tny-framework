package com.tny.game.net.exception;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.session.Session;

public class ValidatorFailException extends DispatchException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String account;

    private String ip;

    public ValidatorFailException(ResultCode code, Session<?> session, String message, Throwable e) {
        super(code, "!!|" + session + "|" + (message == null ? code.getMessage() : message), e);
    }

    public ValidatorFailException(ResultCode code, Session<?> session, String message) {
        super(code, "!!|" + session + "|" + (message == null ? code.getMessage() : message));
    }

    public ValidatorFailException(ResultCode code, Session<?> session, Throwable e) {
        this(code, session, code.getMessage(), e);
    }

    public ValidatorFailException(ResultCode code, Session<?> session) {
        this(code, session, code.getMessage());
    }

    public ValidatorFailException(Session<?> session, String message, Throwable e) {
        this(CoreResponseCode.VALIDATOR_FAIL, session, message, e);
    }

    public ValidatorFailException(Session<?> session, String message) {
        this(CoreResponseCode.VALIDATOR_FAIL, session, message);
    }

    public String getAccount() {
        return this.account;
    }

    public String getIp() {
        return this.ip;
    }

}
