package com.tny.game.net.config.properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tny.game.common.config.Config;
import com.tny.game.common.config.ConfigLib;
import com.tny.game.common.utils.json.JSONUtils;
import com.tny.game.log.NetLogger;
import com.tny.game.net.config.BindIp;
import com.tny.game.net.config.ServerConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.*;

/**
 * 服务器属性上下文
 *
 * @author KGTny
 */
public final class PropertiesServerConfig implements ServerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.CONTEXT);

    /**
     * 服务器配置文件键头
     */
    public static final String HEAD_KEY = "tny.server.";

    /**
     * 服务器端口号
     */
    private static final String BIND_IP_KEY_SUFFIX = "config.bind.ips";

    /**
     * 服务器类型
     */
    private static final String SERVER_SCOPE = HEAD_KEY + "scope";

    public final String PROPERTIES_FILE;

    /**
     * Json set bindIp token
     */
    private static final TypeReference<List<BindIp>> SET_BINDIP_TOKEN = new TypeReference<List<BindIp>>() {
    };

    /**
     * 服务器类型
     */
    private String scopeType;
    /**
     * 监听端口号列表
     */
    private Map<String, List<BindIp>> bindMap;
    /**
     * 配置信息Map
     */
    private volatile Config config;

    /**
     * 构造函数
     */
    protected PropertiesServerConfig(String propertiesFile) {
        this.bindMap = new HashMap<>();
        this.PROPERTIES_FILE = propertiesFile;
    }

    //	@Override
    //	public Attributes attributes() {
    //		return this.attributes;
    //	}

    @Override
    public String getScopeType() {
        return this.scopeType;
    }

    @Override
    public List<InetSocketAddress> getBindInetSocketAddressList(String serverName) {
        List<InetSocketAddress> addressList = new ArrayList<>();
        List<BindIp> ipList = this.bindMap.get(this.getBindIpKey(serverName));
        if (ipList == null)
            return null;
        for (BindIp ip : ipList)
            addressList.addAll(ip.createInetSocketAddress());
        return addressList;
    }

    @Override
    public List<BindIp> getBindIp(String name) {
        List<BindIp> ipList = this.bindMap.get(this.getBindIpKey(name));
        if (ipList == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(ipList);
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    private List<BindIp> json2PortsList(final String portsStr) {
        return JSONUtils.toObject(portsStr, SET_BINDIP_TOKEN);
    }

    /**
     * 读取数据
     */
    protected void load() {
        this.load(this.PROPERTIES_FILE);
    }

    private String getBindIpKey(String serverName) {
        if (StringUtils.isBlank(serverName)) {
            serverName = "";
        } else {
            serverName = serverName + ".";
        }
        return HEAD_KEY + serverName + BIND_IP_KEY_SUFFIX;
    }

    private void load(String file) {

        try {

            LOG.info("#PropertiesServiceContext#读取服务器信息配置文件 {}  ", file);
            this.config = ConfigLib.getConfig(file);
            LOG.info("#PropertiesServiceContext#读取服务器信息配置文件 {} 完成 ", file);
            long now = System.currentTimeMillis();
            this.scopeType = this.config.getStr(SERVER_SCOPE);
            this.bindMap = this.json2IpMap();
            LOG.info("#PropertiesServiceContext#读取服务器信息配置文件 {} 完成 | 耗时 {} ", file, System.currentTimeMillis() - now);
        } catch (Exception e) {
            throw new RuntimeException(MessageFormat.format("#PropertiesServiceContext#读取服务器信息配置文件 {0} 异常", file), e);
        }

    }

    private Map<String, List<BindIp>> json2IpMap() {
        final Map<String, List<BindIp>> ipMap = new HashMap<>();
        for (Object keyObject : config.keySet()) {
            final String key = (String) keyObject;
            if (!key.startsWith(HEAD_KEY) || !key.endsWith(BIND_IP_KEY_SUFFIX))
                continue;
            final String ipsStr = config.getStr(key);
            try {
                final List<BindIp> ipList = this.json2PortsList(ipsStr);
                ipMap.put(key, ipList);
            } catch (Exception e) {
                LOG.error("#PropertiesServiceContext#读取系统用户信息 {} 异常", ipsStr, e);
            }
        }
        return ipMap;
    }

}
