/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.url;

import com.tny.game.common.collection.*;
import com.tny.game.common.utils.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by Kun Yang on 2017/9/7.
 */

public final class URL implements Serializable {

    private static final String BACKUP_KEY = "backup";

    private static final String DEFAULT_KEY_PREFIX = "default.";

    private static final String ANY_HOST_KEY = "anyhost";

    private static final String ANY_HOST_VALUE = "0.0.0.0";

    private static final String LOCALHOST_KEY = "localhost";

    private static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    private static final long serialVersionUID = -1985165475234910535L;

    private final String scheme;

    private final String username;

    private final String password;

    private final String host;

    private final int port;

    private final String path;

    private final Map<String, String> parameters;

    // ==== cache ====

    private volatile transient Map<String, Number> numbers;

    private volatile transient Map<String, URL> urls;

    private volatile transient String ip;

    private volatile transient String full;

    private volatile transient String identity;

    private volatile transient String parameter;

    private volatile transient String string;

    public URL() {
        this.scheme = null;
        this.username = null;
        this.password = null;
        this.host = null;
        this.port = 0;
        this.path = null;
        this.parameters = null;
    }

    public URL(String scheme, String host, int port) {
        this(scheme, null, null, host, port, null, (Map<String, String>)null);
    }

    public URL(String scheme, String host, int port, String[] pairs) { // 变长参数...与下面的path参数冲突，改为数组
        this(scheme, null, null, host, port, null, CollectionAide.toStringMap(pairs));
    }

    public URL(String scheme, String host, int port, Map<String, String> parameters) {
        this(scheme, null, null, host, port, null, parameters);
    }

    public URL(String scheme, String host, int port, String path) {
        this(scheme, null, null, host, port, path, (Map<String, String>)null);
    }

    public URL(String scheme, String host, int port, String path, String... pairs) {
        this(scheme, null, null, host, port, path, CollectionAide.toStringMap(pairs));
    }

    public URL(String scheme, String host, int port, String path, Map<String, String> parameters) {
        this(scheme, null, null, host, port, path, parameters);
    }

    public URL(String scheme, String username, String password, String host, int port, String path) {
        this(scheme, username, password, host, port, path, (Map<String, String>)null);
    }

    public URL(String scheme, String username, String password, String host, int port, String path, String... pairs) {
        this(scheme, username, password, host, port, path, CollectionAide.toStringMap(pairs));
    }

    public URL(String scheme, String username, String password, String host, int port, String path, Map<String, String> parameters) {
        if ((username == null || username.length() == 0)
                && password != null && password.length() > 0) {
            throw new IllegalArgumentException("Invalid url, password without username!");
        }
        this.scheme = scheme;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = Math.max(port, 0);
        // trim the beginning "/"
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path;
        if (parameters == null) {
            parameters = new HashMap<>();
        } else {
            parameters = new HashMap<>(parameters);
        }
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    /**
     * Parse url string
     *
     * @param url URL string
     * @return URL instance
     * @see URL
     */
    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String username = null;
        String password = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = null;
        int i = url.indexOf("?"); // seperator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("&");
            parameters = new HashMap<>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) {
                throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            }
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) {
                    throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                }
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.indexOf("@");
        if (i >= 0) {
            username = url.substring(0, i);
            int j = username.indexOf(":");
            if (j >= 0) {
                password = username.substring(j + 1);
                username = username.substring(0, j);
            }
            url = url.substring(i + 1);
        }
        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) {
            host = url;
        }
        return new URL(protocol, username, password, host, port, path, parameters);
    }

    public static String encode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getScheme() {
        return this.scheme;
    }

    public URL setScheme(String scheme) {
        return new URL(scheme, this.username, this.password, this.host, this.port, this.path, getParameters());
    }

    public String getUsername() {
        return this.username;
    }

    public URL setUsername(String username) {
        return new URL(this.scheme, username, this.password, this.host, this.port, this.path, getParameters());
    }

    public String getPassword() {
        return this.password;
    }

    public URL setPassword(String password) {
        return new URL(this.scheme, this.username, password, this.host, this.port, this.path, getParameters());
    }

    public String getAuthority() {
        if ((this.username == null || this.username.length() == 0)
                && (this.password == null || this.password.length() == 0)) {
            return null;
        }
        return (this.username == null ? "" : this.username)
                + ":" + (this.password == null ? "" : this.password);
    }

    public String getHost() {
        return this.host;
    }

    public URL setHost(String host) {
        return new URL(this.scheme, this.username, this.password, host, this.port, this.path, getParameters());
    }

    /**
     * 获取IP地址.
     * <p>
     * 请注意：
     * 如果和Socket的地址对比，
     * 或用地址作为Map的Key查找，
     * 请使用IP而不是Host，
     * 否则配置域名会有问题
     *
     * @return ip
     */
    public String getIp() {
        if (this.ip == null) {
            this.ip = IpAide.getIpByHost(this.host);
        }
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public URL setPort(int port) {
        return new URL(this.scheme, this.username, this.password, this.host, port, this.path, getParameters());
    }

    public int getPort(int defaultPort) {
        return this.port <= 0 ? defaultPort : this.port;
    }

    public String getAddress() {
        return this.port <= 0 ? this.host : this.host + ":" + this.port;
    }

    public URL setAddress(String address) {
        int i = address.lastIndexOf(':');
        String host;
        int port = this.port;
        if (i >= 0) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
        }
        return new URL(this.scheme, this.username, this.password, host, port, this.path, getParameters());
    }

    public String getBackupAddress() {
        return getBackupAddress(0);
    }

    private String getBackupAddress(int defaultPort) {
        StringBuilder address = new StringBuilder(appendDefaultPort(getAddress(), defaultPort));
        String[] backups = getParameter(BACKUP_KEY, new String[0]);
        if (backups != null && backups.length > 0) {
            for (String backup : backups) {
                address.append(",");
                address.append(appendDefaultPort(backup, defaultPort));
            }
        }
        return address.toString();
    }

    public List<URL> getBackupUrls() {
        List<URL> urls = new ArrayList<>();
        urls.add(this);
        String[] backups = getParameter(BACKUP_KEY, new String[0]);
        if (backups != null && backups.length > 0) {
            for (String backup : backups) {
                urls.add(this.setAddress(backup));
            }
        }
        return urls;
    }

    private String appendDefaultPort(String address, int defaultPort) {
        if (address != null && address.length() > 0
                && defaultPort > 0) {
            int i = address.indexOf(':');
            if (i < 0) {
                return address + ":" + defaultPort;
            } else if (Integer.parseInt(address.substring(i + 1)) == 0) {
                return address.substring(0, i + 1) + defaultPort;
            }
        }
        return address;
    }

    public String getPath() {
        return this.path;
    }

    public URL setPath(String path) {
        return new URL(this.scheme, this.username, this.password, this.host, this.port, path, getParameters());
    }

    public String getAbsolutePath() {
        if (this.path != null && !this.path.startsWith("/")) {
            return "/" + this.path;
        }
        return this.path;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    private String getParameterAndDecoded(String key) {
        return getParameterAndDecoded(key, null);
    }

    private String getParameterAndDecoded(String key, String defaultValue) {
        return decode(getParameter(key, defaultValue));
    }

    private String getParameter(String key) {
        String value = this.parameters.get(key);
        if (value == null || value.length() == 0) {
            value = this.parameters.get(DEFAULT_KEY_PREFIX + key);
        }
        return value;
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }

    private String[] getParameter(String key, String[] defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return COMMA_SPLIT_PATTERN.split(value);
    }

    private Map<String, Number> getNumbers() {
        if (this.numbers == null) { // 允许并发重复创建
            this.numbers = new ConcurrentHashMap<>();
        }
        return this.numbers;
    }

    private Map<String, URL> getUrls() {
        if (this.urls == null) { // 允许并发重复创建
            this.urls = new ConcurrentHashMap<>();
        }
        return this.urls;
    }

    public URL getUrlParameter(String key) {
        URL u = getUrls().get(key);
        if (u != null) {
            return u;
        }
        String value = getParameterAndDecoded(key);
        if (value == null || value.length() == 0) {
            return null;
        }
        u = URL.valueOf(value);
        getUrls().put(key, u);
        return u;
    }

    public double getParameter(String key, double defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.doubleValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        double d = Double.parseDouble(value);
        getNumbers().put(key, d);
        return d;
    }

    public float getParameter(String key, float defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.floatValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        float f = Float.parseFloat(value);
        getNumbers().put(key, f);
        return f;
    }

    public long getParameter(String key, long defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.longValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        long l = Long.parseLong(value);
        getNumbers().put(key, l);
        return l;
    }

    public int getParameter(String key, int defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.intValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        getNumbers().put(key, i);
        return i;
    }

    public short getParameter(String key, short defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.shortValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        short s = Short.parseShort(value);
        getNumbers().put(key, s);
        return s;
    }

    public byte getParameter(String key, byte defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.byteValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        byte b = Byte.parseByte(value);
        getNumbers().put(key, b);
        return b;
    }

    public char getParameter(String key, char defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value.charAt(0);
    }

    public boolean getParameter(String key, boolean defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public boolean hasParameter(String key) {
        String value = getParameter(key);
        return value != null && value.length() > 0;
    }

    public boolean isLocalHost() {
        return IpAide.isLocalHost(this.host) || getParameter(LOCALHOST_KEY, false);
    }

    public boolean isAnyHost() {
        return ANY_HOST_VALUE.equals(this.host) || getParameter(ANY_HOST_KEY, false);
    }

    public String getRawParameter(String key) {
        if ("protocol".equals(key)) {
            return this.scheme;
        }
        if ("username".equals(key)) {
            return this.username;
        }
        if ("password".equals(key)) {
            return this.password;
        }
        if ("host".equals(key)) {
            return this.host;
        }
        if ("port".equals(key)) {
            return String.valueOf(this.port);
        }
        if ("path".equals(key)) {
            return this.path;
        }
        return getParameter(key);
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>(this.parameters);
        if (this.scheme != null) {
            map.put("protocol", this.scheme);
        }
        if (this.username != null) {
            map.put("username", this.username);
        }
        if (this.password != null) {
            map.put("password", this.password);
        }
        if (this.host != null) {
            map.put("host", this.host);
        }
        if (this.port > 0) {
            map.put("port", String.valueOf(this.port));
        }
        if (this.path != null) {
            map.put("path", this.path);
        }
        return map;
    }

    @Override
    public String toString() {
        if (this.string != null) {
            return this.string;
        }
        return this.string = buildString(false, true); // no show username and password
    }

    public String toString(String... parameters) {
        return buildString(false, true, parameters); // no show username and password
    }

    public String toIdentityString() {
        if (this.identity != null) {
            return this.identity;
        }
        return this.identity = buildString(true, false); // only return identity message, see the method "equals" and "hashCode"
    }

    public String toIdentityString(String... parameters) {
        return buildString(true, false, parameters); // only return identity message, see the method "equals" and "hashCode"
    }

    public String toFullString() {
        if (this.full != null) {
            return this.full;
        }
        return this.full = buildString(true, true);
    }

    public String toFullString(String... parameters) {
        return buildString(true, true, parameters);
    }

    public String toParameterString() {
        if (this.parameter != null) {
            return this.parameter;
        }
        return this.parameter = toParameterString(new String[0]);
    }

    private String toParameterString(String... parameters) {
        StringBuilder buf = new StringBuilder();
        buildParameters(buf, false, parameters);
        return buf.toString();
    }

    private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
        if (getParameters() != null && getParameters().size() > 0) {
            List<String> includes = (parameters == null || parameters.length == 0 ? null : Arrays.asList(parameters));
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<>(getParameters()).entrySet()) {
                if (entry.getKey() != null && entry.getKey().length() > 0
                        && (includes == null || includes.contains(entry.getKey()))) {
                    if (first) {
                        if (concat) {
                            buf.append("?");
                        }
                        first = false;
                    } else {
                        buf.append("&");
                    }
                    buf.append(entry.getKey());
                    buf.append("=");
                    buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
    }

    private String buildString(boolean appendUser, boolean appendParameter, String... parameters) {
        return buildString(appendUser, appendParameter, false, parameters);
    }

    private String buildString(boolean appendUser, boolean appendParameter, boolean useIP, String... parameters) {
        StringBuilder buf = new StringBuilder();
        if (this.scheme != null && this.scheme.length() > 0) {
            buf.append(this.scheme);
            buf.append("://");
        }
        if (appendUser && this.username != null && this.username.length() > 0) {
            buf.append(this.username);
            if (this.password != null && this.password.length() > 0) {
                buf.append(":");
                buf.append(this.password);
            }
            buf.append("@");
        }
        String host;
        if (useIP) {
            host = getIp();
        } else {
            host = getHost();
        }
        if (host != null && host.length() > 0) {
            buf.append(host);
            if (this.port > 0) {
                buf.append(":");
                buf.append(this.port);
            }
        }
        String path = getPath();
        if (path != null && path.length() > 0) {
            buf.append("/");
            buf.append(path);
        }
        if (appendParameter) {
            buildParameters(buf, true, parameters);
        }
        return buf.toString();
    }

    public java.net.URL toJavaURL() {
        try {
            return new java.net.URL(toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.host == null) ? 0 : this.host.hashCode());
        result = prime * result + ((this.parameters == null) ? 0 : this.parameters.hashCode());
        result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
        result = prime * result + ((this.path == null) ? 0 : this.path.hashCode());
        result = prime * result + this.port;
        result = prime * result + ((this.scheme == null) ? 0 : this.scheme.hashCode());
        result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        URL other = (URL)obj;
        if (this.host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!this.host.equals(other.host)) {
            return false;
        }
        if (this.parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!this.parameters.equals(other.parameters)) {
            return false;
        }
        if (this.password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!this.password.equals(other.password)) {
            return false;
        }
        if (this.path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!this.path.equals(other.path)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        if (this.scheme == null) {
            if (other.scheme != null) {
                return false;
            }
        } else if (!this.scheme.equals(other.scheme)) {
            return false;
        }
        if (this.username == null) {
            return other.username == null;
        } else {
            return this.username.equals(other.username);
        }
    }

}