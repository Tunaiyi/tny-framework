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

package com.tny.game.data.mongodb.repositories;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.*;

import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

/**
 * Created by Kun Yang on 2020/3/1.
 */
@NoRepositoryBean
public interface UpdatableRepository<T, ID> extends Repository<T, ID> {

    default T loadOrCreate(ID id, T document) {
        return loadOrCreate(query(where("_id").is(id)), document);
    }

    default T loadOrCreate(Map<String, Object> query, T document) {
        return loadOrCreate(query(byExample(query)), document);
    }

    T loadOrCreate(Query query, T document);

    T findAndSave(Query query, T document);

    T findAndUpdate(Query query, T document);

}
