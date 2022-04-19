package com.tny.game.basics.item.model;

import com.google.common.collect.ImmutableSet;
import com.tny.game.basics.item.*;

import java.util.Set;

/**
 * 抽象xml映射事物模型
 *
 * @author KGTny
 */
public abstract class BaseModel<C> implements Model {

    protected boolean init = false;

    protected Set<Object> tags;

    protected String currentFormula(String alias) {
        return null;
    }

    protected void init(C context) {
        if (init) {
            return;
        }
        if (this.tags == null) {
            this.tags = ImmutableSet.of();
        }
        this.doInit(context);
        init = true;
    }

    @Override
    public Set<Object> tags() {
        return tags;
    }

    protected abstract void doInit(C context);

}
