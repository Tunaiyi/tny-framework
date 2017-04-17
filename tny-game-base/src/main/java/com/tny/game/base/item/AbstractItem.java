package com.tny.game.base.item;

/**
 * 抽象事物接口
 *
 * @param <IM>
 * @author KGTny
 */
public abstract class AbstractItem<IM extends ItemModel> implements Item<IM> {

    /**
     * 事物所属玩家id
     */
    protected long playerID;

    /**
     * 事物模型
     */
    protected IM model;

    @Override
    public long getPlayerID() {
        return this.playerID;
    }

    @Override
    public int getItemID() {
        return model.getID();
    }

    @Override
    public String getAlias() {
        return model.getAlias();
    }

    @Override
    public <IT extends ItemType> IT getItemType() {
        return this.getModel().getItemType();
    }

    @Override
    public IM getModel() {
        return this.model;
    }


    protected void setPlayerID(long playerID) {
        this.playerID = playerID;
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
        result = prime * result + (int) (this.playerID ^ (this.playerID >>> 32));
        result = prime * result + (int) this.getID();
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        AbstractItem other = (AbstractItem) obj;
        if (this.model == null) {
            if (other.model != null)
                return false;
        } else if (!this.getModel().equals(other.model))
            return false;
        if (this.playerID != other.playerID)
            return false;
        if (this.getID() != other.getID())
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AbstractItem [playerID=" + this.playerID + ", model=" + this.model + "]";
    }

}
