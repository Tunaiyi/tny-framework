package com.tny.game.common.formula;

import com.tny.game.common.config.ConfigLoader;
import org.apache.commons.io.IOUtils;
import org.mvel2.ParserContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
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
public class MvelFormulaFactory {

    public static final String LAZY_COMPILE_KEY = "com.sd.formula.mvel.lazy";

    private static ConcurrentMap<String, Expression> expressionMap = new ConcurrentHashMap<String, Expression>();

    /**
     * 创建表达式 <br>
     *
     * @param formula 表达式内容
     * @param type    表达式类型
     * @return 表达式
     * @throws IOException
     */
    public static FormulaHolder load(final String path, final FormulaType type) throws IOException {
        String value = System.getProperty(LAZY_COMPILE_KEY);
        boolean lazy = value != null && value.toLowerCase().equals("true");
        Map<String, Object> context = Collections.emptyMap();
        try (InputStream input = ConfigLoader.loadInputStream(path)) {
            byte[] data = new byte[input.available()];
            IOUtils.readFully(input, data);
            String formula = new String(data);
            return create(formula, type, context, lazy);
        }
    }

    /**
     * 创建表达式 <br>
     *
     * @param formula 表达式内容
     * @param type    表达式类型
     * @return 表达式
     */
    public static FormulaHolder create(final String formula, final FormulaType type) {
        String value = System.getProperty(LAZY_COMPILE_KEY);
        boolean lazy = value != null && value.toLowerCase().equals("true");
        Map<String, Object> context = Collections.emptyMap();
        return create(formula, type, context, lazy);
    }

    /**
     * 创建表达式 <br>
     *
     * @param formula 表达式内容
     * @param type    表达式类型
     * @return 表达式
     */
    public static FormulaHolder create(final String formula, final FormulaType type, Map<String, Object> context, boolean lazy) {
        if (type == FormulaType.EXPRESSION)
            return getExpression(formula, context, lazy);
        return new Template(formula, context, lazy);
    }

    public static FormulaHolder create(final String formula, final FormulaType type, ParserContext parserContext, boolean lazy) {
        if (type == FormulaType.EXPRESSION)
            return getExpression(formula, parserContext, lazy);
        return new Template(formula, parserContext, lazy);
    }

    private static Expression getExpression(String expression, ParserContext parserContext, boolean lazy) {
        Expression exp = expressionMap.get(expression);
        if (exp != null)
            return exp;
        exp = new Expression(expression, parserContext, lazy);
        Expression oldExpression = expressionMap.putIfAbsent(expression, exp);
        return oldExpression != null ? oldExpression : exp;
    }

    private static Expression getExpression(String expression, Map<String, Object> context, boolean lazy) {
        Expression exp = expressionMap.get(expression);
        if (exp != null)
            return exp;
        exp = new Expression(expression, context, lazy);
        Expression oldExpression = expressionMap.putIfAbsent(expression, exp);
        return oldExpression != null ? oldExpression : exp;
    }

//	public static void main(final String[] args) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("index", 0);
//		FormulaHolder formulatest = MvelFormulaFactory.create("value.bytes[mapVars.index]", FormulaType.EXPRESSION, null, true);
//		System.out.println(formulatest.createFormula().put("value", "abc").put("mapVars", map).execute(byte[].class));
//	}

}
