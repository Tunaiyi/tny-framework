/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class OwnerTest {

    List<Test> testList = new ArrayList<>();

    public void doSay(Object number) {
        for (Test test : this.testList) {
            test.say(number);
        }
    }

    public static void main(String[] args) {
        OwnerTest ownerTest = new OwnerTest();
        ownerTest.testList.add(new Test<>(1000L));
        ownerTest.testList.add(new Test<>(1000));
        ownerTest.testList.add(new Test<>("1000"));

        ownerTest.doSay(100);
    }

}
