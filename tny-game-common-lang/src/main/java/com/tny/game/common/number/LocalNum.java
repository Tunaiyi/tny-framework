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

package com.tny.game.common.number;

import com.tny.game.common.utils.*;

/**
 * 本地变量
 * Created by Kun Yang on 16/2/21.
 */
public class LocalNum<N extends Number> extends Number {

    private volatile N number;

    public LocalNum(N number) {
        this.number = number;
    }

    public void set(N number) {
        Asserts.checkNotNull(number, "number is null");
        this.number = number;
    }

    public N add(int num) {
        int value = this.number.intValue() + num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N add(long num) {
        long value = this.number.longValue() + num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N add(float num) {
        float value = this.number.floatValue() + num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N add(double num) {
        double value = this.number.doubleValue() + num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N add(short num) {
        // 存储值保持原精度、参数加宽运算（原实现先把存储值截断到窄宽度）
        return this.add((long) num);
    }

    public N add(byte num) {
        return this.add((long) num);
    }

    public N add(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num.number), this.number);
    }

    public N add(Number num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num), this.number);
    }

    public N sub(int num) {
        int value = this.number.intValue() - num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N sub(long num) {
        long value = this.number.longValue() - num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N sub(float num) {
        float value = this.number.floatValue() - num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N sub(double num) {
        double value = this.number.doubleValue() - num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N sub(short num) {
        return this.sub((long) num);
    }

    public N sub(byte num) {
        return this.sub((long) num);
    }

    public N sub(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.sub(this.number, num.number), this.number);
    }

    public N multiply(int num) {
        int value = this.number.intValue() * num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N multiply(long num) {
        long value = this.number.longValue() * num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N multiply(float num) {
        float value = this.number.floatValue() * num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N multiply(double num) {
        double value = this.number.doubleValue() * num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N multiply(short num) {
        return this.multiply((long) num);
    }

    public N multiply(byte num) {
        return this.multiply((long) num);
    }

    public N multiply(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.multiply(this.number, num.number), this.number);
    }

    public N divide(int num) {
        int value = this.number.intValue() / num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N divide(long num) {
        long value = this.number.longValue() / num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N divide(float num) {
        float value = this.number.floatValue() / num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N divide(double num) {
        double value = this.number.doubleValue() / num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N divide(short num) {
        return this.divide((long) num);
    }

    public N divide(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.divide(this.number, num.number), this.number);
    }

    public N divide(byte num) {
        return this.divide((long) num);
    }

    public N mod(int num) {
        int value = this.number.intValue() % num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N mod(long num) {
        long value = this.number.longValue() % num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N mod(float num) {
        float value = this.number.floatValue() % num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N mod(double num) {
        double value = this.number.doubleValue() % num;
        return this.number = NumberAide.as(value, this.number);
    }

    public N mod(short num) {
        return this.mod((long) num);
    }

    public N mod(byte num) {
        return this.mod((long) num);
    }

    public N mod(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.mod(this.number, num.number), this.number);
    }

    public N getNumber() {
        return this.number;
    }

    @Override
    public int intValue() {
        return this.number.intValue();
    }

    @Override
    public long longValue() {
        return this.number.longValue();
    }

    @Override
    public float floatValue() {
        return this.number.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.number.doubleValue();
    }

}
