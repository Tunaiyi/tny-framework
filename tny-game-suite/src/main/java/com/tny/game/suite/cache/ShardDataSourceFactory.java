package com.tny.game.suite.cache;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.collect.Maps;
import com.tny.game.common.utils.Logs;
import com.tny.game.common.config.FileLoader;
import net.paoding.rose.jade.context.spring.SpringDataSourceFactory;
import net.paoding.rose.jade.dataaccess.DataSourceFactory;
import net.paoding.rose.jade.dataaccess.DataSourceHolder;
import net.paoding.rose.jade.shard.ShardInterpreter;
import net.paoding.rose.jade.statement.StatementMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ShardDataSourceFactory implements DataSourceFactory, ApplicationContextAware {

    public static final Logger LOGGER = LoggerFactory.getLogger(ShardDataSourceFactory.class);

    private String dbUrlTemplate = "jdbc:mysql://{}:{}/{}?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&failOverReadOnly=false&rewriteBatchedStatements=true";

    private Map<Object, ShardDataSourceHolder> holderMap = new ConcurrentHashMap<>();

    private DataSourceFactory defaultFactory;

    private volatile Properties properties;

    public ShardDataSourceFactory(String path) throws Exception {
        FileLoader loader = new FileLoader(path) {

            @Override
            protected void doLoad(InputStream inputStream, boolean reload) throws Exception {
                Properties properties = new Properties();
                properties.load(inputStream);
                ShardDataSourceFactory.this.properties = properties;
                ShardDataSourceFactory.this.holderMap.values().forEach(ShardDataSourceHolder::reload);
            }
        };
        loader.load();
    }

    public void register(Object id, String host, int port, String db) {
        ShardDataSourceHolder holder = this.holderMap.get(id);
        if (holder == null) {
            synchronized (this) {
                holder = this.holderMap.get(id);
                if (holder == null) {
                    try {
                        holder = new ShardDataSourceHolder(id, host, port, db);
                        this.holderMap.put(id, holder);
                    } catch (Exception e) {
                        LOGGER.error("创建 [s{}] 数据源 {}:{}/{} 发生异常", id, host, port, db, e);
                    }
                    return;
                }
            }
        }
        holder.reload(host, port, db);
    }

    @Override
    public DataSourceHolder getHolder(StatementMetaData metaData, Map<String, Object> runtime) {
        DataSourceHolder holder = null;
        Object shardObject = runtime.get(ShardInterpreter.SHARD_BY_KEY);
        if (shardObject != null && shardObject instanceof ShardTable)
            holder = this.holderMap.get(((ShardTable) shardObject).getSid());
        if (holder == null)
            return this.defaultFactory.getHolder(metaData, runtime);
        return holder;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.defaultFactory = new SpringDataSourceFactory(applicationContext);
    }

    private class ShardDataSourceHolder extends DataSourceHolder {

        private volatile Properties properties;

        private volatile Object serverID;

        private volatile String host;

        private volatile int port;

        private volatile String name;

        private DruidDataSource druidDataSource;

        public ShardDataSourceHolder(Object serverID, String host, int port, String name) throws Exception {
            this.serverID = serverID;
            this.host = host;
            this.port = port;
            this.name = name;
            Properties properties = this.createProperties(host, port, name);
            DruidDataSource dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("创建 [s{}] 数据源 {}:{}/{} 发生[成功]!", serverID, host, port, name);
            this.init(dataSource);
            this.druidDataSource = dataSource;
            this.properties = properties;
        }

        private Properties createProperties(String host, Integer port, String name) {
            Properties defProperties = ShardDataSourceFactory.this.properties;
            Properties properties = new Properties();
            properties.putAll(defProperties);
            String url = Logs.format(ShardDataSourceFactory.this.dbUrlTemplate,
                    host == null ? this.host : host,
                    port == null ? this.port : port,
                    name == null ? this.name : name);
            properties.put(DruidDataSourceFactory.PROP_URL, url);
            if (properties.get(DruidDataSourceFactory.PROP_PASSWORD) == null)
                properties.setProperty(DruidDataSourceFactory.PROP_PASSWORD, "");
            return properties;
        }

        private void reload() {
            this.reload(null, null, null);
        }

        private synchronized void reload(String host, Integer port, String name) {
            Properties properties = this.createProperties(host, port, name);
            if (properties == null || Maps.difference(this.properties, properties).areEqual()) {
                try {
                    if (properties == null)
                        properties = new Properties();
                    this.druidDataSource.restart();
                    DruidDataSourceFactory.config(this.druidDataSource, properties);
                    this.properties = properties;
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("重新配置 [s{}] 数据源 {}:{}/{} 发生[成功]!", this.serverID, this.host, this.port, this.name);
                } catch (SQLException e) {
                    LOGGER.error("重新配置 [s{}] 数据源 {}:{}/{} 发生异常",
                            this.serverID, this.host, this.port, this.name, e);
                }
            }
        }

    }

    public static void main(String[] args) {
        Map<Object, Object> map1 = new HashMap<>();
        Map<Object, Object> map2 = new Properties();
        map1.put(1, "1");
        map1.put(2, "2");
        map1.put(3, "3");
        map2.put(1, "1");
        map2.put(2, "2");
        map2.put(3, "3");
        System.out.println(Maps.difference(map1, map2).areEqual());
    }
}
