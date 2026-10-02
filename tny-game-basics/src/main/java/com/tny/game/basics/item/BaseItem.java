/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
