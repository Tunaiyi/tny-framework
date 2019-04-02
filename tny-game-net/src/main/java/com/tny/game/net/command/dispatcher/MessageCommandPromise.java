package com.tny.game.net.command.dispatcher;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;

import java.util.concurrent.Future;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-01-23 15:37
 */
public class MessageCommandPromise {

    private String name;

    private Waiter<Object> waiter;

    private Object result;

    private Throwable cause;

    private long timeout = -1;

    private boolean done = false;

    public MessageCommandPromise(String name) {
        this.name = name;
    }

    public boolean isSuccess() {
        return this.done && this.cause == null;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isWaiting() {
        return this.waiter != null;
    }

    public Object getResult() {
        return result;
    }

    public Throwable getCause() {
        return cause;
    }

    void checkWait() {
        if (this.waiter != null) { // 检测是否
            if (this.waiter.isDone()) {
                Waiter<Object> waiter = this.waiter;
                if (waiter.isSuccess()) {
                    // 等待成功
                    this.setResult(waiter.getResult());
                } else {
                    // 等待异常
                    setResult(waiter.getCause());
                }
            } else {
                if (timeout > 0 && System.currentTimeMillis() > timeout) {
                    this.setResult(new CommandTimeoutException(NetResultCode.EXECUTE_TIMEOUT, format("执行 {} 超时", this.name)));
                }
            }
        }
    }

    public void setResult(Object result) {
        if (result instanceof Future)
            result = Waiter.of(as(result));

        if (result instanceof Waiter) {
            this.waiter = as(result);
            this.timeout = System.currentTimeMillis() + 5000; // 超时
        } else if (result instanceof Throwable) {
            this.cause = as(result);
            this.waiter = null;
            this.done = true;
        } else {
            this.result = result;
            this.waiter = null;
            this.done = true;
        }
    }

}