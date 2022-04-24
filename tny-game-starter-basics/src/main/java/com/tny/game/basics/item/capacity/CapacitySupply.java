package com.tny.game.basics.item.capacity;

import java.util.Map;

/**
 * 可通过能力接口
 * Created by Kun Yang on 16/3/12.
 */
public interface CapacitySupply extends Capable {

    /**
     * 是否存在 capacity 能力值
     *
     * @param capacity 能力类型
     * @return 存在 capacity 能力值返回 true, 否则返回false
     */
    boolean isHasCapacity(Capacity capacity);

    /**
     * @return 获取所有相关的所有 能力值
     */
    Map<Capacity, Number> getAllCapacities();

}
