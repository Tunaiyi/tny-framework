/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.expr.graaljs;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import com.tny.game.common.math.*;
import com.tny.game.expr.*;
import com.tny.game.expr.jsr223.*;
import org.graalvm.polyglot.Context;

import javax.script.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class GraalJsExprHolderFactory extends ScriptExprHolderFactory {

    private static final String LAN = "graal.js";

    private static volatile GraalJsExprHolderFactory factory;

    public GraalJsExprHolderFactory() {
        super(LAN, GraalJsExprContext::new);
    }

    private GraalJsExprHolderFactory(int cacheGroupSize) {
        super(LAN, cacheGroupSize, GraalJsExprContext::new);
    }

    public static GraalJsExprHolderFactory createFactory() {
        return new GraalJsExprHolderFactory();
    }

    public static GraalJsExprHolderFactory getDefault() {
        if (factory != null) {
            return factory;
        }
        synchronized (GraalJsExprHolderFactory.class) {
            if (factory != null) {
                return factory;
            }
            return factory = new GraalJsExprHolderFactory();
        }
    }

    @Override
    protected ScriptEngine createEngine(String lan) {

        return GraalJSScriptEngine.create(null,
                Context.newBuilder("js")
                        .allowAllAccess(true)
                        .allowHostClassLoading(true)
                        .allowHostClassLookup(cl -> true)
        );
    }

    public static void main(String[] args) throws ScriptException {
        GraalJsExprHolderFactory factory = new GraalJsExprHolderFactory();
        factory.getContext()
                .importClasses(Instant.class)
                .importClasses(MathAide.class)
                .importClassAs("S", String.class);
        // ExprHolder holder = factory.create("a + 100 + new S('__222__') + DateTime.now() + '---' + rand(200)");
        // System.out.println(holder.createExpr().put("a", 2000).execute(Integer.class));
        //
        // ExprHolder test2 = factory.create(
        //         "def index; def fibonacci = []; fibonacci[0] = 1; fibonacci[1] = 1; for(index=2; index<=100; index++) { fibonacci[index] =
        //         (fibonacci[index-2] + fibonacci[index-1]);}\n fibonacci.size();");
        // System.out.println(test2.createExpr().execute(Integer.class));

        ExprHolder holder3 = factory
                .create("{"
                        + "let a = 100;"
                        + "MathAide.rand(a);"
                        + "};");
        ExprHolder holder4 = factory
                .create("{" +
                        "let a = 10;" +
                        "a;" +
                        "}");
        List<ForkJoinTask<?>> tasks = new ArrayList<>();
        for (int n = 0; n < 100; n++) {
            tasks.add(ForkJoinPool.commonPool().submit(() -> System.out.println(holder3.createExpr().execute(Integer.class))));
        }
        for (int n = 0; n < 100; n++) {
            tasks.add(ForkJoinPool.commonPool().submit(() -> System.out.println(holder4.createExpr().execute(Integer.class))));
        }
        tasks.forEach(ForkJoinTask::join);
    }

}