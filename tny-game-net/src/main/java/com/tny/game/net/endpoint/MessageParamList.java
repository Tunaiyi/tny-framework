package com.tny.game.net.endpoint;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 12:13 上午
 */
public class MessageParamList extends AbstractList<Object> {

    private final List<Object> objectList;

    public MessageParamList(List<Object> objectList) {
        this.objectList = objectList;
    }

    public MessageParamList(Object... params) {
        this.objectList = Arrays.asList(params);
    }

    @Override
    public Object get(int index) {
        return this.objectList.get(index);
    }

    @Override
    public int size() {
        return this.objectList.size();
    }

}
