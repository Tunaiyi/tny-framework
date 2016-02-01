package com.tny.game.net.config;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BindIp {

    /**
     * 校验值
     */
    private String ip;

    /**
     * ip列表
     */
    private List<Integer> ports = new ArrayList<Integer>();

    public BindIp() {
    }

    public Set<InetSocketAddress> createInetSocketAddress() {
        Set<InetSocketAddress> ipSet = new HashSet<InetSocketAddress>();
        for (int port : ports)
            ipSet.add(new InetSocketAddress(ip, port));
        return ipSet;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BindIp [ip=" + ip + ", ports=" + ports + "]";
    }

}
