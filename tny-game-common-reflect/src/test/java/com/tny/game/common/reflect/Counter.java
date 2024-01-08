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
