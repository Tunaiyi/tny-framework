package com.tny.game.basics.item.loader.jackson.yaml;

import com.fasterxml.jackson.core.JsonFactory;
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
public class JsonModelLoader<M extends Model> extends JacksonModelLoader<M> {

    public JsonModelLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler, ExprHolderFactory exprHolderFactory) {
        super(modelClass, loadHandler, exprHolderFactory, new JsonFactory());
    }

    @Override
    protected String getFileType() {
        return "json";
    }

}
