package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.dispatcher.NetRequest;
import com.tny.game.net.dispatcher.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleRequest extends NetRequest {

    private static final long serialVersionUID = 1L;

    protected int ID;

    protected Object module;

    protected Object operation;

    protected String checkKey;

    protected long time;

    protected List<Object> paramList = new ArrayList<Object>();

    public SimpleRequest() {
    }

    protected SimpleRequest(Session session) {
        this.session = session;
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public int size() {
        return this.paramList.size();
    }

    @Override
    protected Object getParam(int index) {
        return this.paramList.get(index);
    }

    @Override
    public String getCheckKey() {
        return this.checkKey;
    }

    public Object getModule() {
        return this.module;
    }

    public Object getOperation() {
        return this.operation;
    }

    @Override
    public List<Object> getParamList() {
        return Collections.unmodifiableList(this.paramList);
    }

    public void setParamList(List<?> paramList) {
        this.paramList.addAll(paramList);
    }

    public void setModule(Object module) {
        this.module = module;
    }

    protected void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public void setOperation(Object operation) {
        this.operation = operation;
    }

    public void setCheckKey(String checkKey) {
        this.checkKey = checkKey;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Object[] getParams() {
        return this.paramList.toArray(new Object[0]);
    }

    public void setID(int id) {
        this.ID = id;
    }

    @Override
    public int getProtocol() {
        return (Integer) operation;
    }

    @Override
    public String toString() {
        return "SimpleRequest [id=" + this.ID + ", hostName=" + this.getID() + ", module=" + this.module + ", operation=" + this.operation + ", checkKey=" + this.checkKey + ", time=" + this.time
                + ", paramList=" + this.paramList + "]";
    }

}
