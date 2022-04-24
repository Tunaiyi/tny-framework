package com.tny.game.expr.luaj;

import com.tny.game.common.math.*;
import com.tny.game.expr.*;
import com.tny.game.expr.jsr223.*;

import javax.script.ScriptException;
import java.time.Instant;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class LuajExprHolderFactory extends ScriptExprHolderFactory {

    private static final String LAN = "luaj";

    private static volatile LuajExprHolderFactory factory;

    public LuajExprHolderFactory() {
        super(LAN, LuajExprContext::new);
    }

    private LuajExprHolderFactory(int cacheGroupSize) {
        super(LAN, cacheGroupSize, LuajExprContext::new);
    }

    public static LuajExprHolderFactory createFactory() {
        return new LuajExprHolderFactory();
    }

    public static LuajExprHolderFactory getDefault() {
        if (factory != null) {
            return factory;
        }
        synchronized (LuajExprHolderFactory.class) {
            if (factory != null) {
                return factory;
            }
            return factory = new LuajExprHolderFactory();
        }
    }

    public static void main(String[] args) throws ScriptException {
        LuajExprHolderFactory factory = new LuajExprHolderFactory();
        factory.getContext()
                .importClasses(Instant.class)
                .importClasses(MathAide.class);
        // .importStaticClasses(MathEx.class)
        // .importClassAs("S", String.class);
        ExprHolder holder = factory
                .create("return today:getMillis()..(a + 100)..' '..tostring(DateTime:now())..'---'..MathEx:rand(200)..' or '..MathEx:rand(1, 200);");
        System.out.println(holder.createExpr()
                .put("today", Instant.now())
                .put("a", 2000).execute(Object.class));

        String fibonacci = "local index; \n" +
                "local fibonacci = {}; \n" +
                "fibonacci[0] = 0.0; \n" +
                "fibonacci[1] = 1.0; \n" +
                "for index = 2, size, 1 do \n" +
                "   fibonacci[index] = fibonacci[index-2] + fibonacci[index-1] \n" +
                "end; \n" +
                "return fibonacci[size];\n";
        ExprHolder holder2 = factory.create(fibonacci);
        System.out.println(holder2.createExpr().put("size", 10).execute(Integer.class));

    }

}