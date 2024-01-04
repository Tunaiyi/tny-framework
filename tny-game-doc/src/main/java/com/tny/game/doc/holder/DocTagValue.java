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

package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/17 02:52
 **/
public class DocTagValue {

    private final String tag;

    private final String value;

    private final boolean empty;

    public DocTagValue(DocTag tag) {
        this.tag = tag.tag();
        this.value = tag.value();
        this.empty = StringUtils.isBlank(value);
    }

    public String getTag() {
        return tag;
    }

    public String getValue() {
        return value;
    }

    public String getValue(String defaultValue) {
        return isEmpty() ? defaultValue : value;
    }

    public boolean matchTag(String tag) {
        return Objects.equals(tag, this.tag);
    }

    public boolean matchTag(Collection<String> tags) {
        return tags.contains(this.tag);
    }

    public boolean match(String tag, Object value) {
        return Objects.equals(tag, this.tag) && this.isEmpty() || Objects.equals(String.valueOf(value), this.value);
    }

    public boolean isEmpty() {
        return empty;
    }

    public byte getByte() {
        return getByte((byte) 0);
    }

    public byte getByte(byte defaultValue) {
        return isEmpty() ? defaultValue : Byte.parseByte(value);
    }

    public short getShort() {
        return getShort((short) 0);
    }

    public short getShort(short defaultValue) {
        return isEmpty() ? defaultValue : Short.parseShort(value);
    }

    public int getInt() {
        return getInt(0);
    }

    public int getInt(int defaultValue) {
        return isEmpty() ? defaultValue : Integer.parseInt(value);
    }

    public long getLong() {
        return getLong(0);
    }

    public long getLong(int defaultValue) {
        return isEmpty() ? defaultValue : Long.parseLong(value);
    }

    public float getFloat() {
        return getFloat(0);
    }

    public float getFloat(float defaultValue) {
        return isEmpty() ? defaultValue : Float.parseFloat(value);
    }

    public double getDouble() {
        return getFloat(0);
    }

    public double getDouble(float defaultValue) {
        return isEmpty() ? defaultValue : Double.parseDouble(value);
    }

    public boolean getBoolean() {
        return getBoolean(false);
    }

    public boolean getBoolean(boolean defaultValue) {
        return isEmpty() ? defaultValue : Boolean.parseBoolean(value);
    }

}
