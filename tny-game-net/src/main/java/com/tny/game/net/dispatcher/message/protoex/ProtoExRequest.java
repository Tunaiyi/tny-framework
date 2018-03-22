package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.net.dispatcher.NetRequest;
import com.tny.game.protoex.ProtoExEnum;
import com.tny.game.protoex.annotations.Packed;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExConf;
import com.tny.game.protoex.annotations.ProtoExElement;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.protoex.annotations.TypeEncode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ProtoEx(ProtoExMessageCoder.REQUEST_ID)
public class ProtoExRequest extends NetRequest {

    private static final long serialVersionUID = 1L;

    @ProtoExField(1)
    protected int ID;

    @ProtoExField(2)
    protected int protocol = -1;

    @ProtoExField(3)
    protected String checkKey;

    @ProtoExField(4)
    protected long time = -1;

    @ProtoExField(5)
    @Packed(false)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    protected List<Object> paramList = new ArrayList<>();

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
        return this.getParamList0().size();
    }

    @Override
    protected Object getParam(int index) {
        return this.getParamList0().get(index);
    }

    @Override
    public String getCheckKey() {
        return this.checkKey;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public List<Object> getParamList() {
        return Collections.unmodifiableList(this.getParamList0());
    }

    private List<Object> getParamList0() {
        return this.paramList;
    }

    public Object[] getParams() {
        return this.getParamList().toArray(new Object[0]);
    }

    @Override
    protected <T> T parseParam(int index, Class<T> clazz, Object object) {
        if (object instanceof Number && ProtoExEnum.class.isAssignableFrom(clazz) && Enum.class.isAssignableFrom(clazz)) {
            for (T value : clazz.getEnumConstants()) {
                if (((ProtoExEnum) value).getID() == ((Number) object).intValue())
                    return value;
            }
        }
        return super.parseParam(index, clazz, object);
    }

    @Override
    protected void setCheckKey(String checkKey) {
        this.checkKey = checkKey;
    }

    @Override
    protected void setID(int id) {
        this.ID = id;
    }

    @Override
    protected void setParamList(List<?> paramList) {
        this.paramList.addAll(paramList);
    }

    @Override
    protected void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void setTime(long time) {
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

    @Override
    public String toString() {
        return "ProtoRequest [ID=" + this.ID + ", protocol=" + this.protocol + ", checkKey=" + this.checkKey
                + ", time=" + this.time + ", paramList="
                + this.paramList + "]";
    }

}