package com.tny.game.suite.login;

import com.tny.game.suite.base.DateTimeAide;
import com.tny.game.suite.login.dao.AccountDAO;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({GAME})
public class AccountManager {

    @Resource
    private AccountDAO accountDAO;

    public Account getAccount(String account) {
        return this.accountDAO.get(account);
    }

    public Account getAccount(long userID) {
        return this.accountDAO.get(userID);
    }

    public Collection<Long> findUIDsByDeviceID(int serverID, String deviceID) {
        return this.accountDAO.findUIDsByDeviceID(serverID, deviceID);
    }

    public Long findUIDByOpenID(int serverID, String openID) {
        return this.accountDAO.findUIDByOpenID(serverID, openID);
    }

    public List<String> findPF() {
        return this.accountDAO.findAllFP();
    }

    public int updateIfNull(Account accountObj, GameTicket ticket) {
        return this.accountDAO.updateIfNull(
                accountObj.getUid(),
                accountObj.getAccount(),
                ticket.getOpenID(),
                ticket.getDevice(),
                ticket.getDeviceID(),
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
        DateTime dateTime = accountObj.getOfflineTime();
        if (dateTime == null)
            dateTime = DateTime.now();
        this.accountDAO.updateOfflineAt(accountObj.getUid(), DateTimeAide.date2Int(dateTime), dateTime.getMillis());
    }

    public void updateOnlineAt(Account account, GameTicket ticket) {
        DateTime dateTime = account.getOnlineTime();
        if (dateTime == null)
            dateTime = DateTime.now();
        this.accountDAO.updateOnlineAt(
                account.getUid(),
                DateTimeAide.date2Int(dateTime),
                dateTime.getMillis(), ticket.getPf(), ticket.getDevice(), ticket.getDeviceID());
    }

    public void updateCreateRole(Account account) {
        DateTime dateTime = account.getCreateDateTime();
        if (dateTime == null)
            return;
        this.accountDAO.updateCreateRole(
                account.getUid(), account.getName(), DateTimeAide.date2Int(dateTime), dateTime.getMillis());
    }

    public void updateName(Account account) {
        this.accountDAO.updateName(account.getUid(), account.getName());
    }

    public List<Long> findEmptyUID(Long min, Long max) {
        return this.accountDAO.findEmptyUID(min, max);
    }

    public Long findMaxUID(Long min, Long max) {
        return this.accountDAO.findMaxUID(min, max);
    }

    public int[] insert(List<Long> uids) {
        return this.accountDAO.insert(uids);
    }
}
