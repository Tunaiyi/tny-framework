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

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.common.scheduler.cycle.*;
import com.tny.game.common.utils.*;

import java.text.ParseException;

/**
 * Created by Kun Yang on 2017/7/23.
 */
public class CronTimeCycleConverter implements SingleValueConverter {

    @Override
    public String toString(Object obj) {
        CronTimeCycle cycle = ObjectAide.as(obj);
        return cycle.getExpression().getCronExpression();
    }

    @Override
    public Object fromString(String str) {
        try {
            return CronTimeCycle.of(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean canConvert(Class type) {
        return CronTimeCycle.class.isAssignableFrom(type);
    }

}
