package com.tny.game.suite.login.dao;

import com.tny.game.suite.login.Account;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.ReturnGeneratedKeys;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import java.util.Collection;
import java.util.List;

@DAO
public interface AccountDAO extends AccountDBFields {

    public static final String ACCOUNT_FIELDS = "`uid`, `account`, `device`, `deviceID`, `name`, `pf`, `zone`, `entry`, `createSID`, `createAt`, `createRoleAt`";

    @SQL("select " + ACCOUNT_FIELDS + " from " + TABLE + " where `account` = :account")
    public Account get(@SQLParam("account") String account);

    @SQL("select `uid` from " + TABLE + " where `account` = :account")
    public Long getUid(@SQLParam("account") String account);

    @SQL("select `account` from " + TABLE + " where `uid` = :uid")
    public String getAccount(@SQLParam("uid") long uid);

    @ReturnGeneratedKeys
    @SQL("insert ignore into " + TABLE + "(`uid`) values(:uid)")
    public int[] insert(@SQLParam("uid") Collection<Long> uid);

    @SQL("select `uid` from " + TABLE + " where :minID <= `uid` and `uid` <= :maxID and `account` is null")
    public List<Long> findEmptyUID(@SQLParam("minID") long min, @SQLParam("maxID") long max);

    @SQL("select max(`uid`) from " + TABLE + " where :minID <= `uid` and `uid` <= :maxID")
    public Long findMaxUID(@SQLParam("minID") long min, @SQLParam("maxID") long max);

    @SQL("update " + TABLE + " set "
            + "`account`=:account, "
            + "`pfAccount`=:pfAccount, "
            + "`device`=:device, "
            + "`deviceID`=:deviceID, "
            + "`pf`=:pf, "
            + "`zone`=:zone, "
            + "`entry`=:entry, "
            + "`ad`=:ad, "
            + "`createSID`=:createSID, "
            + "`createDate`=:createDate, "
            + "`createAt`=:createAt "
            + "where `uid` = :uid and `account` is null")
    public int update(@SQLParam("uid") long uid,
                      @SQLParam("account") String account,
                      @SQLParam("pfAccount") String pfAccount,
                      @SQLParam("device") String device,
                      @SQLParam("deviceID") String deviceID,
                      @SQLParam("pf") String pf,
                      @SQLParam("zone") int zone,
                      @SQLParam("entry") int entry,
                      @SQLParam("ad") String ad,
                      @SQLParam("createSID") int createSID,
                      @SQLParam("createDate") int createDate,
                      @SQLParam("createAt") long createAt);

    @SQL("update " + TABLE + " set `createRoleDate`=:createRoleDate, `createRoleAt`=:createRoleAt, `onlineAt`=:createRoleAt, `level`=1 where `uid` = :uid")
    public int updateCreateRole(@SQLParam("uid") long uid, @SQLParam("createRoleDate") int createRoleDate, @SQLParam("createRoleAt") long createRoleAt);

    @SQL("update " + TABLE + " set `activeDate`=:actionDate, `activeAt`=:actionAt, `onlineAt`=:actionAt where `uid` = :uid")
    public int updateOnlineAt(@SQLParam("uid") long uid, @SQLParam("actionDate") int activeDate, @SQLParam("actionAt") long onlineAt);

    @SQL("update " + TABLE + " set `activeDate`=:actionDate, `activeAt`=:actionAt, `offlineAt`=:actionAt where `uid` = :uid")
    public int updateOfflineAt(@SQLParam("uid") long uid, @SQLParam("actionDate") int actionDate, @SQLParam("actionAt") long offlineAt);

}
