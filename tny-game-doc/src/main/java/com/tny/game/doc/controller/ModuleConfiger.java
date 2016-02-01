package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.ClassDocHolder;
import com.tny.game.doc.holder.FunDocHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@XStreamAlias("module")
public class ModuleConfiger {

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
        private String type = "list";

        @XStreamImplicit(itemFieldName = "operation")
        private List<OperationConifger> operationList;

    }

    public ModuleConfiger(ClassDocHolder holder, TypeFormatter typeFormatter) {
        this.className = holder.getEntityClass().getSimpleName();
        this.packageName = holder.getEntityClass().getPackage().getName();
        this.des = holder.getClassDoc().value();
        this.moduleID = holder.getModuleID();
        this.operationList = new OperationList();
        List<OperationConifger> operationList = new ArrayList<OperationConifger>();
        for (FunDocHolder funDocHolder : holder.getFunList()) {
            operationList.add(new OperationConifger(funDocHolder, typeFormatter));
        }
        Collections.sort(operationList, new Comparator<OperationConifger>() {

            @Override
            public int compare(OperationConifger o1, OperationConifger o2) {
                return o1.getMethodName().compareTo(o2.getMethodName());
            }

        });
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

    public List<OperationConifger> getOperationList() {
        return operationList.operationList;
    }

}
