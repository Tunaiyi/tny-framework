package com.tny.game.suite.login;

public class LoginResult {

    private String account;

    private String ip;

    private int port;

    private String ticket;

    public LoginResult(String account, String ip, int port, String ticket) {
        this.account = account;
        this.ip = ip;
        this.port = port;
        this.ticket = ticket;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTicket() {
        return this.ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

}
