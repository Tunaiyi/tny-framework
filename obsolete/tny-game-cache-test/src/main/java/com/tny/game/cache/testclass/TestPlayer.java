package com.tny.game.cache.testclass;

import com.tny.game.asyndb.*;
import com.tny.game.asyndb.annotation.*;
import com.tny.game.cache.annotation.*;
import com.tny.game.cache.async.*;

import java.io.Serializable;

@ToCache(prefix = "player", triggers = TestLinkHandler.class,
        cacheKeys = {"playerId"})
@Persistent(synchronizerClass = CacheSynchronizer.class)
public class TestPlayer implements Serializable {

    final static Operation[] OPERATIONS = new Operation[]{};

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private long playerId;

    private String name;

    //	@Link
    //	private transient List<Equip> equipList = new ArrayList<Equip>();
    //
    //	@Link
    //	private transient Equip equip;
    //
    //	@Link(ignore = true, ignoreOperation = { Operation.SAVE, Operation.DELETE, Operation.UPDATE })
    //	private transient List<Equip> unchangeList = new ArrayList<Equip>();
    //
    //	@Link(ignore = true, ignoreOperation = { Operation.SAVE, Operation.DELETE, Operation.UPDATE })
    //	private transient Equip unchange;

    public TestPlayer() {
    }

    ;

    public TestPlayer(long playerId) {
        this.playerId = playerId;
        this.name = playerId + "";
        //		this.equipList = new ArrayList<Equip>();
        //		this.unchangeList = new ArrayList<Equip>();
    }

    public TestPlayer(long playerId, String name) {
        this.playerId = playerId;
        this.name = name;
        //		this.equip = equip;
        //		this.equipList = new ArrayList<Equip>(equips);
        //		for (Equip e : equips)
        //			this.unchangeList.add(e.coppy());
        //		this.unchange = equip.coppy();
    }

    //	public TestPlayer(long playerId, List<Equip> equips, Equip equip) {
    //		this.playerId = playerId;
    //		this.name = playerId + "";
    //		this.equipList = equips;
    //		this.equip = equip;
    //		for (Equip e : equips)
    //			this.unchangeList.add(e.coppy());
    //		this.unchange = equip.coppy();
    //	}

    public long getPlayerId() {
        return this.playerId;
    }

    //	public void clear() {
    //		this.getEquipList().clear();
    //	}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //
    //	protected void setEquips(List<Equip> equipList) {
    //		this.equipList = equipList;
    //	}
    //
    //	protected void setEquip(Equip equip) {
    //		this.equip = equip;
    //	}
    //
    //	public List<Equip> getEquipList() {
    //		if (this.equipList == null)
    //			this.equipList = new ArrayList<Equip>();
    //		return this.equipList;
    //	}
    //
    //	public Equip getEquip() {
    //		return equip;
    //	}
    //
    //	public List<Equip> getUnchangeList() {
    //		return unchangeList;
    //	}
    //
    //	protected void setUnchangeList(List<Equip> unchangeList) {
    //		this.unchangeList = unchangeList;
    //	}
    //
    //	public Equip getUnchange() {
    //		return unchange;
    //	}
    //
    //	protected void setUnchange(Equip unchange) {
    //		this.unchange = unchange;
    //	}
    //
    //	protected void setEquipList(List<Equip> equipList) {
    //		this.equipList = equipList;
    //	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + (int) (this.playerId ^ (this.playerId >>> 32));
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
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TestPlayer other = (TestPlayer) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.playerId != other.playerId) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(new TestPlayer());
    }

}
