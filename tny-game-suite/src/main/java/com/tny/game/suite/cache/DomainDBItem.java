package com.tny.game.suite.cache;

import com.tny.game.base.item.*;
import com.tny.game.cache.mysql.*;

public class DomainDBItem<R> extends DBCacheItem<R> {

    private Long uid;
    private Integer itemId;
    private Integer number;

    public DomainDBItem(String key, Object data, long version, long millisecond) {
        super(key, data, version, millisecond);
    }

    @Override
    protected void format(Object data) {
        if (data instanceof ProtoItem) {
            ProtoItem proto = (ProtoItem) data;
            Object object = proto.getObject();
            if (object instanceof Item) {
                Item<?> item = (Item<?>) object;
                this.uid = item.getPlayerId();
                this.itemId = item.getItemId();
            } else if (object instanceof Owned) {
                this.uid = ((Owned) object).getPlayerId();
            }
            this.number = proto.getNumber();
            data = proto.getItem();
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

    public Integer getItemId() {
        return this.itemId;
    }

    protected void setUid(Long uid) {
        this.uid = uid;
    }

    protected void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

}
