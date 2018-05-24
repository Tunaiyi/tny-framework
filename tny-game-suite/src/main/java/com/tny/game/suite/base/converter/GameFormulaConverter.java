package com.tny.game.suite.base.converter;

import com.tny.game.base.converter.MvelFormulaConverter;
import com.tny.game.expr.FormulaType;

import java.util.Map;

public class GameFormulaConverter extends MvelFormulaConverter {

    public GameFormulaConverter(Map<String, Object> map) {
        super(FormulaType.EXPRESSION);
        this.formulaContext.putAll(map);
        //必须在最后!!
        this.init();
    }
}
