package com.tny.game.asyndb;

/**
 * 在错误的状态下提交持久化操作异常
 *
 * @author KGTny
 */
public class SubmitAtWrongStateException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -1;
    private AsyncDBState state;
    private Operation operation;

    public SubmitAtWrongStateException(String msg, AsyncDBState state, Operation operation) {
        super(msg);
        this.state = state;
        this.operation = operation;
    }

    public AsyncDBState getState() {
        return state;
    }

    public Operation getOperation() {
        return operation;
    }

}
