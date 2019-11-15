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
        Throws.checkNotNull(number, "number is null");
        this.number = number;
    }

    public N add(int num) {
        int value = this.number.intValue() + num;
        return number = NumberAide.as(value, this.number);
    }

    public N add(long num) {
        long value = this.number.longValue() + num;
        return number = NumberAide.as(value, this.number);
    }

    public N add(float num) {
        float value = this.number.floatValue() + num;
        return number = NumberAide.as(value, this.number);
    }

    public N add(double num) {
        double value = this.number.doubleValue() + num;
        return number = NumberAide.as(value, this.number);
    }

    public N add(short num) {
        int value = this.number.shortValue() + num;
        return number = NumberAide.as(value, this.number);
    }

    public N add(byte num) {
        int value = this.number.byteValue() + num;
        return number = NumberAide.as(value, this.number);
    }

    public N add(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num.number), this.number);
    }

    public N add(Number num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num), this.number);
    }

    public N sub(int num) {
        int value = this.number.intValue() - num;
        return number = NumberAide.as(value, this.number);
    }

    public N sub(long num) {
        long value = this.number.longValue() - num;
        return number = NumberAide.as(value, this.number);
    }

    public N sub(float num) {
        float value = this.number.floatValue() - num;
        return number = NumberAide.as(value, this.number);
    }

    public N sub(double num) {
        double value = this.number.doubleValue() - num;
        return number = NumberAide.as(value, this.number);
    }

    public N sub(short num) {
        int value = this.number.shortValue() - num;
        return number = NumberAide.as(value, this.number);
    }

    public N sub(byte num) {
        int value = this.number.byteValue() - num;
        return number = NumberAide.as(value, this.number);
    }

    public N sub(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.sub(this.number, num.number), this.number);
    }

    public N multiply(int num) {
        int value = this.number.intValue() * num;
        return number = NumberAide.as(value, this.number);
    }

    public N multiply(long num) {
        long value = this.number.longValue() * num;
        return number = NumberAide.as(value, this.number);
    }

    public N multiply(float num) {
        float value = this.number.floatValue() * num;
        return number = NumberAide.as(value, this.number);
    }

    public N multiply(double num) {
        double value = this.number.doubleValue() * num;
        return number = NumberAide.as(value, this.number);
    }

    public N multiply(short num) {
        int value = this.number.shortValue() * num;
        return number = NumberAide.as(value, this.number);
    }

    public N multiply(byte num) {
        int value = this.number.byteValue() * num;
        return number = NumberAide.as(value, this.number);
    }

    public N multiply(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.multiply(this.number, num.number), this.number);
    }

    public N divide(int num) {
        int value = this.number.intValue() / num;
        return number = NumberAide.as(value, this.number);
    }

    public N divide(long num) {
        long value = this.number.longValue() / num;
        return number = NumberAide.as(value, this.number);
    }

    public N divide(float num) {
        float value = this.number.floatValue() / num;
        return number = NumberAide.as(value, this.number);
    }

    public N divide(double num) {
        double value = this.number.doubleValue() / num;
        return number = NumberAide.as(value, this.number);
    }

    public N divide(short num) {
        int value = this.number.shortValue() / num;
        return number = NumberAide.as(value, this.number);
    }

    public N divide(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.divide(this.number, num.number), this.number);
    }

    public N divide(byte num) {
        int value = this.number.byteValue() / num;
        return number = NumberAide.as(value, this.number);
    }

    public N mod(int num) {
        int value = this.number.intValue() % num;
        return number = NumberAide.as(value, this.number);
    }

    public N mod(long num) {
        long value = this.number.longValue() % num;
        return number = NumberAide.as(value, this.number);
    }

    public N mod(float num) {
        float value = this.number.floatValue() % num;
        return number = NumberAide.as(value, this.number);
    }

    public N mod(double num) {
        double value = this.number.doubleValue() % num;
        return number = NumberAide.as(value, this.number);
    }

    public N mod(short num) {
        int value = this.number.shortValue() % num;
        return number = NumberAide.as(value, this.number);
    }

    public N mod(byte num) {
        int value = this.number.byteValue() % num;
        return number = NumberAide.as(value, this.number);
    }

    public N mod(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.mod(this.number, num.number), this.number);
    }

    public N getNumber() {
        return number;
    }

    @Override
    public int intValue() {
        return number.intValue();
    }

    @Override
    public long longValue() {
        return number.longValue();
    }

    @Override
    public float floatValue() {
        return number.floatValue();
    }

    @Override
    public double doubleValue() {
        return number.doubleValue();
    }

}
