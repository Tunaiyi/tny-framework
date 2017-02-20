package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContent {

    private ResultCode code = ResultCode.SUCCESS;

    private Object body;

    private Integer toMessage;

    private MessageAction<Object> messageAction;

    private MessageSentHandler sentHandler;

}
