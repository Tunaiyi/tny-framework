package com.tny.game.common.unit;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-25 15:03
 */
@Unit(value = "TestNormalWithSetUnitInterfaces", unitInterfaces = {TestNormalInterlace.class, TestNormalInterlace1.class})
public class TestNormalWithSetUnitInterfaces implements TestNormalInterlace, TestNormalInterlace1, TestNormalInterlace2 {
}
