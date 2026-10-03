/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
