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
