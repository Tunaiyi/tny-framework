package com.tny.game.suite.login;

import com.google.common.collect.Range;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.exception.*;
import com.tny.game.suite.core.*;
import com.tny.game.suite.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AccountService implements AppPrepareStart {

    @Resource
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

    public Account loadOrCreateAccount(GameTicket ticket) throws CommandException {
        String account = AccountUtils.openID2Account(ticket.getAccountTag(), ticket.getServer(), ticket.getOpenId());
        int max = GameInfo.info().getScopeType().isTest() ? Integer.MAX_VALUE : 10;
        int index = 0;
        Account accountObj = null;
        while (index < max) {
            accountObj = this.getAccount(account, ticket);
            if (accountObj != null)
                return accountObj;
            long playerId = this.createUid(ticket.getServer());
            try {
                AccountService.LOGGER.info("#FolSessionValidator#尝试为IP {} 创建帐号 {} 的PlayerID {}", ticket.getOpenId(), playerId);
                accountObj = new Account(playerId, account, ticket);
                int result = this.accountManager.updateIfNull(accountObj, ticket);
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

    private UIDCreator getUidCreator(int serverID) throws CommandException {
        if (!GameInfo.isHasServer(serverID))
            throw new CommandException(SuiteResultCode.AUTH_USER_LOGIN_ERROR_SID);
        UIDCreator creator = this.UIDCreatorMap.get(serverID);
        if (creator == null) {
            synchronized (this) {
                creator = this.UIDCreatorMap.computeIfAbsent(serverID, UIDCreator::new);
            }
        }
        if (!GameInfo.info().getScopeType().isTest() && creator.isFull())
            throw new CommandException(SuiteResultCode.AUTH_USER_IS_FULL);
        return creator;
    }

    private long createUid(int serverId) throws CommandException {
        return this.getUidCreator(serverId).createId();
    }

    public void updateOfflineAt(Account account) {
        try {
            account.offline();
            this.accountManager.updateOfflineAt(account);
        } catch (Throwable e) {
            LOGGER.error("accountDAO.updateOfflineAt exception", e);
        }
    }

    public void updateOnlineAt(Account account, GameTicket ticket, String ip) {
        try {
            account.online(ip);
            account.setDevice(ticket.getDevice());
            account.setDeviceId(ticket.getDeviceId());
            if (StringUtils.isNoneBlank(ticket.getPf()) && !Objects.equals(account.getPf(), ticket.getPf()))
                account.setPf(ticket.getPf());
            this.accountManager.updateOnlineAt(account, ticket);
        } catch (Throwable e) {
            LOGGER.error("accountDAO.updateOnlineAt exception", e);
        }
    }

    public void updateCreateRole(Account account, String name, DateTime dateTime) {
        try {
            account.setName(name);
            account.createRole(dateTime);
            if (dateTime != null) {
                this.accountManager.updateCreateRole(account);
            }
        } catch (Exception e) {
            LOGGER.error("accountDAO.updateCreateRole exception", e);
        }
    }

    public void updateName(Account account, String name) {
        try {
            account.setName(name);
            this.accountManager.updateName(account);
        } catch (Exception e) {
            LOGGER.error("accountDAO.updateName exception", e);
        }
    }

    public class UIDCreator {

        private volatile boolean full = false;

        private int growSize = 10;

        private double growAtPCT = 0.5;

        private int serverId;

        private Range<Long> uidRange;

        private AtomicBoolean growing = new AtomicBoolean(false);

        private AccountManager manager;

        private Queue<Long> idQueue = new ConcurrentLinkedQueue<>();

        public UIDCreator(int serverId) {
            super();
            this.serverId = serverId;
            this.uidRange = IDAide.createUIDRange(serverId);
            this.manager = AccountService.this.accountManager;
            List<Long> emptyIDs = this.manager.findEmptyUid(this.uidRange.lowerEndpoint(), this.uidRange.upperEndpoint());
            this.idQueue.addAll(emptyIDs);
            if (this.idQueue.isEmpty())
                this.doGrow();
        }

        public int getServerId() {
            return this.serverId;
        }

        public boolean isFull() {
            return this.full;
        }

        public Long createId() {
            Long id = null;
            while (!this.isFull()) {
                id = this.idQueue.poll();
                if (id != null) {
                    this.tryGrowUIDs();
                    return id;
                }
            }
            return id;
        }

        private void doGrow() {
            Long maxUID = this.manager.findMaxUid(this.uidRange.lowerEndpoint(), this.uidRange.upperEndpoint());
            if (maxUID == null)
                maxUID = this.uidRange.lowerEndpoint() + 10000 + ThreadLocalRandom.current().nextInt(10000);
            maxUID = Math.min(maxUID, this.uidRange.upperEndpoint());
            List<Long> growthIDs = new ArrayList<>();
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
                    AccountService.this.growUIDExecutor.execute(() -> {
                        try {
                            UIDCreator.this.doGrow();
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        } finally {
                            UIDCreator.this.growing.set(false);
                        }
                    });
                }
            }
        }

    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_6);
    }

    @Override
    public void prepareStart() throws Exception {
        for (GameInfo info : GameInfo.getAllGamesInfo()) {
            this.getUidCreator(info.getZoneId());
        }
    }

}
