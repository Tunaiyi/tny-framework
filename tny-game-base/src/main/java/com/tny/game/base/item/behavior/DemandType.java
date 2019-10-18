package com.tny.game.base.item.behavior;

import com.tny.game.common.enums.EnumIdentifiable;
import com.tny.game.common.result.ResultCode;

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
public interface DemandType extends EnumIdentifiable<Integer> {

    /**
     * 条件类型
     *
     * @return
     */
    Integer getId();

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
