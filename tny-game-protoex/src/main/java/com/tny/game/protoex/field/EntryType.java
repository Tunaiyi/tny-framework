package com.tny.game.protoex.field;

import com.tny.game.protoex.*;

/**
 * map键值对类型
 *
 * @author KGTny
 */
public enum EntryType {

    /**
     * 键
     */
    KEY(1),

    /**
     * 值
     */
    VALUE(2);

    private final int index;

    private EntryType(int index) {
        this.index = index;
    }

    public int getFieldIndex() {
        return this.index;
    }

    public boolean isType(Tag tag) {
        return tag.getFieldNumber() == this.index;
    }

}
