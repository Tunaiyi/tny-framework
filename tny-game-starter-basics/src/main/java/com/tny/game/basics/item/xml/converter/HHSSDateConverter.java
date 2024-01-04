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

package com.tny.game.basics.item.xml.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.text.*;
import java.util.Date;

public class HHSSDateConverter extends AbstractSingleValueConverter {

    private final DateFormat format = new SimpleDateFormat("HH:mm");

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class clazz) {
        return clazz.isAssignableFrom(Date.class);
    }

    @Override
    public Object fromString(String value) {
        try {
            return this.format.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
