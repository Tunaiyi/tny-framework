package com.tny.game.base.item.xml;

import com.tny.game.base.item.Model;

/**
 * 抽象xml映射事物模型
 *
 * @author KGTny
 */
public abstract class XMLModel implements Model {

    protected boolean init = false;

    protected String currentFormula(String alias) {
        return null;
    }

    protected void init() {
        if (init) {
            return;
        }
        this.doInit();
        init = true;
    }

    protected abstract void doInit();

}
