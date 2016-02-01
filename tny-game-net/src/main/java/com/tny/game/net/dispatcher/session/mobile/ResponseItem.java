package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.common.result.ResultCode;

public class ResponseItem {

    private int id;

    private ResultCode code;

    private Object body;

    public ResponseItem(int id, ResultCode code, Object body) {
        super();
        this.id = id;
        this.code = code;
        this.body = body;
    }

    public int getID() {
        return this.id;
    }

    public ResultCode getResultCode() {
        return this.code;
    }

    public Object getBody() {
        return this.body;
    }

}
