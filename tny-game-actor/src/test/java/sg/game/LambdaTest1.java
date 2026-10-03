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

package sg.game;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/2/21.
 */
public class LambdaTest1 {

    static Consumer<String> temp = null;

    private static void test(Consumer<String> old) {
        Consumer<String> ld = (s) -> System.out.println(s);
        if (old == null) {
            temp = ld;
        } else {
            System.out.println(ld == old);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Consumer<String> ld = System.out::println;
        ld.accept("abc");
        for (int i = 0; i < 4; i++)
            test(temp);
        System.out.println("@@@@@@@@");

        Consumer<String> old = null;
        int index = 0;
        while (true) {
            Consumer<String> ld1 = (s) -> System.out.println(s);
            if (old == null) {
                old = ld1;
            }
            System.out.println(old == ld1);
            if (index++ == 4) {
                break;
            }
            ld1.accept("aa");
        }
    }

}
