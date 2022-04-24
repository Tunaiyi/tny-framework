package com.tny.game.basics.item.loader.jackson.yaml;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.basics.item.loader.jackson.*;
import com.tny.game.expr.*;

/**
 * 模型加载器
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:38
 **/
public class YamlModelLoader<M extends Model> extends JacksonModelLoader<M> {

    public YamlModelLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler, ExprHolderFactory exprHolderFactory) {
        super(modelClass, loadHandler, exprHolderFactory, new YAMLFactory());
    }

    @Override
    protected String getFileType() {
        return "yaml";
    }

}
