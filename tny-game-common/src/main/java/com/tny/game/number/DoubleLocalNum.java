package com.tny.game.number;

import com.tny.game.common.ExceptionUtils;

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
        ExceptionUtils.checkNotNull(number, "number is null");
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
        return this.number = NumberUtils.become(NumberUtils.add(this.number, num.getNumber()), this.number);
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
        return this.number = NumberUtils.become(NumberUtils.sub(this.number, num.getNumber()), this.number);
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
        return this.number = NumberUtils.become(NumberUtils.multiply(this.number, num.getNumber()), this.number);
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
        return this.number = NumberUtils.become(NumberUtils.divide(this.number, num.getNumber()), this.number);
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
        return this.number = NumberUtils.become(NumberUtils.mod(this.number, num.getNumber()), this.number);
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
        return (float) number;
    }

    @Override
    public double doubleValue() {
        return number;
    }

}
