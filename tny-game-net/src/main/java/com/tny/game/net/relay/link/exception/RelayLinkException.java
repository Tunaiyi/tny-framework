package com.tny.game.net.relay.link.exception;

import com.tny.game.common.result.*;
import com.tny.game.net.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:00 下午
 */
public class RelayLinkException extends NetGeneralException {

	public RelayLinkException(ResultCode code) {
		super(code);
	}

	public RelayLinkException(ResultCode code, String message, Object... messageParams) {
		super(code, message, messageParams);
	}

	public RelayLinkException(ResultCode code, Throwable cause) {
		super(code, cause);
	}

	public RelayLinkException(ResultCode code, Throwable cause, String message, Object... messageParams) {
		super(code, cause, message, messageParams);
	}

}
