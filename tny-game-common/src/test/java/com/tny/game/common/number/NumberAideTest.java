package com.tny.game.common.number;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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