package com.tny.game.common.formula;

import com.tny.game.common.config.ConfigLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mvel2.ParserContext;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

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

    private static final String LAZY_KEY = "tny.common.formula.mvel.lazy";
    private static final String CACHED_KEY = "tny.common.formula.mvel.cached";
    private static final String EXPR_INFO_KEY = "tny.common.formula.mvel.info";

    private static final boolean LAZY = System.getProperty(LAZY_KEY, "false").equals("true");
    private static final boolean CACHED = System.getProperty(CACHED_KEY, "true").equals("true");
    public static final boolean EXPR_INFO = System.getProperty(EXPR_INFO_KEY, "true").equals("true");

    public static final int size = 16;
    private static Map<String, Expression>[] expressionMaps;
    private static StampedLock[] locks;

    static {
        expressionMaps = new HashMap[16];
        locks = new StampedLock[16];
        for (int i = 0; i < size; i++) {
            expressionMaps[i] = new HashMap<>();
            locks[i] = new StampedLock();
        }
    }

    public static void cleanCache() {
        for (int i = 0; i < size; i++) {
            StampedLock lock = locks[i];
            long rs = lock.writeLock();
            try {
                expressionMaps[i].clear();
            } finally {
                lock.unlockWrite(rs);
            }
        }
    }

    /**
     * 创建表达式 <br>
     *
     * @param path 表达式内容
     * @param type 表达式类型
     * @return 表达式
     * @throws IOException 异常
     */
    public static FormulaHolder load(final String path, final FormulaType type) throws IOException {
        Map<String, Object> context = Collections.emptyMap();
        try (InputStream input = ConfigLoader.loadInputStream(path)) {
            byte[] data = new byte[input.available()];
            IOUtils.readFully(input, data);
            String formula = new String(data);
            return create(formula, type, context, LAZY);
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
        Map<String, Object> context = Collections.emptyMap();
        return create(formula, type, context, LAZY);
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
        return getExpression(expression, () -> new Expression(expression, parserContext, lazy));
    }

    private static Expression getExpression(String expression, Map<String, Object> context, boolean lazy) {
        return getExpression(expression, () -> new Expression(expression, context, lazy));
    }

    private static int toHash(String expression) {
        return Math.abs(expression.hashCode()) % size;
    }

    private static Expression getExpression(String expression, Supplier<Expression> creator) {
        expression = StringUtils.replace(StringUtils.trim(expression), "\n", "");
        int index = toHash(expression);
        Map<String, Expression> expressionMap = expressionMaps[index];
        Expression exp;
        if (CACHED) {
            StampedLock lock = locks[index];
            long rs = lock.readLock();
            try {
                exp = expressionMap.get(expression);
                if (exp != null)
                    return exp;
            } finally {
                lock.unlockRead(rs);
            }
            long ws = lock.writeLock();
            try {
                exp = creator.get();
                Expression oldExpression = expressionMap.putIfAbsent(expression, exp);
                return oldExpression != null ? oldExpression : exp;
            } finally {
                lock.unlockWrite(ws);
            }
        }
        return creator.get();
    }


//	public static void main(final String[] args) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("index", 0);
//		FormulaHolder formulatest = MvelFormulaFactory.create("value.bytes[mapVars.index]", FormulaType.EXPRESSION, null, true);
//		System.out.println(formulatest.createFormula().put("value", "abc").put("mapVars", map).execute(byte[].class));
//	}

}
