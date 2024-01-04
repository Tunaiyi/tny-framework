/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.number;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 */
public class NumberAideTest {

    @Test
    public void testConvertString() {
        // assertEquals(Integer.toString(1), NumberAide.numberConverter(-1, ScaleCharacterSets.HEX_LOWER));
        assertEquals(Integer.toString(Integer.MIN_VALUE, 16), NumberAide.numberConverter(Integer.MIN_VALUE, ScaleCharacterSets.HEX_LOWER));
        for (int index = 0; index < 500; index++) {
            assertEquals(Integer.toString(index, 16), NumberAide.numberConverter(index, ScaleCharacterSets.HEX_LOWER));
        }
        assertEquals(Integer.toString(Integer.MAX_VALUE, 16), NumberAide.numberConverter(Integer.MAX_VALUE, ScaleCharacterSets.HEX_LOWER));
        ScaleCharacterSet set = ScaleCharacterSets.SIXTY_TWO_SCALE;
        for (int index = 0; index <= set.length(); index++) {
            System.out.println(NumberAide.numberConverter(index, set));
        }
    }

}