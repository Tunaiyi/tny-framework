/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.common.enums;

import com.tny.game.common.type.*;
import com.tny.game.common.utils.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * numeric-hash-integrity 补做契约钉桩（verify 裁决 REAL_GAP 第 7 项）：
 * EnumAide.ofName 以常量声明名为索引，显示文本不得劫持查找；ObjectAide.enumConvert 链路回归。
 */
public class NumericHashIntegrityContractTest {

    public enum Plain {
        SPRING, SUMMER
    }

    public enum Talkative {
        ALPHA("first"), BETA("second");

        private final String label;

        Talkative(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum Twin {
        ONE("same"), TWO("same");

        private final String label;

        Twin(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum Scored implements Enumerable<Integer> {
        LOW(1), HIGH(2);

        private final int id;

        Scored(int id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }

    @Test
    public void plainEnumNameLookupBackwardCompatible() {
        assertSame(Plain.SPRING, EnumAide.ofName(Plain.class, "SPRING"));
        assertSame(Plain.SUMMER, EnumAide.ofName(Plain.class, "SUMMER"));
        assertSame(Plain.SPRING, EnumAide.checkOfName(Plain.class, "SPRING"));
    }

    @Test
    public void constantNameWinsOverOverriddenToString() {
        assertSame(Talkative.ALPHA, EnumAide.ofName(Talkative.class, "ALPHA"), "常量名查找必须命中，显示文本不得劫持");
        assertSame(Talkative.BETA, EnumAide.ofName(Talkative.class, "BETA"));
        assertNull(EnumAide.ofName(Talkative.class, "first"), "显示文本不再是合法名字（查找域收窄为常量名）");
        assertThrows(NullPointerException.class, () -> EnumAide.checkOfName(Talkative.class, "first"));
    }

    @Test
    public void sameDisplayTextConstantsRemainIndependent() {
        assertSame(Twin.ONE, EnumAide.ofName(Twin.class, "ONE"), "同显示文本常量各自按名独立寻回");
        assertSame(Twin.TWO, EnumAide.ofName(Twin.class, "TWO"));
    }

    @Test
    public void unknownNameTwoEntriesDistinctSemantics() {
        assertNull(EnumAide.ofName(Plain.class, "NOPE"), "宽松入口返回空不抛");
        NullPointerException thrown = assertThrows(NullPointerException.class,
                                                   () -> EnumAide.checkOfName(Plain.class, "NOPE"));
        assertTrue(thrown.getMessage().contains("NOPE"), "严格入口失败信息含非法名字");
        assertTrue(thrown.getMessage().contains("Plain"), "严格入口失败信息含枚举类型");
    }

    @Test
    public void nonEnumClassStillReturnsNull() {
        assertNull(EnumAide.ofName(String.class, "anything"));
    }

    @Test
    public void objectAideEnumConvertChainRegression() {
        assertEquals(Talkative.ALPHA,
                     ObjectAide.convertTo("ALPHA", new ReferenceType<Talkative>() {
                     }), "字符串按常量名转枚举（enumConvert 走 ofName 名索引）");
        assertEquals(Scored.HIGH,
                     ObjectAide.convertTo(Integer.valueOf(2), new ReferenceType<Scored>() {
                     }), "Enumerable 按 ID 转换回归不变");
        assertSame(Plain.SPRING,
                   ObjectAide.convertTo(Plain.SPRING, new ReferenceType<Plain>() {
                   }));
    }

}
