package com.tny.game.base.item.xml;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Created by Kun Yang on 2017/5/19.
 */
public interface XMLConverter extends SingleValueConverter {

    @Override
    default String toString(Object obj) {
        return null;
    }

    @Override
    default boolean canConvert(Class type) {
        return true;
    }

}
