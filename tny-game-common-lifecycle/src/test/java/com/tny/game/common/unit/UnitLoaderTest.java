///*
// * Copyright (c) 2020 Tunaiyi
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.tny.game.common.unit;
//
//import com.tny.game.common.lifecycle.unit.*;
//import com.tny.game.test.*;
//import org.junit.jupiter.api.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * <p>
// *
// * @author Kun Yang
// * @date 2018-10-25 15:03
// */
//public class UnitLoaderTest {
//
//    @Test
//    public void register() {
//        String unitName;
//        // 注册实现带有 UnitInterface 的Class
//        UnitLoader.register(new TestUnitImpl());
//        unitName = TestUnitImpl.class.getSimpleName();
//        assertTrue(UnitLoader.getLoader(TestUnitInterface1.class).getUnit(unitName).isPresent());
//        assertTrue(UnitLoader.getLoader(TestUnitInterface2.class).getUnit(unitName).isPresent());
//        assertFalse(UnitLoader.getLoader(TestUnitInterface3.class).getUnit(unitName).isPresent());
//        // 注册继承带有 UnitInterface 的Class
//        unitName = TestDefaultUnit.class.getSimpleName();
//        UnitLoader.register(new TestDefaultUnit());
//        assertTrue(UnitLoader.getLoader(TestAbstractUnit.class).getUnit(unitName).isPresent());
//        assertTrue(UnitLoader.getLoader(TestUnitSubInterface.class).getUnit(unitName).isPresent());
//        assertTrue(UnitLoader.getLoader(TestUnitSuperInterface.class).getUnit(unitName).isPresent());
//        assertFalse(UnitLoader.getLoader(TestNormalInterlace.class).getUnit(unitName).isPresent());
//
//        // 重复注册
//        TestAide.assertRunWithException(() -> UnitLoader.register(new TestUnitImpl()), IllegalArgumentException.class);
//        // 没有继承标识UnitInterface 的类或接口, 并且 Unit 没有设置注册的UnitInterface
//        TestAide.assertRunWithException(() -> UnitLoader.register(new TestNormalWithoutSetUnitInterfaces()), IllegalArgumentException.class);
//        // 通过Unit 设置 UnitInterface 但类没有实现或继承该UnitInterface
//        TestAide.assertRunWithException(() -> UnitLoader.register(new TestUnimplWithSetUnitInterfaces()), ClassCastException.class);
//
//        // 通过Unit 设置 UnitInterface
//        unitName = TestNormalWithSetUnitInterfaces.class.getSimpleName();
//        UnitLoader.register(new TestNormalWithSetUnitInterfaces());
//        assertTrue(UnitLoader.getLoader(TestNormalInterlace.class).getUnit(unitName).isPresent());
//        assertTrue(UnitLoader.getLoader(TestNormalInterlace1.class).getUnit(unitName).isPresent());
//        assertFalse(UnitLoader.getLoader(TestNormalInterlace2.class).getUnit(unitName).isPresent());
//
//        TestAide.assertRunWithException(() -> UnitLoader.getLoader(TestUnitInterface1.class).checkUnit("233"), NullPointerException.class);
//    }
//
//}