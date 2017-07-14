package com.tny.game.common.number;

import com.tny.game.common.utils.Throws;

/**
 * 本地变量
 * Created by Kun Yang on 16/2/21.
 */
public class ShortLocalNum extends Number {

    private volatile short number;

    public ShortLocalNum(short number) {
        this.number = number;
    }

    public void set(short number) {
        Throws.checkNotNull(number, "number is null");
        this.number = number;
    }

    public short add(int num) {
        return this.number += num;
    }

    public short add(long num) {
        return this.number += num;
    }

    public short add(float num) {
        return this.number += num;
    }

    public short add(double num) {
        return this.number += num;
    }

    public short add(short num) {
        return this.number += num;
    }

    public short add(byte num) {
        return this.number += num;
    }

    public short add(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.add(this.number, num.getNumber()), this.number);
    }

    public short sub(int num) {
        return this.number -= num;
    }

    public short sub(long num) {
        return this.number -= num;
    }

    public short sub(float num) {
        return this.number -= num;
    }

    public short sub(double num) {
        return this.number -= num;
    }

    public short sub(short num) {
        return this.number -= num;
    }

    public short sub(byte num) {
        return this.number -= num;
    }

    public short sub(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.sub(this.number, num.getNumber()), this.number);
    }

    public short multiply(int num) {
        return this.number *= num;
    }

    public short multiply(long num) {
        return this.number *= num;
    }

    public short multiply(float num) {
        return this.number *= num;
    }

    public short multiply(double num) {
        return this.number *= num;
    }

    public short multiply(short num) {
        return this.number *= num;
    }

    public short multiply(byte num) {
        return this.number *= num;
    }

    public short multiply(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.multiply(this.number, num.getNumber()), this.number);
    }

    public short divide(int num) {
        return this.number /= num;
    }

    public short divide(long num) {
        return this.number /= num;
    }

    public short divide(float num) {
        return this.number /= num;
    }

    public short divide(double num) {
        return this.number /= num;
    }

    public short divide(short num) {
        return this.number /= num;
    }

    public short divide(byte num) {
        return this.number /= num;
    }

    public short divide(LocalNum<?> num) {
        return this.number = NumberAide.as(NumberAide.divide(this.number, num.getNumber()), this.number);
    }

    public short mod(int num) {
        return this.number %= num;
    }

    public short mod(long num) {
        return this.number %= num;
    }

    public short mod(float num) {
        return this.number %= num;
    }

    public short mod(double num) {
        return this.number %= num;
    }

    public short mod(short num) {
        return this.number %= num;
    }

    public short mod(byte num) {
        return this.number %= num;
    }

    public short mod(LocalNum<?> num) {
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
