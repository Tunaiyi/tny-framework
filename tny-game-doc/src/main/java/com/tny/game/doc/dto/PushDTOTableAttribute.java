package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.table.*;

import java.util.*;

public class PushDTOTableAttribute implements TableAttribute {

    private PushDTOList pushDTOList = new PushDTOList();

    @XStreamAlias("pushDTOList")
    private static class PushDTOList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String type = "list";

        @XStreamImplicit(itemFieldName = "pushDTO")
        private SortedSet<PushDTOInfo> pushDTOList = new TreeSet<PushDTOInfo>();
        ;

    }

    public SortedSet<PushDTOInfo> getConfigerSet() {
        return Collections.unmodifiableSortedSet(pushDTOList.pushDTOList);
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        this.pushDTOList.pushDTOList.add(new PushDTOInfo(clazz));
    }

    @Override
    public Object getContent() {
        return pushDTOList;
    }

}
