package com.tny.game.suite.login;

import com.tny.game.net.base.NetResponseCode;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.common.utils.ReferenceType;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.NetCertificate;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.common.utils.md5.MD5;
import com.tny.game.suite.core.AttributesKeys;
import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.utils.Configs;
import com.tny.game.suite.utils.SuiteResultCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

public abstract class UserAuthProvider extends GameAuthProvider<Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthProvider.class);

    @Resource
    private AccountService accountService;

    @Resource
    private GameTicketMaker ticketMaker;

    private static ReferenceType<List<String>> BODY_CLASS = new ReferenceType<List<String>>() {
    };

    private volatile boolean online = true;

    public boolean isOnline() {
        return online;
    }

    protected GameTicket getTicket(String openID, String openKey, String ticketWord) throws ValidatorFailException {
        GameTicket ticket;
        try {
            if (this.isAuth()) {
                if (ticketWord == null)
                    throw new ValidatorFailException(SuiteResultCode.AUTH_NO_TICKET, openID);
                ticket = GameTicketHelper.decryptTicket(ticketWord);
                if (!this.isOnline() && !ticket.isInterior())
                    throw new DispatchException(SuiteResultCode.AUTH_SERVER_IS_OFFLINE, openID);
                // 维护时候只有pf为inside才可以进入
                String checkKey = this.ticketMaker.make(ticket);
                if (!checkKey.equals(ticket.getSecret()))
                    throw new ValidatorFailException(NetResponseCode.VALIDATOR_FAIL, openID);
            } else {
                if (ticketWord == null || ticketWord.equals("{}") || ticketWord.isEmpty()) {
                    int serverID = Configs.DEVELOP_CONFIG.getInt(Configs.DEVELOP_AUTH_SERVER_ID, GameInfo.getMainServerID());
                    int zoneID = Configs.DEVELOP_CONFIG.getInt(Configs.DEVELOP_AUTH_ZONE_ID, 1);
                    int entryID = Configs.DEVELOP_CONFIG.getInt(Configs.DEVELOP_AUTH_ENTRY_ID, 1);
                    String pf = Configs.DEVELOP_CONFIG.getStr(Configs.DEVELOP_AUTH_PF, "lingqu");
                    String[] pfs = StringUtils.split(openID, "@");
                    if (pfs.length > 1) {
                        openID = pfs[0];
                        pf = pfs[1];
                    }
                    long time = Configs.devDateTime(Configs.DEVELOP_AUTH_CREATE_AT, System.currentTimeMillis());
                    long ticketID = System.currentTimeMillis();
                    if (StringUtils.isNumeric(openKey))
                        ticketID = NumberUtils.toLong(openKey);
                    ticket = new GameTicket(ticketID, serverID, openID, openID, false, openKey, "zh-CN", pf, pf, zoneID, entryID, null, null, time, null);
                } else {
                    ticket = GameTicketHelper.decryptTicket(ticketWord);
                    String checkKey = this.ticketMaker.make(ticket);
                    if (!checkKey.equals(ticket.getSecret()))
                        throw new ValidatorFailException(NetResponseCode.VALIDATOR_FAIL, openID);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("ticket word to object JSONException", e);
            if (e instanceof ValidatorFailException)
                throw (ValidatorFailException) e;
            throw new ValidatorFailException(SuiteResultCode.AUTH_ERROR, openID, e);
        }
        return ticket;
    }

    protected NetCertificate<Long> checkUserLogin(Tunnel<Long> tunnel, Message<Long> message, boolean relogin) throws DispatchException {
        List<String> params = message.getBody(BODY_CLASS);
        String openID = params.get(0);
        String openKey = params.get(1);
        String ticketWord = params.get(3);
        GameTicket ticket = this.getTicket(openID, openKey, ticketWord);
        if (ticket == null)
            throw new ValidatorFailException(SuiteResultCode.AUTH_NO_TICKET, openID);
        LOGGER.info("create openID : " + openID + "  openKey : " + openKey);
        Account accountObj = this.accountService.loadOrCreateAccount(ticket);
        if (accountObj == null)
            throw new ValidatorFailException(SuiteResultCode.AUTH_NO_ACCOUNT, openID + (relogin ? "重登" : "登录"));
        // accountObj.online(tunnel.getHostName());
        tunnel.attributes().setAttribute(AttributesKeys.OPEN_ID_KEY, ticket.getOpenID());
        tunnel.attributes().setAttribute(AttributesKeys.OPEN_KEY_KEY, ticket.getOpenKey());
        tunnel.attributes().setAttribute(AttributesKeys.ACCOUNT_KEY, accountObj);
        tunnel.attributes().setAttribute(AttributesKeys.TICKET_KEY, ticket);
        LOGGER.info("#FolSessionValidator#为IP {} 帐号 {} 创建玩家PlayerID为 {}", tunnel.remoteAddress(), ticket.getOpenID(), accountObj.getUid());
        return NetCertificate.createLogin(ticket.getTokenID(), accountObj.getUid(), relogin);
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public static void main(String[] args) {
        System.out.println("funsplay.god=" + MD5.md5("funsplay.god"));
        System.out.println("fol.server.games.password=" + MD5.md5("fol.server.games.password67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println("fol.server.admin.password=" + MD5.md5("fol.server.admin.password67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println("fol.server.log.password=" + MD5.md5("fol.server.log.password67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println("fol.server.api.password=" + MD5.md5("fol.server.api.password67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println("fol.server.access.password=" + MD5.md5("fol.server.access.password67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println();
        System.out.println("fol.server.login.pf_token_key=" + MD5.md5("fol.server.login.pf_token_key67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println("fol.server.login.ticket_key=" + MD5.md5("fol.server.login.ticket_key67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println("fol.server.access.pay.pay_bill_key=" + MD5.md5("fol.server.access.pay.pay_bill_key67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println("fol.server.access.lj_sdk.login_key=" + MD5.md5("fol.server.access.lj_sdk.login_key67a2c91fd6ad9f034d75fe27aded85db"));
        System.out.println("fol.server.admin.remember_me_key=" + MD5.md5("fol.server.admin.remember_me_key67a2c91fd6ad9f034d75fe27aded85db"));
    }
}
