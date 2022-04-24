package com.tny.game.net.rpc;

import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/27 5:02 下午
 */
public enum RpcMode {

    /**
     * 推送
     */
    PUSH(MessageMode.PUSH, 0, 1),

    /**
     * 请求
     */
    REQUEST(MessageMode.REQUEST, 0, 20),

    //
    ;

    private final MessageMode mode;

    private final int minParamSizeLimit;

    private final int maxParamSizeLimit;

    RpcMode(MessageMode mode, int minParamSizeLimit, int maxParamSizeLimit) {
        this.mode = mode;
        this.minParamSizeLimit = minParamSizeLimit;
        this.maxParamSizeLimit = maxParamSizeLimit;
    }

    public MessageMode getMode() {
        return mode;
    }

    public int getMinParamSizeLimit() {
        return minParamSizeLimit;
    }

    public int getMaxParamSizeLimit() {
        return maxParamSizeLimit;
    }

    public boolean checkParamsSize(int size) {
        return size >= minParamSizeLimit && size <= maxParamSizeLimit;
    }

}
