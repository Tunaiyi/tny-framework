package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;
import com.tny.game.net.command.dispatcher.*;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class FunDocHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunDocHolder.class);

    private FunDoc funDoc;

    private int opID = -1;

    private Method method;

    private List<VarDocHolder> paramList;

    public static FunDocHolder create(Class<?> clazz, Method method) {
        FunDocHolder holder = new FunDocHolder();
        FunDoc funDoc = method.getAnnotation(FunDoc.class);
        if (funDoc == null) {
            return null;
        }
        RpcProfile controller = RpcProfile.of(method);
        if (controller == null) {
            LOGGER.warn("{}.{} is not controller", clazz, method.getName());
        } else {
            if (controller.getProtocol() < 0) {
                LOGGER.warn("{}.{} controller value {} < 0", clazz, method.getName(), controller.getProtocol());
            }
            holder.opID = controller.getProtocol();
        }
        holder.funDoc = funDoc;
        holder.method = method;
        List<VarDocHolder> paramList = new ArrayList<VarDocHolder>();
        holder.paramList = Collections.unmodifiableList(paramList);
        Parameter[] params = method.getParameters();
        for (Parameter param : params) {
            VarDoc paramDoc = param.getAnnotation(VarDoc.class);
            if (paramDoc == null) {
                continue;
            }
            String name = param.getName();
            paramList.add(VarDocHolder.create(paramDoc, name, param.getType()));
        }
        return holder;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getParamsAnnotation(Annotation[] paramAnnotation, Class<VarDoc> class1) {
        for (Annotation an : paramAnnotation) {
            if (class1.isInstance(an)) {
                return (T)an;
            }
        }
        return null;
    }

    public FunDoc getFunDoc() {
        return funDoc;
    }

    public int getOpId() {
        return opID;
    }

    public Method getMethod() {
        return method;
    }

    public List<VarDocHolder> getParamList() {
        return paramList;
    }

}
