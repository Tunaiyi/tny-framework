package com.tny.game.suite.base.listener;

import com.tny.game.base.item.CountableStuff;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.event.Event;

public abstract class CountableStuffEvent<S extends CountableStuff<?>> extends Event<S> {

    private Action action;

    private long oldNumber;

    private long alter;

    private long newNumber;

    protected CountableStuffEvent(String handler, S soure, Action action,
                                  long oldNumber, long alter, long newNumber) {
        super(handler, soure);
        this.action = action;
        this.oldNumber = oldNumber;
        this.alter = alter;
        this.newNumber = newNumber;
    }

    public enum StuffEventHandlerType {
        /**
         * 使用
         */
        CONSUME("consume"),
        /**
         * 接受
         */
        RECEIVE("receive"),
        /**
         * 重置
         */
        RESET("reset"),
        /**
         * 刷新
         */
        REFRESH("refresh"),
        /**
         * 绑定
         */
        BIND("bind"),
        /**
         * 重置购买次数
         */
        RESET_BUY_TIMES("resetBuyTimes"),;

        private final String handle;

        StuffEventHandlerType(String handle) {
            this.handle = handle;
        }

        public String getHandler() {
            return handle;
        }

    }

    public Action getAction() {
        return action;
    }

    public long getOldNumber() {
        return oldNumber;
    }

    public long getAlter() {
        return alter;
    }

    public long getNewNumber() {
        return newNumber;
    }

    @Override
    public String toString() {
        return "CountableStuffEvent [action=" + action + ", oldNumber="
                + oldNumber + ", alter=" + alter + ", newNumber=" + newNumber
                + "]";
    }

}
