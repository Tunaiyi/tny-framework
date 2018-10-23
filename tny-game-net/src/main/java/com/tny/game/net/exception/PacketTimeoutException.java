package com.tny.game.net.exception;

import com.tny.game.net.base.NetResultCode;

public class PacketTimeoutException extends CommandException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PacketTimeoutException() {
        super(NetResultCode.DECODE_ERROR);
    }

    public PacketTimeoutException(String message) {
        super(NetResultCode.DECODE_ERROR, message);
    }

    public PacketTimeoutException(String message, Throwable cause) {
        super(NetResultCode.DECODE_ERROR, message, cause);
    }

    public PacketTimeoutException(Throwable cause) {
        super(NetResultCode.DECODE_ERROR, cause);
    }

}
