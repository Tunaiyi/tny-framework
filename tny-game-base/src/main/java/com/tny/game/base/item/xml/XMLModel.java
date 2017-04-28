package com.tny.game.base.item.xml;

import com.google.common.collect.ImmutableSet;
import com.tny.game.base.item.Model;

import java.util.Set;

/**
 * 抽象xml映射事物模型
 *
 * @author KGTny
 */
public abstract class XMLModel implements Model {

    protected boolean init = false;

    protected Set<Object> tags;

    protected String currentFormula(String alias) {
        return null;
    }

    protected void init() {
        if (init) {
            return;
        }
        this.doInit();
        init = true;
        if (this.tags == null)
            this.tags = ImmutableSet.of();
    }

    @Override
    public Set<Object> tags() {
        return tags;
    }

    protected abstract void doInit();

}
