/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

/**
 * 抽象事物接口
 *
 * @param <IM>
 * @author KGTny
 */
public abstract class BaseItem<IM extends ItemModel> implements Item<IM> {

    /**
     * 事物所属玩家id
     */
    protected long playerId;

    /**
     * 事物模型
     */
    protected IM model;

    /**
     *
     */
    private transient volatile AnyId unid;

    protected BaseItem() {
    }

    protected BaseItem(long playerId, IM model) {
        this.setPlayerId(playerId);
        this.setModel(model);
    }

    /**
     * @return 全局唯一id
     */
    @Override
    public AnyId getAnyId() {
        if (unid == null) {
            unid = AnyId.idOf(this);
        }
        return unid;
    }

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    @Override
    public int getModelId() {
        return this.model.getId();
    }

    @Override
    public String getAlias() {
        return this.model.getAlias();
    }

    @Override
    public ItemType getItemType() {
        return this.getModel().getItemType();
    }

    @Override
    public IM getModel() {
        return this.model;
    }

    protected void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    protected void setModel(IM model) {
        this.model = model;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.model == null) ? 0 : this.getModel().hashCode());
        result = prime * result + (int) (this.playerId ^ (this.playerId >>> 32));
        result = prime * result + (int) this.getId();
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        BaseItem other = (BaseItem) obj;
        if (this.model == null) {
            if (other.model != null) {
                return false;
            }
        } else if (!this.getModel().equals(other.model)) {
            return false;
        }
        if (this.playerId != other.playerId) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AbstractItem [playerId=" + this.playerId + ", model=" + this.model + "]";
    }

}
