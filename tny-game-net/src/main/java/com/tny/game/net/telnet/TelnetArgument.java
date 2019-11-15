package com.tny.game.net.telnet;

import java.util.*;

public class TelnetArgument {

    private List<String> argumentList;

    public TelnetArgument(String[] args) {
        this.argumentList = new ArrayList<>(args.length);
        int index = 0;
        for (String arg : args) {
            if (index++ != 0)
                this.argumentList.add(arg);
        }
        this.argumentList = Collections.unmodifiableList(this.argumentList);
    }

    public int size() {
        return argumentList.size();
    }

    public String getStr(int index) {
        String value = argumentList.get(index);
        if (value != null)
            return value;
        return null;
    }

    public String getStr(int index, String defValue) {
        String value = argumentList.get(index);
        return value == null ? defValue : value;
    }

    public int getInt(int index) {
        String value = argumentList.get(index);
        if (value == null)
            return 0;
        return Integer.parseInt(value);
    }

    public int getInt(int index, int defValue) {
        String value = argumentList.get(index);
        if (value == null)
            return defValue;
        return Integer.parseInt(value);
    }

    public long getLong(int index) {
        String value = argumentList.get(index);
        if (value == null)
            return 0;
        return Long.parseLong(value);
    }

    public long getLong(int index, long defValue) {
        String value = argumentList.get(index);
        if (value == null)
            return defValue;
        return Long.parseLong(value);
    }

    public double getDouble(int index) {
        String value = argumentList.get(index);
        if (value == null)
            return 0.0;
        return Double.parseDouble(value);
    }

    public double getDouble(int index, double defValue) {
        String value = argumentList.get(index);
        if (value == null)
            return defValue;
        return Double.parseDouble(value);
    }

    public float getFloat(int index) {
        String value = argumentList.get(index);
        if (value == null)
            return 0.0F;
        return Float.parseFloat(value);
    }

    public float getFloat(int index, float defValue) {
        String value = argumentList.get(index);
        if (value == null)
            return defValue;
        return Float.parseFloat(value);
    }

    public boolean getBoolean(int index) {
        String value = argumentList.get(index);
        if (value == null)
            return false;
        return Boolean.parseBoolean(value);
    }

    public boolean getBoolean(int index, boolean defValue) {
        String value = argumentList.get(index);
        if (value == null)
            return defValue;
        return Boolean.parseBoolean(value);
    }

    public byte getByte(int index) {
        String value = argumentList.get(index);
        if (value == null)
            return 0;
        return Byte.parseByte(value);
    }

    public byte getByte(int index, byte defValue) {
        String value = argumentList.get(index);
        if (value == null)
            return defValue;
        return Byte.parseByte(value);
    }

    public List<String> getArgumentList() {
        return argumentList;
    }

}
