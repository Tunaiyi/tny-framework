package com.tny.game.suite.login;

import com.google.common.collect.Range;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.common.utils.DateTimeHelper;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.utils.SuiteResultCode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({GAME})
public class AccountService implements ServerPreStart {

    @Autowired
    private AccountManager accountManager;

    private static final Logger LOGGER = LoggerFactory.getLogger("validator");

    private Map<Integer, UIDCreator> UIDCreatorMap = new HashMap<>();

    private ExecutorService growUIDExecutor = Executors.newSingleThreadExecutor(new CoreThreadFactory("GrowUIDExecutor"));

    public Account getAccount(String account, GameTicket ticket) {
        Account accountObj = this.accountManager.getAccount(account);
        if (accountObj != null) {
            accountObj.updateDevice(ticket);
            return accountObj;
        }
        return null;
    }

    public Account loadOrCreateAccount(GameTicket ticket) throws DispatchException {
        String account = AccountUtils.openID2Account(ticket.getAccountTag(), ticket.getServer(), ticket.getOpenID());
        int max = GameInfo.getMainInfo().getScopeType().isTest() ? Integer.MAX_VALUE : 10;
        int index = 0;
        Account accountObj = null;
        while (index < max) {
            accountObj = this.getAccount(account, ticket);
            if (accountObj != null)
                return accountObj;
            long playerID = this.createUID(ticket.getServer());
            try {
                AccountService.LOGGER.info("#FolSessionValidator#尝试为IP {} 创建帐号 {} 的PlayerID {}", ticket.getOpenID(), playerID);
                accountObj = new Account(playerID, account, ticket);
                int result = this.accountManager.update(accountObj, ticket);
                if (result > 0) {
                    accountObj.onCreate();
                    return accountObj;
                }
            } catch (Throwable e) {
                AccountService.LOGGER.error("创建帐号出错", e);
                return null;
            } finally {
                index++;
            }
        }
        return accountObj;
    }

    private UIDCreator getUIDCreator(int serverID) throws DispatchException {
        if (!GameInfo.isHasServer(serverID))
            throw new DispatchException(SuiteResultCode.AUTH_USER_LOGIN_ERROR_SID);
        UIDCreator creator = this.UIDCreatorMap.get(serverID);
        if (creator == null) {
            synchronized (this) {
                creator = this.UIDCreatorMap.get(serverID);
                if (creator == null) {
                    creator = new UIDCreator(serverID);
                    this.UIDCreatorMap.put(serverID, creator);
                }
            }
        }
        if (!GameInfo.getMainInfo().getScopeType().isTest() && creator.isFull())
            throw new DispatchException(SuiteResultCode.AUTH_USER_IS_FULL);
        return creator;
    }

    private long createUID(int serverID) throws DispatchException {
        return this.getUIDCreator(serverID).createID();
    }

    public void updateOfflineAt(Account account) {
        try {
            account.offline();
            DateTime dateTime = account.getOfflineTime();
            this.accountManager.updateOfflineAt(account.getPlayerID(), DateTimeHelper.date2Int(dateTime), dateTime.getMillis());
        } catch (Throwable e) {
            LOGGER.error("accountDAO.updateOfflineAt exception", e);
        }
    }

    public void updateOnlineAt(Account account, String ip) {
        try {
            account.online(ip);
            DateTime dateTime = account.getOnlineTime();
            this.accountManager.updateOnlineAt(account.getPlayerID(), DateTimeHelper.date2Int(dateTime), dateTime.getMillis());
        } catch (Throwable e) {
            LOGGER.error("accountDAO.updateOnlineAt exception", e);
        }
    }

    public void updateCreateRole(Account account, String name, DateTime dateTime) {
        try {
            account.setName(name);
            account.createRole(dateTime);
            if (dateTime != null) {
                this.accountManager.updateCreateRole(account.getUid(), name, DateTimeHelper.date2Int(dateTime), dateTime.getMillis());
            }
        } catch (Exception e) {
            LOGGER.error("accountDAO.updateCreateRole exception", e);
        }
    }

    public void updateName(Account account, String name) {
        try {
            account.setName(name);
            this.accountManager.updateName(account.getUid(), name);
        } catch (Exception e) {
            LOGGER.error("accountDAO.updateName exception", e);
        }
    }

    public class UIDCreator {

        private volatile boolean full = false;

        private int growSize = 10;

        private double growAtPCT = 0.5;

        private int serverID;

        private Range<Long> uidRange;

        private AtomicBoolean growing = new AtomicBoolean(false);

        private AccountManager manager;

        private Queue<Long> idQueue = new ConcurrentLinkedQueue<Long>();

        public UIDCreator(int serverID) {
            super();
            this.serverID = serverID;
            this.uidRange = IDUtils.createUIDRange(serverID);
            this.manager = AccountService.this.accountManager;
            List<Long> emptyIDs = this.manager.findEmptyUID(this.uidRange.lowerEndpoint(), this.uidRange.upperEndpoint());
            this.idQueue.addAll(emptyIDs);
            if (this.idQueue.isEmpty())
                this.doGrow();
        }

        public int getServerID() {
            return this.serverID;
        }

        public boolean isFull() {
            return this.full;
        }

        public Long createID() {
            Long id = null;
            while (!this.isFull() && id == null) {
                id = this.idQueue.poll();
                if (id != null) {
                    this.tryGrowUIDs();
                    return id;
                }
            }
            return id;
        }

        private void doGrow() {
            Long maxUID = this.manager.findMaxUID(this.uidRange.lowerEndpoint(), this.uidRange.upperEndpoint());
            if (maxUID == null)
                maxUID = this.uidRange.lowerEndpoint() + 10000 + ThreadLocalRandom.current().nextInt(10000);
            maxUID = Math.min(maxUID, this.uidRange.upperEndpoint());
            List<Long> growthIDs = new ArrayList<Long>();
            for (int index = 0; index < this.growSize; index++)
                growthIDs.add(maxUID++);
            int[] results = this.manager.insert(growthIDs);
            for (int index = 0; index < results.length; index++) {
                int result = results[index];
                if (result > 0 || result == java.sql.Statement.SUCCESS_NO_INFO) {
                    if (index < growthIDs.size())
                        this.idQueue.add(growthIDs.get(index));
                }
            }
        }

        private void tryGrowUIDs() {
            if (this.growing.get())
                return;
            double current = this.idQueue.size();
            double usedPCT = current / this.growSize;
            if (usedPCT <= this.growAtPCT) {
                if (this.growing.compareAndSet(false, true)) {
                    AccountService.this.growUIDExecutor.execute(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                UIDCreator.this.doGrow();
                            } catch (Exception e) {
                                LOGGER.error("", e);
                            } finally {
                                UIDCreator.this.growing.set(false);
                            }
                        }

                    });
                }
            }
        }

    }

    @Override
    public PerIniter getIniter() {
        return PerIniter.initer(this.getClass(), InitLevel.LEVEL_1);
    }

    @Override
    public void initialize() throws Exception {
        for (GameInfo info : GameInfo.getAllGamesInfo()) {
            this.getUIDCreator(info.getServerID());
        }
    }

}
