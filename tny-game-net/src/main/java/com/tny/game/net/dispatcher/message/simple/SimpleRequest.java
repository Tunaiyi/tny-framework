package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.dispatcher.NetRequest;
import com.tny.game.net.session.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleRequest extends NetRequest {

    private static final long serialVersionUID = 1L;

    protected int ID;

    protected int proto;

    protected String checkKey;

    protected long time;

    protected List<Object> paramList = new ArrayList<>();

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
    public String getCheckCode() {
        return this.checkKey;
    }

    @Override
    public List<Object> getParamList() {
        return Collections.unmodifiableList(this.paramList);
    }

    public void setParamList(List<?> paramList) {
        this.paramList.addAll(paramList);
    }

    @Override
    protected void setProtocol(int protocol) {
        this.proto = protocol;
    }

    protected void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setCheckKey(String checkKey) {
        this.checkKey = checkKey;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    protected void addParam(int index, Object parameter) {
        this.paramList.add(index, parameter);
    }

    @Override
    protected void addParam(Object parameter) {
        this.paramList.add(parameter);
    }

    @Override
    protected void addAllParam(List<Object> parameterList) {
        this.paramList.addAll(parameterList);
    }

    public Object[] getParams() {
        return this.paramList.toArray(new Object[0]);
    }

    @Override
    protected void setID(int id) {
        this.ID = id;
    }

    @Override
    public int getProtocol() {
        return proto;
    }

    @Override
    public String toString() {
        return "SimpleRequest [id=" + this.ID + ", hostName=" + this.getID() + ", proto=" + this.proto + ", checkKey=" + this.checkKey + ", time=" + this.time
                + ", paramList=" + this.paramList + "]";
    }

}
