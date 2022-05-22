package com.tny.game.expr.mvel;

import com.tny.game.expr.*;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public abstract class MvelExprHolderFactory extends AbstractExprHolderFactory {

    private static final String LAZY_KEY = "tny.common.formula.mvel.lazy";

    // private static final String CACHED_KEY = "tny.common.formula.mvel.cached";
    private static final String EXPR_INFO_KEY = "tny.common.formula.mvel.info";

    private static final boolean LAZY = System.getProperty(LAZY_KEY, "false").equals("true");

    public static final boolean EXPR_INFO = System.getProperty(EXPR_INFO_KEY, "true").equals("true");

    public static final Logger LOGGER = LoggerFactory.getLogger(MvelExprHolderFactory.class);

    protected MvelExprContext context;

    protected boolean lazy;

    protected boolean oneLine;

    public MvelExprHolderFactory() {
        super(1);
        this.lazy = LAZY;
        this.oneLine = false;
        this.context = new MvelExprContext();
    }

    public MvelExprHolderFactory(boolean oneLine) {
        this(LAZY, oneLine);
    }

    public MvelExprHolderFactory(boolean lazy, boolean oneLine) {
        super(1);
        this.lazy = lazy;
        this.oneLine = oneLine;
        this.context = new MvelExprContext();
    }

    @Override
    public ExprContext getContext() {
        return this.context;
    }

}
