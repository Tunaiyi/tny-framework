package com.tny.game.suite.login;

import com.tny.game.common.utils.*;
import com.tny.game.suite.login.dao.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({GAME})
public class AccountManager {

    @Autowired
    private AccountDAO accountDAO;

    public Account getAccount(String account) {
        return this.accountDAO.get(account);
    }

    public Account getAccount(long userId) {
        return this.accountDAO.get(userId);
    }

    public Collection<Long> findUidsByDeviceId(int serverId, String deviceId) {
        return this.accountDAO.findUidsByDeviceId(serverId, deviceId);
    }

    public Long findUIDByOpenId(int serverId, String openId) {
        return this.accountDAO.findUidByOpenId(serverId, openId);
    }

    public List<String> findPF() {
        return this.accountDAO.findAllPf();
    }

    public int updateIfNull(Account accountObj, GameTicket ticket) {
        return this.accountDAO.updateIfNull(
                accountObj.getUid(),
                accountObj.getAccount(),
                ticket.getOpenId(),
                ticket.getDevice(),
                ticket.getDeviceId(),
                ticket.getPf(),
                ticket.getZone(),
                ticket.getEntry(),
                ticket.getAd(),
                ticket.getServer(),
                accountObj.getCreateDate(),
                accountObj.getCreateAt(),
                accountObj.getOnlineAt(),
                accountObj.getOfflineAt());
    }

    // public int update(Account accountObj, GameTicket ticket) {
    //     return this.accountDAO.update(
    //             accountObj.getUid(),
    //             ticket.getDevice(),
    //             ticket.getDeviceID(),
    //             ticket.getPF(),
    //             ticket.getZone(),
    //             ticket.getEntry(),
    //             accountObj.getOnlineAt(),
    //             accountObj.getOfflineAt());
    // }

    public void updateOfflineAt(Account accountObj) {
        Instant dateTime = accountObj.getOfflineTime();
        if (dateTime == null) {
            dateTime = Instant.now();
        }
        this.accountDAO.updateOfflineAt(accountObj.getUid(), DateTimeAide.date2Int(dateTime), dateTime.toEpochMilli());
    }

    public void updateOnlineAt(Account account, GameTicket ticket) {
        Instant dateTime = account.getOnlineTime();
        if (dateTime == null) {
            dateTime = Instant.now();
        }
        this.accountDAO.updateOnlineAt(
                account.getUid(),
                DateTimeAide.date2Int(dateTime),
                dateTime.toEpochMilli(), ticket.getPf(), ticket.getDevice(), ticket.getDeviceId());
    }

    public void updateCreateRole(Account account) {
        Instant dateTime = account.getCreateDateTime();
        if (dateTime == null) {
            return;
        }
        this.accountDAO.updateCreateRole(
                account.getUid(), account.getName(), DateTimeAide.date2Int(dateTime), dateTime.toEpochMilli());
    }

    public void updateName(Account account) {
        this.accountDAO.updateName(account.getUid(), account.getName());
    }

    public List<Long> findEmptyUid(Long min, Long max) {
        return this.accountDAO.findEmptyUid(min, max);
    }

    public Long findMaxUid(Long min, Long max) {
        return this.accountDAO.findMaxUid(min, max);
    }

    public int[] insert(List<Long> uids) {
        return this.accountDAO.insert(uids);
    }

}
