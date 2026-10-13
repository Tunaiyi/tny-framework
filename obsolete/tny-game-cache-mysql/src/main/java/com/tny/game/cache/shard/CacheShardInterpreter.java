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
package com.tny.game.cache.shard;

import com.tny.game.cache.*;
import com.tny.game.cache.mysql.dao.*;
import net.paoding.rose.jade.shard.ShardInterpreter;
import net.paoding.rose.jade.statement.StatementRuntime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

@Order(-20)
public class CacheShardInterpreter<T> extends ShardInterpreter<T> {

    protected CacheShardInterpreter(Class<T> clazz) {
        super(clazz);
    }

    protected String getTable(T param) {
        if (param instanceof String) {
            String key = param.toString();
            String headPre = CacheUtils.getKeyHeadPre();
            int end = key.indexOf(CacheUtils.getSeparator());
            if (end < 0) {
                return key;
            }
            return StringUtils.substring(key, headPre.length(), end);
        }
        return null;
    }

    /**
     * 重新实现此方法自定义转化规则
     *
     * @param term
     * @param runtime
     * @return
     */
    protected String convert(String term, int shardIndex) {
        return term.substring(1) + "_" + shardIndex;
    }

    @Override
    public void doInterpret(StatementRuntime runtime, T object) {
        Class<?> daoClass = runtime.getMetaData().getDAOMetaData().getDAOClass();
        if (!ShardCacheDAO.class.isAssignableFrom(daoClass)) {
            return;
        }
        String sql = runtime.getSQL();
        String table = this.getTable(object);
        if (table != null) {
            sql = StringUtils.replace(sql, ShardCacheDAO.TABLE_PLACEHOLDER, table);
            runtime.setSQL(sql);
        }
    }

}