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
