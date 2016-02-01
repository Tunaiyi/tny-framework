package com.tny.game.net.client.exception;

import com.tny.game.common.result.ResultCode;

public class ClientException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int responseCode;

    public ClientException(ResultCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode.getCode();
    }

    public ClientException(ResultCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.responseCode = responseCode.getCode();
    }

    public ClientException(int responesCode, String message) {
        super(message);
    }

    public ClientException(int responesCode, String message, Throwable cause) {
        super(message, cause);
    }

    public int getResponseCode() {
        return this.responseCode;
    }

}
