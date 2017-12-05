package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.tny.game.suite.base.Logs;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.ClassDocHolder;
import com.tny.game.doc.holder.FunDocHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@XStreamAlias("module")
public class ModuleConfiger {

    private static Map<Integer, ModuleConfiger> configerMap = new ConcurrentHashMap<>();

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private int moduleID;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

    @XStreamAsAttribute
    private String packageName;

    private OperationList operationList;

    @XStreamAlias("operationList")
    private static class OperationList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String type = "list";

        @XStreamImplicit(itemFieldName = "operation")
        private List<OperationConfiger> operationList;

    }

    public static ModuleConfiger create(ClassDocHolder holder, TypeFormatter typeFormatter) {
        int id = holder.getModuleID();
        if (id > 0) {
            ModuleConfiger configer = configerMap.get(holder.getModuleID());
            ModuleConfiger old;
            if (configer != null) {
                if (configer.getClassName().equals(holder.getClassName()))
                    return configer;
                throw new IllegalArgumentException(Logs.format("{} 类 与 {} 类 ModuleID 都为 {}", configer.getClassName(), holder.getEntityClass(), holder.getModuleID()));
            } else {
                configer = new ModuleConfiger(holder, typeFormatter);
                old = configerMap.putIfAbsent(configer.getModuleID(), configer);
                if (old != null) {
                    throw new IllegalArgumentException(Logs.format("{} 类 与 {} 类 ModuleID 都为 {}", configer.getClassName(), holder.getEntityClass(), holder.getModuleID()));
                } else {
                    return configer;
                }
            }
        }
        throw new IllegalArgumentException(Logs.format("{} ModuleID 不存在", holder.getEntityClass()));
    }

    private ModuleConfiger(ClassDocHolder holder, TypeFormatter typeFormatter) {
        this.className = holder.getClassName();
        this.packageName = holder.getEntityClass().getPackage().getName();
        this.moduleID = holder.getModuleID();
        this.operationList = new OperationList();
        this.des = holder.getClassDoc().value();
        this.text = holder.getClassDoc().text();
        if (StringUtils.isBlank(this.text))
            this.text = this.des;
        Map<Integer, OperationConfiger> fieldMap = new HashMap<>();
        List<OperationConfiger> operationList = new ArrayList<>();
        for (FunDocHolder funDocHolder : holder.getFunList()) {
            OperationConfiger configer = new OperationConfiger(funDocHolder, typeFormatter);
            operationList.add(configer);
            OperationConfiger old = fieldMap.put(configer.getOpID(), configer);
            if (old != null) {
                throw new IllegalArgumentException(Logs.format("{} 类 {} 与 {} 字段 OpID 都为 {}",
                        holder.getEntityClass(), configer.getMethodName(), old.getMethodName(), configer.getOpID()));
            }
        }
        Collections.sort(operationList, Comparator.comparing(OperationConfiger::getMethodName));
        this.operationList.operationList = Collections.unmodifiableList(operationList);
    }

    public String getDes() {
        return des;
    }

    public String getClassName() {
        return className;
    }

    public int getModuleID() {
        return moduleID;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<OperationConfiger> getOperationList() {
        return operationList.operationList;
    }

}
