/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.number;

import com.tny.game.common.utils.*;

/**
 * 本地变量
 * Created by Kun Yang on 16/2/21.
 */
public class LongLocalNum extends Number {

    private volatile long number;

    public LongLocalNum(long number) {
        this.number = number;
    }

    public void set(long number) {
        Asserts.checkNotNull(number, "number is null");
        this.number = number;
    }

    public long add(int num) {
        return this.number += num;
    }

    public long add(long num) {
        return this.number += num;
    }

    public long add(float num) {
        return this.number += num;
    }

    public long add(double num) {
        return this.number += num;
    }

    public long add(short num) {
        return this.number += num;
    }

    public long add(byte num) {
        return this.number += num;
    }

    public long add(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num.getNumber()), this.number);
    }

    public long sub(int num) {
        return this.number -= num;
    }

    public long sub(long num) {
        return this.number -= num;
    }

    public long sub(float num) {
        return this.number -= num;
    }

    public long sub(double num) {
        return this.number -= num;
    }

    public long sub(short num) {
        return this.number -= num;
    }

    public long sub(byte num) {
        return this.number -= num;
    }

    public long sub(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.sub(this.number, num.getNumber()), this.number);
    }

    public long multiply(int num) {
        return this.number *= num;
    }

    public long multiply(long num) {
        return this.number *= num;
    }

    public long multiply(float num) {
        return this.number *= num;
    }

    public long multiply(double num) {
        return this.number *= num;
    }

    public long multiply(short num) {
        return this.number *= num;
    }

    public long multiply(byte num) {
        return this.number *= num;
    }

    public long multiply(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.multiply(this.number, num.getNumber()), this.number);
    }

    public long divide(int num) {
        return this.number /= num;
    }

    public long divide(long num) {
        return this.number /= num;
    }

    public long divide(float num) {
        return this.number /= num;
    }

    public long divide(double num) {
        return this.number /= num;
    }

    public long divide(short num) {
        return this.number /= num;
    }

    public long divide(byte num) {
        return this.number /= num;
    }

    public long divide(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.divide(this.number, num.getNumber()), this.number);
    }

    public long mod(int num) {
        return this.number %= num;
    }

    public long mod(long num) {
        return this.number %= num;
    }

    public long mod(float num) {
        return this.number %= num;
    }

    public long mod(double num) {
        return this.number %= num;
    }

    public long mod(short num) {
        return this.number %= num;
    }

    public long mod(byte num) {
        return this.number %= num;
    }

    public long mod(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.mod(this.number, num.getNumber()), this.number);
    }

    @Override
    public int intValue() {
        return (int)this.number;
    }

    @Override
    public long longValue() {
        return this.number;
    }

    @Override
    public float floatValue() {
        return this.number;
    }

    @Override
    public double doubleValue() {
        return (double)this.number;
    }

}
