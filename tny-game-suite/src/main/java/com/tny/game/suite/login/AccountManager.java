package com.tny.game.suite.login;

import com.tny.game.suite.login.dao.AccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"suite.game_auth", "suite.all"})
public class AccountManager {

    @Autowired
    private AccountDAO accountDAO;

    public Account getAccount(String account) {
        return this.accountDAO.get(account);
    }

    public int update(Account accountObj, GameTicket ticket) {
        return this.accountDAO.update(
                accountObj.getUid(),
                accountObj.getAccount(),
                ticket.getPfAccount(),
                ticket.getDevice(),
                ticket.getDeviceID(),
                ticket.getPf(),
                ticket.getZone(),
                ticket.getEntry(),
                ticket.getAd(),
                ticket.getServer(),
                accountObj.getCreateDate(),
                accountObj.getCreateAt());
    }

    public void updateOfflineAt(long uid, int dateInt, long millis) {
        this.accountDAO.updateOfflineAt(uid, dateInt, millis);
    }

    public void updateOnlineAt(long uid, int dateInt, long millis) {
        this.accountDAO.updateOnlineAt(uid, dateInt, millis);
    }

    public void updateCreateRole(long uid, int dateInt, long millis) {
        this.accountDAO.updateCreateRole(uid, dateInt, millis);
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
