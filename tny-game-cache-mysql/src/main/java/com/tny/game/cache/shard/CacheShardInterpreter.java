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