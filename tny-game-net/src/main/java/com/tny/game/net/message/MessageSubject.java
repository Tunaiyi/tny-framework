package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageSubject extends Protocol {

    ResultCode getCode();

    MessageMode getMode();

    long getToMessage();

    Object getBody();

}
