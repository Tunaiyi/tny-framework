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

package com.tny.game.common.reflect;

public class FormulaTest {

    // @Test
    // public void testNomal() {
    //     int data = 0;
    //     long time = System.currentTimeMillis();
    //     System.out.println();
    //     for (int i = 0; i < 1000000; i++) {
    //         FPlayer player = new FPlayer(i);
    //         data = player.getLevel() * 30 / 20 + player.getLevel() * 31;
    //     }
    //     System.out.println(data + "  " + (System.currentTimeMillis() - time));
    // }
    //
    // @Test
    // public void testFomual() {
    //     FormulaHolder formulaHolder = MvelFormulaFactory.create("player.int * 30 / 20 + player.int * 31", FormulaType.EXPRESSION);
    //     int data = 0;
    //     long time = System.currentTimeMillis();
    //     for (int i = 0; i < 1000000; i++) {
    //         data = formulaHolder.createFormula().put("player", new FPlayer(i)).execute(Integer.class);
    //     }
    //     System.out.println(data + "  " + (System.currentTimeMillis() - time));
    // }
}
