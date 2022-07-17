package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;

@XStreamAlias("module")
public class ModuleDescription {

    private static final Map<Class<?>, ModuleDescription> configerMap = new ConcurrentHashMap<>();

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private int moduleID;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String packageName;

    private OperationList operationList;

    @XStreamAlias("operationList")
    private static class OperationList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private final String type = "list";

        @XStreamImplicit(itemFieldName = "operation")
        private List<OperationDescription> operationList;

    }

    public static ModuleDescription create(ClassDocHolder holder, TypeFormatter typeFormatter) {
        ModuleDescription configer = configerMap.get(holder.getEntityClass());
        ModuleDescription old;
        if (configer != null) {
            if (configer.getClassName().equals(holder.getDocClassName())) {
                return configer;
            }
            throw new IllegalArgumentException(
                    format("{} 类 与 {} 类 Module 已存在", configer.getClassName(), holder.getEntityClass()));
        } else {
            configer = new ModuleDescription(holder, typeFormatter);
            old = configerMap.putIfAbsent(holder.getEntityClass(), configer);
            if (old != null) {
                throw new IllegalArgumentException(
                        format("{} 类 与 {} 类 Module 已存在", configer.getClassName(), holder.getEntityClass()));
            } else {
                return configer;
            }
        }
    }

    private ModuleDescription(ClassDocHolder holder, TypeFormatter typeFormatter) {
        this.className = holder.getDocClassName();
        this.packageName = holder.getEntityClass().getPackage().getName();
        //		this.moduleID = holder.getModuleId();
        this.operationList = new OperationList();
        this.des = holder.getClassDoc().value();
        Map<Integer, OperationDescription> fieldMap = new HashMap<>();
        List<OperationDescription> operationList = new ArrayList<>();
        for (DocMethod funDocHolder : holder.getFunList()) {
            OperationDescription description = new OperationDescription(funDocHolder, typeFormatter);
            operationList.add(description);
            OperationDescription old = fieldMap.put(description.getOpId(), description);
            if (old != null) {
                throw new IllegalArgumentException(format("{} 类 {} 与 {} 字段 OpID 都为 {}",
                        holder.getEntityClass(), description.getMethodName(), old.getMethodName(), description.getOpId()));
            }
        }
        operationList.sort(Comparator.comparing(OperationDescription::getMethodName));
        this.operationList.operationList = Collections.unmodifiableList(operationList);
    }

    public String getDes() {
        return this.des;
    }

    public String getClassName() {
        return this.className;
    }

    public int getModuleId() {
        return this.moduleID;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public List<OperationDescription> getOperationList() {
        return this.operationList.operationList;
    }

}
