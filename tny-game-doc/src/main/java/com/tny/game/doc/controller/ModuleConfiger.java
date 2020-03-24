package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;

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
        int id = holder.getModuleId();
        if (id > 0) {
            ModuleConfiger configer = configerMap.get(holder.getModuleId());
            ModuleConfiger old;
            if (configer != null) {
                if (configer.getClassName().equals(holder.getClassName()))
                    return configer;
                throw new IllegalArgumentException(
                        format("{} 类 与 {} 类 ModuleID 都为 {}", configer.getClassName(), holder.getEntityClass(), holder.getModuleId()));
            } else {
                configer = new ModuleConfiger(holder, typeFormatter);
                old = configerMap.putIfAbsent(configer.getModuleId(), configer);
                if (old != null) {
                    throw new IllegalArgumentException(
                            format("{} 类 与 {} 类 ModuleID 都为 {}", configer.getClassName(), holder.getEntityClass(), holder.getModuleId()));
                } else {
                    return configer;
                }
            }
        }
        throw new IllegalArgumentException(format("{} ModuleID 不存在", holder.getEntityClass()));
    }

    private ModuleConfiger(ClassDocHolder holder, TypeFormatter typeFormatter) {
        this.className = holder.getClassName();
        this.packageName = holder.getEntityClass().getPackage().getName();
        this.moduleID = holder.getModuleId();
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
            OperationConfiger old = fieldMap.put(configer.getOpId(), configer);
            if (old != null) {
                throw new IllegalArgumentException(format("{} 类 {} 与 {} 字段 OpID 都为 {}",
                        holder.getEntityClass(), configer.getMethodName(), old.getMethodName(), configer.getOpId()));
            }
        }
        Collections.sort(operationList, Comparator.comparing(OperationConfiger::getMethodName));
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

    public List<OperationConfiger> getOperationList() {
        return this.operationList.operationList;
    }

}
