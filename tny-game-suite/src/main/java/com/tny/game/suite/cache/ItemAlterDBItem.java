package com.tny.game.suite.cache;

import com.tny.game.base.item.Item;
import com.tny.game.cache.mysql.AlterDBItem;

public class ItemAlterDBItem<R> extends AlterDBItem<R> {

    private Long uid;
    private Integer itemID;
    private Integer number;

    public ItemAlterDBItem(String key, Object data, long version, long millisecond) {
        super(key, data, version, millisecond);
    }

    @Override
    protected void format(Object data) {
        if (data instanceof ProtoItem) {
            ProtoItem item = (ProtoItem) data;
            Item<?> object = item.getItemObject();
            this.uid = object.getPlayerID();
            this.itemID = object.getItemID();
            this.number = item.getNumber();
            data = item.getItem();
        }
        super.format(data);
    }

    private static final long serialVersionUID = 1L;

    public Long getUid() {
        return this.uid;
    }

    public Integer getNumber() {
        return this.number;
    }

    public Integer getItemID() {
        return this.itemID;
    }

    protected void setUid(Long uid) {
        this.uid = uid;
    }

    protected void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

}
