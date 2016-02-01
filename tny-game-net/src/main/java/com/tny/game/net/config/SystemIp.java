package com.tny.game.net.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ip信息类
 *
 * @author KGTny
 */
public final class SystemIp {

    /**
     * ip列表
     */
    private List<String> ip;

    public SystemIp() {
        super();
    }

    /**
     * 构造器
     *
     * @param name     ip类型名称
     * @param ipList   ip列表
     * @param checkKey 校验值
     */
    public SystemIp(final List<String> ip, final String key) {
        super();
        this.ip = new ArrayList<String>();
        if (ip != null) {
            this.ip.addAll(ip);
        }
    }

    /**
     * 获取不修改ip列表
     *
     * @return the ipList
     */
    public List<String> getIpList() {
        return Collections.unmodifiableList(this.ip);
    }

    /**
     * 是否包含有ip
     *
     * @param 检查的ip
     * @return 返回是否包含ip 包含返回: true 不包含返回: false
     */
    public boolean isInclude(final String ipValue) {
        return this.ip.indexOf(ipValue) > -1;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(-829812303, 569781687)
                .appendSuper(super.hashCode())
                .append(this.ip).toHashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IpContent [ipList=" + ip + "]";
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (!(object instanceof SystemIp)) {
            return false;
        }
        if (this == object)
            return true;
        SystemIp rhs = (SystemIp) object;
        return new EqualsBuilder()
                .append(this.ip, rhs.ip)
                .isEquals();
    }

}
