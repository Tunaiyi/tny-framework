package com.tny.game.net.base;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 3:38 下午
 */
@UnitInterface
public interface NetIdGenerator {

    long generate();

}
