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

package com.tny.game.common.aop;

import com.tny.game.common.reflect.aop.annotation.*;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Player extends Person {

    public Player() {

    }

    public void callProtected() {
        this.protectedMethod();
    }

    @AOP
    protected void protectedMethod() {
        System.out.println("protectedMethod");
    }

    @AOP
    public void tryException() throws Exception {
        throw new NullPointerException();
    }

    @AOP
    public void callName() {
        System.out.println("void callName()");
    }

    @AOP
    public int getName() {
        System.out.println("int getName()");
        return 10;
    }

    int testPackage() {
        System.out.println("int testPackage()");
        return 10;
    }

    protected int testProtected(int value) {
        System.out.println("int testProtected()" + value);
        return 10;
    }

    @AOP
    public Player friend(int data, Player player, long value) {
        System.out.println("Player friend(int data, Player player) ");
        return null;
    }

    public static void main(String[] args) {
        var queue =new ConcurrentLinkedQueue<String>();
        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");
        queue.add("6");
        for (Object o : queue) {
            if (o.equals("3")) {
                queue.remove(o);
            }
            if (o.equals("2")) {
                queue.remove("4");
            }
            System.out.println(o);
        }
        System.out.println("============");
        for (Object o : queue) {
            System.out.println(o);
        }
    }

    // public static void main(String[] args) {
    //     // TreeSet<DateTime> date = new TreeSet<>();
    //     // date.add(DateTime.now());
    //     // date.add(DateTime.now().plusDays(1));
    //     // date.add(DateTime.now().plusDays(2));
    //     // date.add(DateTime.now().plusDays(3));
    //     // date.stream().map(d -> d.toString(DateTimeHelper.DATE_TIME_MIN_FORMAT))
    //     //         .forEach(System.out::println);
    //
    //     String reg = " ~= '(.*return .*)|[{(.\\\"\\')].+'";
    //     String[] fxs = {
    //             "return { abc }",
    //             "{ abc }",
    //             "{ abc",
    //             "abc return ",
    //             "(abc",
    //             "\"abc\"",
    //             "'abc'",
    //             "return abc",
    //     };
    //     for (String fx : fxs) {
    //         System.out.println(fx + " = " + MvelFormulaFactory.create("fx " + reg, FormulaType.EXPRESSION)
    //                 .createFormula()
    //                 .put("fx", fx)
    //                 .execute(Object.class));
    //     }
    // }

}
