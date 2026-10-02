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
