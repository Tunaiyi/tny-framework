package com.tny.game.base.item.behavior;

import com.tny.game.common.enums.EnumID;
import com.tny.game.common.result.ResultCode;

/**
 * 条件类型接口
 *
 * @author KGTny
 */
public interface DemandType extends EnumID<Integer> {

    /**
     * 条件类型
     *
     * @return
     */
    Integer getID();

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
