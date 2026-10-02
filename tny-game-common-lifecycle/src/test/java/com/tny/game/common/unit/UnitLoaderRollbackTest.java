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
package com.tny.game.common.unit;

import com.tny.game.common.lifecycle.unit.UnitLoader;
import com.tny.game.common.lifecycle.unit.annotation.Unit;
import com.tny.game.common.lifecycle.unit.annotation.UnitInterface;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UnitLoader 修复契约：register(Object) 写入前整体预校验——
 * 同名冲突时不得留下"部分名字已注册"的脏状态（原实现边写边炸、无回滚）。
 */
class UnitLoaderRollbackTest {

    @UnitInterface
    public interface RollbackInterface {
    }

    @Unit(value = "firstRollbackUnit")
    public static class SharedNameUnit implements RollbackInterface {
    }

    /** 与 SharedNameUnit 同 simpleName、不同全名的第二个实现（嵌套在不同外部类） */
    public static class ConflictHolder {

        @Unit(value = "secondRollbackUnit")
        public static class SharedNameUnit implements RollbackInterface {
        }

    }

    @Unit(value = "unimplRollbackUnit", unitInterfaces = {RollbackInterface.class})
    public static class UnimplementedUnit {
    }

    @Test
    void duplicateSimpleNameRegistrationLeavesNoPartialState() {
        UnitLoader<RollbackInterface> loader = UnitLoader.getLoader(RollbackInterface.class);
        // 先注册第一个：三个名字（value、simpleName、fullName）全部落账
        var first = new SharedNameUnit();
        java.util.Set<String> names = UnitLoader.register(first);
        assertEquals(3, names.size());
        assertTrue(loader.getUnit("firstRollbackUnit").isPresent());
        assertTrue(loader.getUnit("SharedNameUnit").isPresent());

        // 再注册同 simpleName 的第二个：预校验必须在任何写入前失败
        var second = new ConflictHolder.SharedNameUnit();
        assertThrows(IllegalArgumentException.class, () -> UnitLoader.register(second));
        assertFalse(loader.getUnit("secondRollbackUnit").isPresent(),
                "冲突注册不得留下部分名字（原实现 valueName/fullName 已写入后 simpleName 才炸）");
        // 第一个的注册不受影响
        assertSame(first, loader.getUnit("firstRollbackUnit").orElse(null));
        assertSame(first, loader.getUnit("SharedNameUnit").orElse(null));
    }

    @Test
    void unimplementedUnitInterfaceFailsBeforeWrite() {
        UnitLoader<RollbackInterface> loader = UnitLoader.getLoader(RollbackInterface.class);
        var lying = new UnimplementedUnit();
        assertThrows(ClassCastException.class, () -> UnitLoader.register(lying));
        assertFalse(loader.getUnit("unimplRollbackUnit").isPresent(),
                "谎报 unitInterfaces 的注册不得写入任何名字");
    }

}
