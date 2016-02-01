package com.tny.game.base.item;

import com.tny.game.LogUtils;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.exception.StuffAlertException;

import java.text.MessageFormat;

/**
 * 数量改变策略枚举
 *
 * @author KGTny
 */
public enum AlterType {

    /**
     * 检测上下限，操作界限抛出异常CountableStuffAlertException
     */
    CHECK(1) {
        @Override
        public long consume(long playerID, ItemModel model, long number, long alertNum) {
            if (this.overLowerLimit(model, number, alertNum))
                throw new StuffAlertException(
                        ItemResultCode.LACK_NUMBER,
                        playerID,
                        model,
                        number,
                        alertNum,
                        LogUtils.format("玩家({}) 物品[{}]数量为 {1},不够扣除 {2}", playerID, model.getID(), number, alertNum));
            long countNumber, currentNum;
            countNumber = currentNum = number;
            countNumber -= alertNum;
            countNumber = countNumber < 0 ? 0 : countNumber;
            return currentNum - countNumber;
        }

        @Override
        public long receive(long playerID, ItemModel model, long limit, long number, long alertNum) {
            if (this.overUpperLimit(model, limit, number, alertNum))
                throw new StuffAlertException(
                        ItemResultCode.FULL_NUMBER,
                        playerID,
                        model,
                        number,
                        alertNum,
                        LogUtils.format("玩家({}) 物品[{}]数量为 {},增加 {} 会超过上限 {}", playerID, model.getID(), number, alertNum, limit));
            return alertNum;
        }

        @Override
        public boolean overLowerLimit(ItemModel model, long number, long alertNum) {
            if (alertNum < 0)
                throw new IllegalArgumentException(MessageFormat.format("{0}扣除物品数量{1}小于0", model.getID(), alertNum));
            return alertNum > number;
        }

        @Override
        public boolean overUpperLimit(ItemModel model, long limit, long number, long alertNum) {
            if (alertNum < 0)
                throw new IllegalArgumentException(MessageFormat.format("{0}接受物品数量{1}小于0", model.getID(), alertNum));
            return limit > -1 && alertNum + number > limit;
        }

    },
    /**
     * 不检测上下限，低于下限为0,高于上限不做仍和处理
     */
    UNCHECK(2) {
        @Override
        public long consume(long playerID, ItemModel model, long number, long alertNum) {
            this.overLowerLimit(model, number, alertNum);
            long countNumber, currentNum;
            countNumber = currentNum = number;
            countNumber -= alertNum;
            countNumber = countNumber < 0 ? 0 : countNumber;
            return currentNum - countNumber;
        }

        @Override
        public long receive(long playerID, ItemModel model, long limit, long number, long alertNum) {
            this.overUpperLimit(model, limit, number, alertNum);
            return alertNum;
        }

        @Override
        public boolean overLowerLimit(ItemModel model, long number, long alertNum) {
            if (alertNum < 0)
                throw new IllegalArgumentException(MessageFormat.format("{0}扣除物品数量{1}小于0", model.getID(), alertNum));
            return false;
        }

        @Override
        public boolean overUpperLimit(ItemModel model, long limit, long number, long alertNum) {
            if (alertNum < 0)
                throw new IllegalArgumentException(MessageFormat.format("{0}接受物品数量{1}小于0", model.getID(), alertNum));
            return false;
        }
    },
    /**
     * 忽略超出界限，低于下限为0，高于上限，则忽略多出部分
     */
    IGNORE(3) {
        @Override
        public long consume(long playerID, ItemModel model, long number, long alertNum) {
            this.overLowerLimit(model, number, alertNum);
            long countNumber, currentNum;
            countNumber = currentNum = number;
            countNumber -= alertNum;
            countNumber = countNumber < 0 ? 0 : countNumber;
            return currentNum - countNumber;
        }

        @Override
        public long receive(long playerID, ItemModel model, long limit, long number, long alertNum) {
            this.overUpperLimit(model, limit, number, alertNum);
            long countNumber = number;
            countNumber += alertNum;
            countNumber = limit > CountableStuff.UNLIMINT && countNumber > limit ? limit : countNumber;
            long result = countNumber - number;
            return result <= 0 ? 0 : result;
        }

        @Override
        public boolean overLowerLimit(ItemModel model, long number, long alertNum) {
            if (alertNum < 0)
                throw new IllegalArgumentException(MessageFormat.format("{0}扣除物品数量{1}小于0", model.getID(), alertNum));
            return false;
        }

        @Override
        public boolean overUpperLimit(ItemModel model, long limit, long number, long alertNum) {
            if (alertNum < 0)
                throw new IllegalArgumentException(MessageFormat.format("{0}接受物品数量{1}小于0", model.getID(), alertNum));
            return false;
        }
    };

    private final long ID;

    private AlterType(long iD) {
        this.ID = iD;
    }

    public long getID() {
        return this.ID;
    }

    public static AlterType valueOf(long value) {
        for (AlterType type : AlterType.values()) {
            if (type.ID == value)
                return type;
        }
        throw new NullPointerException(LogUtils.format(
                "ID 为 {} 的 {} 不能存在", value, AlterType.class));
    }

    public abstract long consume(long playerID, ItemModel model, long number, long alertNum);

    public abstract long receive(long playerID, ItemModel model, long limit, long number, long alertNum);

    public abstract boolean overLowerLimit(ItemModel model, long number, long alertNum);

    public abstract boolean overUpperLimit(ItemModel model, long limit, long number, long alertNum);

    public long consume(CountableStuff<?> stuff, long alertNum) {
        return this.consume(stuff.getPlayerID(), stuff.getModel(), stuff.getNumber(), alertNum);
    }

    public long receive(CountableStuff<?> stuff, long alertNum) {
        return this.receive(stuff.getPlayerID(), stuff.getModel(), stuff.getNumberLimit(), stuff.getNumber(), alertNum);
    }

    public boolean overLowerLimit(CountableStuff<?> stuff, long alertNum) {
        return this.overLowerLimit(stuff.getModel(), stuff.getNumber(), alertNum);
    }

    public boolean overUpperLimit(CountableStuff<?> stuff, long alertNum) {
        return this.overUpperLimit(stuff.getModel(), stuff.getNumberLimit(), stuff.getNumber(), alertNum);
    }
}
