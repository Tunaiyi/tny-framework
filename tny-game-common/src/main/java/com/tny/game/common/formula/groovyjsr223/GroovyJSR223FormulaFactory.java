package com.tny.game.common.formula.groovyjsr223;

import com.tny.game.common.formula.FormulaHolder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author KGTny
 * @ClassName: FormulaFactory
 * @Description: 表达式工程
 * @date 2011-10-31 下午10:18:41
 * <p>
 * <p>
 * <br>
 */
public class GroovyJSR223FormulaFactory {

    private static final String LAZY_COMPILE_KEY = "com.sd.formula.groovyjsr223.lazy";

    private static ConcurrentMap<String, GroovyJSR223Formula> scriptMap = new ConcurrentHashMap<>();

    /**
     * 创建表达式 <br>
     *
     * @param formula 表达式内容
     * @return 表达式
     */
    public static FormulaHolder create(final String formula) {
        return create(formula, null, true);
    }

    public static FormulaHolder create(final String formula, GroovyContext context) {
        return create(formula, context, true);
    }

    public static FormulaHolder create(final String formula, GroovyContext context, boolean cache) {
        String value = System.getProperty(LAZY_COMPILE_KEY);
        boolean lazy = value != null && value.toLowerCase().equals("true");
        return create(formula, context, lazy, cache);
    }

    public static FormulaHolder create(final String formula, GroovyContext context, boolean lazy, boolean cache) {
        return getScript(formula, context, lazy, cache);
    }

    private static GroovyJSR223Formula getScript(String expression, GroovyContext context, boolean lazy, boolean cache) {
        GroovyJSR223Formula script = null;
        if (cache)
            script = scriptMap.get(expression);
        if (script == null)
            script = new GroovyJSR223Formula(expression, context, lazy);
        if (cache) {
            GroovyJSR223Formula oldScript = scriptMap.putIfAbsent(expression, script);
            script = oldScript != null ? oldScript : script;
        }
        return script;
    }

    public static void main(String[] args) {
        //		create("println KEEPER_CONFIG_PATH", ScriptDescribe.newInstance()
        //				.addImportStatic(ConfigPath.class, "KEEPER_CONFIG_PATH"))
        //				.createFormula().execute(Object.class);
    }

}
