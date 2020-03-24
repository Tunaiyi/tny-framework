package com.tny.game.common.number;

import com.tny.game.common.utils.*;

/**
 * 本地变量
 * Created by Kun Yang on 16/2/21.
 */
public class DoubleLocalNum extends Number {

    private volatile double number;

    public DoubleLocalNum(double number) {
        this.number = number;
    }

    public void set(double number) {
        ThrowAide.checkNotNull(number, "number is null");
        this.number = number;
    }

    public double add(int num) {
        return this.number += num;
    }

    public double add(long num) {
        return this.number += num;
    }

    public double add(float num) {
        return this.number += num;
    }

    public double add(double num) {
        return this.number += num;
    }

    public double add(short num) {
        return this.number += num;
    }

    public double add(byte num) {
        return this.number += num;
    }

    public double add(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num.getNumber()), this.number);
    }

    public double sub(int num) {
        return this.number -= num;
    }

    public double sub(long num) {
        return this.number -= num;
    }

    public double sub(float num) {
        return this.number -= num;
    }

    public double sub(double num) {
        return this.number -= num;
    }

    public double sub(short num) {
        return this.number -= num;
    }

    public double sub(byte num) {
        return this.number -= num;
    }

    public double sub(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.sub(this.number, num.getNumber()), this.number);
    }

    public double multiply(int num) {
        return this.number *= num;
    }

    public double multiply(long num) {
        return this.number *= num;
    }

    public double multiply(float num) {
        return this.number *= num;
    }

    public double multiply(double num) {
        return this.number *= num;
    }

    public double multiply(short num) {
        return this.number *= num;
    }

    public double multiply(byte num) {
        return this.number *= num;
    }

    public double multiply(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.multiply(this.number, num.getNumber()), this.number);
    }

    public double divide(int num) {
        return this.number /= num;
    }

    public double divide(long num) {
        return this.number /= num;
    }

    public double divide(float num) {
        return this.number /= num;
    }

    public double divide(double num) {
        return this.number /= num;
    }

    public double divide(short num) {
        return this.number /= num;
    }

    public double divide(byte num) {
        return this.number /= num;
    }

    public double divide(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.divide(this.number, num.getNumber()), this.number);
    }

    public double mod(int num) {
        return this.number %= num;
    }

    public double mod(long num) {
        return this.number %= num;
    }

    public double mod(float num) {
        return this.number %= num;
    }

    public double mod(double num) {
        return this.number %= num;
    }

    public double mod(short num) {
        return this.number %= num;
    }

    public double mod(byte num) {
        return this.number %= num;
    }

    public double mod(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.mod(this.number, num.getNumber()), this.number);
    }

    @Override
    public int intValue() {
        return (int) this.number;
    }

    @Override
    public long longValue() {
        return (long) this.number;
    }

    @Override
    public float floatValue() {
        return (float) this.number;
    }

    @Override
    public double doubleValue() {
        return this.number;
    }

}
