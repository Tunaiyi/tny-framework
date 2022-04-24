package com.tny.game.cache.testclass;

import com.tny.game.asyndb.annotation.*;
import com.tny.game.cache.annotation.*;
import com.tny.game.cache.async.*;

import java.io.Serializable;

@ToCache(prefix = "equip", triggers = TestLinkHandler.class,
        cacheKeys = {"playerId", "id"})
@Persistent(synchronizerClass = CacheSynchronizer.class)
public class Equip implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private long playerId;

    private int id;

    private String name;

    public Equip() {
    }

    public Equip(int id, long playerId, String name) {
        super();
        this.id = id;
        this.playerId = playerId;
        this.name = name;
    }

    public Equip coppy() {
        return new Equip(id + 20000000, playerId, name);
    }

    public long getPlayerId() {
        return playerId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int)(playerId ^ (playerId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Equip other = (Equip)obj;
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (playerId != other.playerId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Equip [playerId=" + playerId + ", id=" + id + ", name=" + name + "]";
    }

}
