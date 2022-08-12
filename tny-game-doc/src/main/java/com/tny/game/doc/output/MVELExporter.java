/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.output;

import com.tny.game.common.math.*;
import com.tny.game.doc.table.*;
import com.tny.game.expr.*;
import com.tny.game.expr.mvel.*;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.*;

/**
 * xml 格式化
 * Created by Kun Yang on 2017/4/8.
 */
class MVELExporter implements Exporter {

    public static final MvelTemplateHolderFactory factory = new MvelTemplateHolderFactory();

    MVELExporter() {
        ExprContext context = factory.getContext();
        context.importStaticClasses(Math.class, MathAide.class, FormulaUtils.class);
        context.importClasses(ArrayList.class, HashSet.class, HashMap.class);
    }

    @Override
    public String output(OutputScheme scheme) throws IOException {
        String templateContent = FileUtils.readFileToString(scheme.getTemplate(), UTF_8);
        ExprHolder holder = factory.create(templateContent);
        return holder.createExpr()
                .putAll(scheme.getAttribute().getContext())
                .execute(String.class);
    }

}
