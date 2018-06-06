package com.tny.game.expr.jsr223;

import com.tny.game.common.formula.*;
import com.tny.game.common.number.NumberAide;
import com.tny.game.expr.*;

import javax.script.*;
import java.util.function.Function;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public abstract class ScriptExprHolderFactory extends AbstractExprHolderFactory {

    // private static final String CACHED_KEY = "tny.common.expr.groovy.cached";

    private ScriptEngine engine;

    private ScriptExprContext context;

    public ScriptExprHolderFactory(String lan, Function<ScriptEngine, ScriptExprContext> contextCreator) {
        this(lan, Runtime.getRuntime().availableProcessors() * 2, contextCreator);
    }

    public ScriptExprHolderFactory(String lan, int cacheGroupSize, Function<ScriptEngine, ScriptExprContext> contextCreator) {
        super(cacheGroupSize);
        final ScriptEngineManager factory = new ScriptEngineManager();
        this.engine = factory.getEngineByName(lan);
        this.context = contextCreator.apply(this.engine);
        this.context.importStaticClasses(
                CollectionEx.class,
                Math.class,
                MathEx.class,
                NumberAide.class,
                DateTimeEx.class);
    }

    @Override
    protected String preproccess(String expr) {
        return this.context.getImportCode() + expr;
    }


    @Override
    protected ExprHolder createExprHolder(String expr) throws ExprException {
        return new ScriptExpr(engine, expr, this.context);
    }

    @Override
    public ExprContext getContext() {
        return context;
    }
}
