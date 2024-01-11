package com.tny.game.cache.testclass;

import java.io.Serializable;
import java.util.*;

public class PlayerTest implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public long playerId;

    public List<Integer> ids = new ArrayList<Integer>();

    public transient List<Equip> equipList = new ArrayList<Equip>();

    public PlayerTest(int flag, long playerId, Integer... ids) {
        this.playerId = playerId;
        this.ids = Arrays.asList(ids);
    }

    public long getPlayerId() {
        return playerId;
    }

    public List<Integer> getIds1() {
        return ids;
    }

    public List<Integer> getIds2() {
        return ids;
    }

    public List<Integer> getIdError1() {
        return ids;
    }

    public List<Integer> getIdError2(Long id) {
        return ids;
    }

    public void getIdError3() {
    }

    public List<Integer> getIdError4() {
        return ids;
    }

    public List<Integer> getIdError5() {
        return ids;
    }

    public List<Integer> getIdError6() {
        return ids;
    }

    public void clear() {
        this.equipList.clear();
    }

    public void getPlayerAge() {
    }

    public List<Equip> getEquipList() {
        return equipList;
    }

    protected void setEquips(Collection<Equip> equipList) {
        this.equipList.addAll(equipList);
    }

    protected void setEquip(Equip equip) {
        this.equipList.add(equip);
    }

}
