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

import com.tny.game.common.event.annotation.*;

@GlobalEventListener
public class Counter {

    private double number;

    private double num1;

    private double num2;

    public Counter(double number, double num1, double num2) {
        super();
        this.number = number;
        this.num1 = num1;
        this.num2 = num2;
    }

    public double count(int time, long size) {
        return (this.number - this.num1 + this.num2) * time / size;
    }

}
