package com.tny.game.common.number;

import com.tny.game.common.utils.*;

/**
 * 本地变量
 * Created by Kun Yang on 16/2/21.
 */
public class IntLocalNum extends Number {

    private volatile int number;

    public IntLocalNum(int number) {
        this.number = number;
    }

    public void set(int number) {
        Asserts.checkNotNull(number, "number is null");
        this.number = number;
    }

    public int add(int num) {
        return this.number += num;
    }

    public int add(long num) {
        return this.number += num;
    }

    public int add(float num) {
        return this.number += num;
    }

    public int add(double num) {
        return this.number += num;
    }

    public int add(short num) {
        return this.number += num;
    }

    public int add(byte num) {
        return this.number += num;
    }

    public int add(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num.getNumber()), this.number);
    }

    public int sub(int num) {
        return this.number -= num;
    }

    public int sub(long num) {
        return this.number -= num;
    }

    public int sub(float num) {
        return this.number -= num;
    }

    public int sub(double num) {
        return this.number -= num;
    }

    public int sub(short num) {
        return this.number -= num;
    }

    public int sub(byte num) {
        return this.number -= num;
    }

    public int sub(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.sub(this.number, num.getNumber()), this.number);
    }

    public int multiply(int num) {
        return this.number *= num;
    }

    public int multiply(long num) {
        return this.number *= num;
    }

    public int multiply(float num) {
        return this.number *= num;
    }

    public int multiply(double num) {
        return this.number *= num;
    }

    public int multiply(short num) {
        return this.number *= num;
    }

    public int multiply(byte num) {
        return this.number *= num;
    }

    public int multiply(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.multiply(this.number, num.getNumber()), this.number);
    }

    public int divide(int num) {
        return this.number /= num;
    }

    public int divide(long num) {
        return this.number /= num;
    }

    public int divide(float num) {
        return this.number /= num;
    }

    public int divide(double num) {
        return this.number /= num;
    }

    public int divide(short num) {
        return this.number /= num;
    }

    public int divide(byte num) {
        return this.number /= num;
    }

    public int divide(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.divide(this.number, num.getNumber()), this.number);
    }

    public int mod(int num) {
        return this.number %= num;
    }

    public int mod(long num) {
        return this.number %= num;
    }

    public int mod(float num) {
        return this.number %= num;
    }

    public int mod(double num) {
        return this.number %= num;
    }

    public int mod(short num) {
        return this.number %= num;
    }

    public int mod(byte num) {
        return this.number %= num;
    }

    public int mod(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.mod(this.number, num.getNumber()), this.number);
    }

    @Override
    public int intValue() {
        return this.number;
    }

    @Override
    public long longValue() {
        return (long)this.number;
    }

    @Override
    public float floatValue() {
        return (float)this.number;
    }

    @Override
    public double doubleValue() {
        return (double)this.number;
    }

}
