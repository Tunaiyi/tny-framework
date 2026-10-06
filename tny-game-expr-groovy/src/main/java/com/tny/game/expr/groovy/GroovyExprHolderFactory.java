/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.expr.groovy;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.math.*;
import com.tny.game.expr.*;
import com.tny.game.expr.jsr223.*;

import javax.script.ScriptException;
import java.time.Instant;

/**
 * Created by Kun Yang on 2018/5/24.
 */
// 单元装配自声明（remove-engine-transitive-assembly D3）：装配方把本引擎带上并经启动装配注册后，
// 求值位置经 ExprHolderFactory 契约键解析取得本实现；契约模块保持零引擎依赖
@UnitInterface
@Unit(unitInterfaces = ExprHolderFactory.class)
public class GroovyExprHolderFactory extends ScriptExprHolderFactory {

    private static final String LAN = "groovy";

    private static volatile GroovyExprHolderFactory factory;

    public GroovyExprHolderFactory() {
        super(LAN, GroovyExprContext::new);
    }

    private GroovyExprHolderFactory(int cacheGroupSize) {
        super(LAN, cacheGroupSize, GroovyExprContext::new);
    }

    public static GroovyExprHolderFactory createFactory() {
        return new GroovyExprHolderFactory();
    }

    public static GroovyExprHolderFactory getDefault() {
        if (factory != null) {
            return factory;
        }
        synchronized (GroovyExprHolderFactory.class) {
            if (factory != null) {
                return factory;
            }
            return factory = new GroovyExprHolderFactory();
        }
    }

    public static void main(String[] args) throws ScriptException {
        GroovyExprHolderFactory factory = new GroovyExprHolderFactory();
        factory.getContext()
                .importClasses(Instant.class)
                // .importStaticClasses(MathEx.class)
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
                .create("def rand = MathEx.&rand; def rand1Method(int range) { rand(range) }; def rand2Method(int from, int to) { rand(from, to) };" +
                        " return rand1Method(200) + '   ' +  + rand2Method(10, 30);");
        System.out.println(holder3.createExpr().execute(Integer.class));

    }

}