package com.tny.game.suite.base.converter;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.common.scheduler.cycle.CronTimeCycle;

import java.text.ParseException;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/7/23.
 */
public class CronTimeCycleConverter implements SingleValueConverter {

    @Override
    public String toString(Object obj) {
        CronTimeCycle cycle = as(obj);
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
