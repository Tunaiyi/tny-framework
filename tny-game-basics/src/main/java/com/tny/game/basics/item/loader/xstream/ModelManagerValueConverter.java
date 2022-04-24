package com.tny.game.basics.item.loader.xstream;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.basics.item.xml.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 03:00
 **/
public class ModelManagerValueConverter implements SingleValueConverter {

    private final LoadableModelManager<?> modelManager;

    public static ModelManagerValueConverter of(LoadableModelManager<?> modelManager) {
        return new ModelManagerValueConverter(modelManager);
    }

    private ModelManagerValueConverter(LoadableModelManager<?> modelManager) {
        this.modelManager = modelManager;
    }

    @Override
    public String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    @Override
    public Object fromString(String str) {
        return modelManager.getAndCheckModelByAlias(str);
    }

    @Override
    public boolean canConvert(Class type) {
        return modelManager.getModelClass().isAssignableFrom(type);
    }

}
