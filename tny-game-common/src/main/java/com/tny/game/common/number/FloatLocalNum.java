package com.tny.game.common.number;

import com.tny.game.common.utils.*;

/**
 * 本地变量
 * Created by Kun Yang on 16/2/21.
 */
public class FloatLocalNum extends Number {

    private volatile float number;

    public FloatLocalNum(int number) {
        this.number = number;
    }

    public void set(float number) {
        Throws.checkNotNull(number, "number is null");
        this.number = number;
    }

    public float add(int num) {
        return this.number += num;
    }

    public float add(long num) {
        return this.number += num;
    }

    public float add(float num) {
        return this.number += num;
    }

    public float add(double num) {
        return this.number += num;
    }

    public float add(short num) {
        return this.number += num;
    }

    public float add(byte num) {
        return this.number += num;
    }

    public float add(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num.getNumber()), this.number);
    }

    public float sub(int num) {
        return this.number -= num;
    }

    public float sub(long num) {
        return this.number -= num;
    }

    public float sub(float num) {
        return this.number -= num;
    }

    public float sub(double num) {
        return this.number -= num;
    }

    public float sub(short num) {
        return this.number -= num;
    }

    public float sub(byte num) {
        return this.number -= num;
    }

    public float sub(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.sub(this.number, num.getNumber()), this.number);
    }

    public float multiply(int num) {
        return this.number *= num;
    }

    public float multiply(long num) {
        return this.number *= num;
    }

    public float multiply(float num) {
        return this.number *= num;
    }

    public float multiply(double num) {
        return this.number *= num;
    }

    public float multiply(short num) {
        return this.number *= num;
    }

    public float multiply(byte num) {
        return this.number *= num;
    }

    public float multiply(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.multiply(this.number, num.getNumber()), this.number);
    }

    public float divide(int num) {
        return this.number /= num;
    }

    public float divide(long num) {
        return this.number /= num;
    }

    public float divide(float num) {
        return this.number /= num;
    }

    public float divide(double num) {
        return this.number /= num;
    }

    public float divide(short num) {
        return this.number /= num;
    }

    public float divide(byte num) {
        return this.number /= num;
    }

    public float divide(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.divide(this.number, num.getNumber()), this.number);
    }

    public float mod(int num) {
        return this.number %= num;
    }

    public float mod(long num) {
        return this.number %= num;
    }

    public float mod(float num) {
        return this.number %= num;
    }

    public float mod(double num) {
        return this.number %= num;
    }

    public float mod(short num) {
        return this.number %= num;
    }

    public float mod(byte num) {
        return this.number %= num;
    }

    public float mod(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.mod(this.number, num.getNumber()), this.number);
    }

    @Override
    public int intValue() {
        return (int) number;
    }

    @Override
    public long longValue() {
        return (long) number;
    }

    @Override
    public float floatValue() {
        return number;
    }

    @Override
    public double doubleValue() {
        return (double) number;
    }

}
