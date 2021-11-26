package com.tny.game.basics.item.behavior;

import com.tny.game.common.enums.*;
import com.tny.game.common.result.*;

/**
 * 条件类型接口
 * <p>
 * 枚举命名规则
 * <p>
 * EQ : =
 * NE : !=
 * GE : >=
 * LE : <=
 * GR : >
 * LE : <
 *
 * @author KGTny
 */
public interface DemandType extends IntEnumerable {

	/**
	 * 是否是costDemand
	 *
	 * @return
	 */
	boolean isCost();

	/**
	 * 返回错误码
	 *
	 * @return
	 */
	ResultCode getResultCode();

}
