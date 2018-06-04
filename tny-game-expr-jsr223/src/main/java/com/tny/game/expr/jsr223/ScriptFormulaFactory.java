package com.tny.game.expr.jsr223;

import com.tny.game.expr.FormulaHolder;

import javax.script.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.ifNull;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public abstract class ScriptFormulaFactory {

    // private static final String CACHED_KEY = "tny.common.formula.groovy.cached";

    private ScriptEngine engine;

    private ScriptFormulaContext context;

    private Function<ScriptEngine, ScriptFormulaContext> contextCreator;

    private Map<String, FormulaHolder> holderMap = new HashMap<>();

    private int cacheGroupSize = 16;
    private Map<String, FormulaHolder>[] formulaHolderMap;
    private ReadWriteLock[] locks;

    public ScriptFormulaFactory(String lan, Function<ScriptEngine, ScriptFormulaContext> contextCreator) {
        final ScriptEngineManager factory = new ScriptEngineManager();
        this.contextCreator = contextCreator;
        this.engine = factory.getEngineByName(lan);
        this.context = contextCreator.apply(this.engine);
        this.engine.setBindings(context.bindings, ScriptContext.ENGINE_SCOPE);
        formulaHolderMap = new HashMap[cacheGroupSize];
        locks = new ReadWriteLock[cacheGroupSize];
        for (int i = 0; i < cacheGroupSize; i++) {
            formulaHolderMap[i] = new HashMap<>();
            locks[i] = new ReentrantReadWriteLock();
        }
    }

    protected ScriptEngine engine() {
        return engine;
    }

    public ScriptFormulaContext getEngineContext() {
        return context;
    }

    public ScriptFormulaContext createContext() {
        return contextCreator.apply(this.engine);
    }

    /**
     * 使用默认Context, 创建公式Holder, 可缓存
     *
     * @param formula 表达式
     * @return 返回公式Holder
     * @throws ScriptException 脚本异常
     */
    public FormulaHolder create(String formula) throws ScriptException {
        return create(formula, null);
    }

    /**
     * 使用指定Context, 创建公式Holder, 不可缓存
     *
     * @param formula 表达式
     * @param context 指定Context
     * @return 返回公式Holder
     * @throws ScriptException 脚本异常
     */
    public FormulaHolder create(String formula, ScriptFormulaContext context) throws ScriptException {
        String imports = this.context.getImportCode();
        Map<String, Object> bindings = null;
        if (context != null && context != this.context) {
            imports += context.getImportCode();
            bindings = context.getBindings();
        }
        return new ScriptFormula(engine, imports + formula, ifNull(context, this.context), bindings);
    }

}
