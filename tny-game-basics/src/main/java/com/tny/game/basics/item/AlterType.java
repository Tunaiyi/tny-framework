package com.tny.game.basics.item;

import com.tny.game.basics.exception.*;
import com.tny.game.common.number.*;

import static com.tny.game.common.number.NumberAide.*;
import static com.tny.game.common.utils.StringAide.*;

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
		public Number consume(long playerId, ItemModel model, Number number, Number alertNum) {
			if (this.overLowerLimit(model, number, alertNum)) {
				throw new StuffAlertException(
						ItemResultCode.LACK_NUMBER,
						playerId,
						model,
						number,
						alertNum,
						format("玩家({}) 物品[{}]数量为 {},不够扣除 {}", playerId, model.getId(), number, alertNum));
			}
			Number countNumber, currentNum;
			countNumber = currentNum = number;
			countNumber = sub(countNumber, alertNum);
			countNumber = less(countNumber, 0) ? as(0, number.getClass()) : countNumber;
			return sub(currentNum, countNumber);
		}

		@Override
		public Number receive(long playerId, ItemModel model, Number limit, Number number, Number alertNum) {
			if (this.overUpperLimit(model, limit, number, alertNum)) {
				throw new StuffAlertException(
						ItemResultCode.FULL_NUMBER,
						playerId,
						model,
						number,
						alertNum,
						format("玩家({}) 物品[{}]数量为 {},增加 {} 会超过上限 {}", playerId, model.getId(), number, alertNum, limit));
			}
			return alertNum;
		}

		@Override
		public boolean overLowerLimit(ItemModel model, Number number, Number alertNum) {
			if (less(alertNum, 0)) {
				throw new IllegalArgumentException(format("{}扣除物品数量{}小于0", model.getId(), alertNum));
			}
			return greater(alertNum, number);
		}

		@Override
		public boolean overUpperLimit(ItemModel model, Number limit, Number number, Number alertNum) {
			if (less(alertNum, 0)) {
				throw new IllegalArgumentException(format("{}接受物品数量{}小于0", model.getId(), alertNum));
			}
			return greater(limit, -1) && greater(add(alertNum, number), limit);
		}

	},
	/**
	 * 不检测上下限，低于下限为0,高于上限不做仍和处理
	 */
	UNCHECK(2) {
		@Override
		public Number consume(long playerId, ItemModel model, Number number, Number alertNum) {
			this.overLowerLimit(model, number, alertNum);
			Number countNumber, currentNum;
			countNumber = currentNum = number;
			countNumber = sub(countNumber, alertNum);
			countNumber = less(countNumber, 0) ? as(0, number.getClass()) : countNumber;
			return sub(currentNum, countNumber);
		}

		@Override
		public Number receive(long playerId, ItemModel model, Number limit, Number number, Number alertNum) {
			this.overUpperLimit(model, limit, number, alertNum);
			return alertNum;
		}

		@Override
		public boolean overLowerLimit(ItemModel model, Number number, Number alertNum) {
			if (less(alertNum, 0)) {
				throw new IllegalArgumentException(format("{}扣除物品数量{}小于0", model.getId(), alertNum));
			}
			return false;
		}

		@Override
		public boolean overUpperLimit(ItemModel model, Number limit, Number number, Number alertNum) {
			if (less(alertNum, 0)) {
				throw new IllegalArgumentException(format("{}接受物品数量{}小于0", model.getId(), alertNum));
			}
			return false;
		}
	},
	/**
	 * 忽略超出界限，低于下限为0，高于上限，则忽略多出部分
	 */
	IGNORE(3) {
		@Override
		public Number consume(long playerId, ItemModel model, Number number, Number alertNum) {
			this.overLowerLimit(model, number, alertNum);
			Number countNumber, currentNum;
			countNumber = currentNum = number;
			countNumber = sub(countNumber, alertNum);
			countNumber = less(countNumber, 0) ? NumberAide.as(0, number) : countNumber;
			return sub(currentNum, countNumber);
		}

		@Override
		public Number receive(long playerId, ItemModel model, Number limit, Number number, Number alertNum) {
			this.overUpperLimit(model, limit, number, alertNum);
			Number countNumber = number;
			countNumber = add(countNumber, alertNum);
			countNumber = greater(limit, MultipleStuff.UNLIMITED) && greater(countNumber, limit) ? limit : countNumber;
			Number result = sub(countNumber, number);
			return lessEqual(result, 0) ? NumberAide.as(0, number) : result;
		}

		@Override
		public boolean overLowerLimit(ItemModel model, Number number, Number alertNum) {
			if (less(alertNum, 0)) {
				throw new IllegalArgumentException(format("{}扣除物品数量{}小于0", model.getId(), alertNum));
			}
			return false;
		}

		@Override
		public boolean overUpperLimit(ItemModel model, Number limit, Number number, Number alertNum) {
			if (less(alertNum, 0)) {
				throw new IllegalArgumentException(format("{}接受物品数量{}小于0", model.getId(), alertNum));
			}
			return false;
		}
	};

	private final int id;

	AlterType(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static AlterType valueOf(int value) {
		for (AlterType type : AlterType.values()) {
			if (type.id == value) {
				return type;
			}
		}
		throw new NullPointerException(format(
				"id 为 {} 的 {} 不能存在", value, AlterType.class));
	}

	public abstract Number consume(long playerId, ItemModel model, Number number, Number alertNum);

	public abstract Number receive(long playerId, ItemModel model, Number limit, Number number, Number alertNum);

	public abstract boolean overLowerLimit(ItemModel model, Number number, Number alertNum);

	public abstract boolean overUpperLimit(ItemModel model, Number limit, Number number, Number alertNum);

	public Number deduct(MultipleStuff<?, ?> stuff, Number alertNum) {
		return this.consume(stuff.getPlayerId(), stuff.getModel(), stuff.getNumber(), alertNum);
	}

	public Number reward(MultipleStuff<?, ?> stuff, Number alertNum) {
		return this.receive(stuff.getPlayerId(), stuff.getModel(), stuff.getNumberLimit(), stuff.getNumber(), alertNum);
	}

	public boolean overLowerLimit(MultipleStuff<?, ?> stuff, Number alertNum) {
		return this.overLowerLimit(stuff.getModel(), stuff.getNumber(), alertNum);
	}

	public boolean overUpperLimit(MultipleStuff<?, ?> stuff, Number alertNum) {
		return this.overUpperLimit(stuff.getModel(), stuff.getNumberLimit(), stuff.getNumber(), alertNum);
	}
}
