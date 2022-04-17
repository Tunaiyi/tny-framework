package com.tny.game.basics.item.model;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/16 01:31
 **/
public enum TradeDemandType implements DemandType {

	COST_DEMAND_GE(1) {
		@Override
		public ResultCode getResultCode() {
			return ItemResultCode.TRY_TO_DO_FAIL;
		}

		@Override
		public boolean isCost() {
			return true;
		}

	},

	RECV_DEMAND(2) {
		@Override
		public ResultCode getResultCode() {
			return ItemResultCode.TRY_TO_DO_FAIL;
		}

		@Override
		public boolean isCost() {
			return true;
		}

	};

	private final int id;

	TradeDemandType(int id) {
		this.id = id;
	}

	@Override
	public int id() {
		return this.id;
	}

}
