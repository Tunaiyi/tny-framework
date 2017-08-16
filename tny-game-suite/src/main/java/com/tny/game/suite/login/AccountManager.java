package com.tny.game.suite.login;

import com.tny.game.suite.login.dao.AccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({GAME})
public class AccountManager {

    @Autowired
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

    public int update(Account accountObj, GameTicket ticket) {
        return this.accountDAO.update(
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

    public void updateOfflineAt(long uid, int dateInt, long millis) {
        this.accountDAO.updateOfflineAt(uid, dateInt, millis);
    }

    public void updateOnlineAt(long uid, int dateInt, long millis) {
        this.accountDAO.updateOnlineAt(uid, dateInt, millis);
    }

    public void updateCreateRole(long uid, String name, int dateInt, long millis) {
        this.accountDAO.updateCreateRole(uid, name, dateInt, millis);
    }

    public void updateName(long uid, String name) {
        this.accountDAO.updateName(uid, name);
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
