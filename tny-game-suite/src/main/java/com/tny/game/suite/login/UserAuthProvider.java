package com.tny.game.suite.login;

import com.google.common.collect.Range;
import com.tny.game.common.utils.md5.MD5;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.dispatcher.exception.ValidatorFailException;
import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.core.SessionKeys;
import com.tny.game.suite.utils.Configs;
import com.tny.game.suite.utils.SuiteResultCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public abstract class UserAuthProvider extends GameAuthProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthProvider.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private GameTicketMaker ticketMaker;

    private volatile boolean online = true;

    public UserAuthProvider(String name, Set<Integer> includes) {
        this(name, includes, null);
    }

    public UserAuthProvider(String name, Range<Integer> includeRange) {
        this(name, includeRange, null);
    }

    public UserAuthProvider(String name, Range<Integer> includeRange, Range<Integer> excludeRange) {
        this(name, null, null, includeRange, excludeRange);
    }

    public UserAuthProvider(String name, Set<Integer> includes, Set<Integer> excludes) {
        this(name, includes, excludes, null, null);
    }

    public UserAuthProvider(String name, Set<Integer> includes, Set<Integer> excludes, Range<Integer> includeRange, Range<Integer> excludeRange) {
        super(name, includes, excludes, includeRange, excludeRange);
    }


    public boolean isOnline() {
        return online;
    }

    protected GameTicket getTicket(Request request, String openID, String openKey, String ticketWord) throws ValidatorFailException {
        GameTicket ticket;
        try {
            if (this.isAuth()) {
                if (ticketWord == null)
                    throw new ValidatorFailException(openID, request.getHostName(), "无登录票据");
                ticket = GameTicketHelper.decryptTicket(ticketWord);
                if (!this.isOnline() && !ticket.isInterior())
                    throw new DispatchException(SuiteResultCode.AUTH_SERVER_IS_OFFLINE, "服务器正在维护");
                // 维护时候只有pf为inside才可以进入
                String checkKey = this.ticketMaker.make(ticket);
                if (!checkKey.equals(ticket.getSecret()))
                    throw new ValidatorFailException(openID, request.getHostName(), "票据验证失败");
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
                    ticket = new GameTicket(ticketID, serverID, openID, openID, false, openKey, pf, pf, zoneID, entryID, null, null, time, null);
                } else {
                    ticket = GameTicketHelper.decryptTicket(ticketWord);
                    String checkKey = this.ticketMaker.make(ticket);
                    if (!checkKey.equals(ticket.getSecret()))
                        throw new ValidatorFailException(ticket.getOpenID(), request.getHostName(), "票据验证失败" + ticket);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("ticket word to object JSONException", e);
            if (e instanceof ValidatorFailException)
                throw (ValidatorFailException) e;
            throw new ValidatorFailException(openID, request.getHostName(), "票据验证异常", e);
        }
        return ticket;
    }

    protected LoginCertificate checkUserLogin(Request request, boolean relogin) throws DispatchException {
        String openID = request.getParameter(0, String.class);
        String openKey = request.getParameter(1, String.class);
        String ticketWord = request.getParameter(3, String.class);
        GameTicket ticket = this.getTicket(request, openID, openKey, ticketWord);
        if (ticket == null)
            throw new ValidatorFailException(openID, request.getHostName(), "票据解析失败");
        LOGGER.info("create openID : " + openID + "  openKey : " + openKey);
        Account accountObj = this.accountService.loadOrCreateAccount(ticket);
        if (accountObj == null)
            throw new ValidatorFailException(openID, request.getHostName(), (relogin ? "重登" : "登录") + "帐号不存在");
        accountObj.online(request.getHostName());
        request.getSession().attributes().setAttribute(SessionKeys.OPEN_ID_KEY, ticket.getOpenID());
        request.getSession().attributes().setAttribute(SessionKeys.OPEN_KEY_KEY, ticket.getOpenKey());
        request.getSession().attributes().setAttribute(SessionKeys.ACCOUNT_KEY, accountObj);
        request.getSession().attributes().setAttribute(SessionKeys.TICKET_KEY, ticket);
        LOGGER.info("#FolSessionValidator#为IP {} 帐号 {} 创建玩家PlayerID为 {}", request.getHostName(), ticket.getOpenID(), accountObj.getUid());
        return LoginCertificate.createLogin(ticket.getTokenID(), accountObj.getUid(), relogin);
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
