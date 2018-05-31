package com.tny.game.expr.groovy;

import com.tny.game.expr.FormulaHolder;
import com.tny.game.expr.jsr223.ScriptFormulaFactory;

import java.util.Map;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class GroovyFormulaFactory extends ScriptFormulaFactory {

    private static final String LAN = "groovy";

    private static volatile GroovyFormulaFactory factory;

    private GroovyFormulaFactory() {
        super(LAN, new GroovyImportContext());
    }

    public static GroovyFormulaFactory factory() {
        if (factory != null)
            return factory;
        synchronized (GroovyFormulaFactory.class) {
            if (factory != null)
                return factory;
            return factory = new GroovyFormulaFactory();
        }
    }

    // @Override
    // public FormulaHolder create(String formula, FormulaImportContext context) {
    //     GroovyImportContext groovyContext;
    //     if (context instanceof GroovyImportContext) {
    //         groovyContext = (GroovyImportContext) context;
    //     } else {
    //         groovyContext = new GroovyImportContext(context);
    //     }
    //     engine().eval()
    //     engine().eval()
    //     engine().eval()
    //     ScriptEngine engine = engine();
    //     formula += groovyContext.getImportCode();
    //     // return ;
    // }

    @Override
    public FormulaHolder create(String formula, Map<String, Object> attributes) {
        return null;
    }
}
