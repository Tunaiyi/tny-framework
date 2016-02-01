package com.tny.game.common.formula.groovy;

import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MathEx;
import com.tny.game.common.formula.MvelFormulaFactory;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author KGTny
 * @ClassName: FormulaFactory
 * @Description: 表达式工程
 * @date 2011-10-31 下午10:18:41
 * <p>
 * <p>
 * <br>
 */
public class GroovyFormulaFactory {

    public static final String SCRIPT_TEMPLATE_TEXT = "import funs.god.common.groovy.GroovyScript;@if{importStatic != null}@foreach{value : importStatic}import static @{value};@end{}@end{}@if{importPackage != null}@foreach{value : importPackage}import @{value};@end{}@end{}class @{scriptName} extends GroovyScript {@foreach{field : fields}def @{field};@end{}protected void initContext(Map<String, Object> context) {for (entry in context.entrySet()) {switch(entry.getKey()) {@foreach{field : fields}case \"@{field}\" : this.@{field} = entry.getValue(); break;@end{}}}};def run() {@{script}};}";
    private static final FormulaHolder SCRIPT_TEMPLATE_HOLDER = MvelFormulaFactory.create(SCRIPT_TEMPLATE_TEXT, FormulaType.TEMPLATE);

    private static final String SCRIPT_ATTR_NAME = "scriptName";
    private static final String SCRIPT_ATTR_IMPORT_PACKAGE = "importPackage";
    private static final String SCRIPT_ATTR_IMPORT_STATIC = "importStatic";
    private static final String SCRIPT_ATTR_FIELDS = "fields";
    private static final String SCRIPT_ATTR_SCRIPT = "script";

    private static final String LAZY_COMPILE_KEY = "com.sd.formula.groovy.lazy";
    private static final String CONFIGER_CLASS_NAME_KEY = "formula.groovy.configer_class";

    private static final AtomicInteger scriptIDCreator = new AtomicInteger();

    private volatile static CachedGroovyClassLoader CLASS_LOADER;

    private static ConcurrentMap<String, GroovyFormula> scriptMap = new ConcurrentHashMap<>();

    static {
        CompilerConfiguration configuration = new CompilerConfiguration();
        String configerClassName = System.getProperty(CONFIGER_CLASS_NAME_KEY);
        if (configerClassName != null) {
            try {
                Class<?> configerClass = Class.forName(configerClassName);
                GroovyConfiger configer = (GroovyConfiger) configerClass.newInstance();
                configer.initConfiguration(configuration);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ImportCustomizer imports = new ImportCustomizer();
        for (final Method method : Math.class.getMethods())
            imports.addStaticImport(method.getDeclaringClass().getName(), method.getName());
        for (final Method method : MathEx.class.getMethods())
            imports.addStaticImport(method.getDeclaringClass().getName(), method.getName());
        configuration.addCompilationCustomizers(imports);
        CLASS_LOADER = new CachedGroovyClassLoader(GroovyFormulaFactory.class.getClassLoader(), configuration);
    }

    /**
     * 创建表达式 <br>
     *
     * @param formula 表达式内容
     * @param type    表达式类型
     * @return 表达式
     */
    public static FormulaHolder create(final String formula) {
        return create(formula, true);
    }

    /**
     * 创建表达式 <br>
     *
     * @param formula 表达式内容
     * @param type    表达式类型
     * @return 表达式
     */
    public static FormulaHolder create(final String formula, ScriptDescribe describe) {
        String value = System.getProperty(LAZY_COMPILE_KEY);
        boolean lazy = value != null && value.toLowerCase().equals("true");
        return create(formula, describe, lazy, true);
    }

    /**
     * 创建表达式 <br>
     *
     * @param formula 表达式内容
     * @param type    表达式类型
     * @return 表达式
     */
    public static FormulaHolder create(final String formula, boolean cache) {
        String value = System.getProperty(LAZY_COMPILE_KEY);
        boolean lazy = value != null && value.toLowerCase().equals("true");
        return create(formula, null, lazy, cache);
    }

    /**
     * 创建表达式 <br>
     *
     * @param formula 表达式内容
     * @param type    表达式类型
     * @return 表达式
     */
    public static FormulaHolder create(final String formula, ScriptDescribe describe, boolean lazy, boolean cache) {
        return getScript(formula, describe, lazy, cache);
    }

    private static GroovyFormula getScript(String expression, ScriptDescribe describe, boolean lazy, boolean cache) {
        String scriptText = createScriptText(expression, describe);
        GroovyFormula script = null;
        if (cache)
            script = scriptMap.get(expression);
        if (script == null)
            script = new GroovyFormula(scriptText, CLASS_LOADER, lazy);
        if (cache) {
            GroovyFormula oldScript = scriptMap.putIfAbsent(expression, script);
            script = oldScript != null ? oldScript : script;
        }
        return script;
    }

    public static String createScriptText(String expression, ScriptDescribe describe) {
        if (describe == null)
            describe = ScriptDescribe.newInstance();
        String scriptName = describe.getName();
        scriptName = StringUtils.isBlank(scriptName) ? "GroovyFormula_" + scriptIDCreator.incrementAndGet() : scriptName;
        return SCRIPT_TEMPLATE_HOLDER.createFormula()
                .put(SCRIPT_ATTR_NAME, scriptName)
                .put(SCRIPT_ATTR_SCRIPT, expression)
                .put(SCRIPT_ATTR_FIELDS, describe.getFields())
                .put(SCRIPT_ATTR_IMPORT_PACKAGE, describe.getImportsInfo())
                .put(SCRIPT_ATTR_IMPORT_STATIC, describe.getImportStatics())
                .execute(String.class);
    }

    public static void main(String[] args) {
        //		create("println KEEPER_CONFIG_PATH", ScriptDescribe.newInstance()
        //				.addImportStatic(ConfigPath.class, "KEEPER_CONFIG_PATH"))
        //				.createFormula().execute(Object.class);
    }

}
