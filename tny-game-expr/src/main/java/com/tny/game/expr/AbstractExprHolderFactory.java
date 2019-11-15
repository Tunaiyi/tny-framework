package com.tny.game.expr;


import java.util.*;
import java.util.concurrent.locks.*;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public abstract class AbstractExprHolderFactory implements ExprHolderFactory {

    private int cacheGroupSize;
    private boolean cached;
    private Map<String, ExprHolder>[] exprHolderMap;
    private ReadWriteLock[] locks;

    public AbstractExprHolderFactory() {
        this(Runtime.getRuntime().availableProcessors() * 2);
    }

    @SuppressWarnings("unchecked")
    public AbstractExprHolderFactory(int cacheGroupSize) {
        this.cacheGroupSize = cacheGroupSize;
        this.cached = this.cacheGroupSize > 0;
        if (this.cached) {
            exprHolderMap = new HashMap[cacheGroupSize];
            locks = new ReadWriteLock[cacheGroupSize];
            for (int i = 0; i < cacheGroupSize; i++) {
                exprHolderMap[i] = new HashMap<>();
                locks[i] = new ReentrantReadWriteLock();
            }
        }
    }

    /**
     * 使用默认Context, 创建公式Holder, 可缓存
     *
     * @param expression 表达式
     * @return 返回公式Holder
     * @throws ExprException 脚本异常
     */
    @Override
    public ExprHolder create(String expression) throws ExprException {
        expression = preproccess(expression);
        int index = toHash(expression);
        if (this.cached) {
            ExprHolder exp;
            Map<String, ExprHolder> exprHolderMap = this.exprHolderMap[index];
            Lock lock = locks[index].readLock();
            lock.lock();
            try {
                exp = exprHolderMap.get(expression);
                if (exp != null)
                    return exp;
            } finally {
                lock.unlock();
            }
            lock = locks[index].writeLock();
            lock.lock();
            try {
                exp = exprHolderMap.get(expression);
                if (exp != null)
                    return exp;
                exp = createExprHolder(expression);
                ExprHolder oldExpression = exprHolderMap.putIfAbsent(expression, exp);
                return oldExpression != null ? oldExpression : exp;
            } finally {
                lock.unlock();
            }
        } else {
            return createExprHolder(expression);
        }
    }

    protected abstract String preproccess(String expr);

    protected abstract ExprHolder createExprHolder(String expr) throws ExprException;

    private int toHash(String expression) {
        return Math.abs(expression.hashCode()) % cacheGroupSize;
    }

}
