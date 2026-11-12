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
package com.tny.game.batch;

import net.paoding.rose.jade.annotation.*;

import java.util.Collection;

@DAO(catalog = "cacheShardStrategy")
public interface UpObjectDAO {

    public static final String TABLE = "`UpObject`";
    public static final String FIELD = "`id`, `name`, `age`, `gender`";

    @SQL("SELECT " + FIELD + " FROM " + TABLE + " where `id` in (:keys)")
    public Collection<UpObject> get(@SQLParam("keys") Collection<Integer> keys);

    @SQL("INSERT INTO " + TABLE + " (" + FIELD + ") VALUES (:i.id, :i.name, :i.age, :i.gender)")
    public int insert(@SQLParam("i") UpObject object);

    @SQL("INSERT IGNORE INTO " + TABLE + " (" + FIELD + ") VALUES (:i.id, :i.name, :i.age, :i.gender)")
    public boolean insertIgnore(@SQLParam("i") UpObject object);

    @SQL("INSERT INTO " + TABLE + " (" + FIELD + ") VALUES (:i.id, :i.name, :i.age, :i.gender)")
    public int[] insert(@SQLParam("i") Collection<UpObject> objects);

    @SQL("REPLACE INTO " + TABLE + " (" + FIELD + ") VALUES (:i.id, :i.name, :i.age, :i.gender)")
    public int[] set(@SQLParam("i") Collection<UpObject> objects);

    @SQL("UPDATE " + TABLE + " SET `name`=:i.name, `age`=:i.age, `gender`=:i.gender where `id` = :i.id")
    public int update(@SQLParam("i") Collection<UpObject> item);

    @SQL("TRUNCATE TABLE " + TABLE + ";")
    public void flushAll();

}
