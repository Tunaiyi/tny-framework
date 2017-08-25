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

    String ACCOUNT_FIELDS = "`uid`, `account`, `device`, `deviceID`, `name`, `pf`, `zone`, `entry`, `createSID`, `createAt`, `createRoleAt`, `onlineAt`, `offlineAt`";

    @SQL("select " + ACCOUNT_FIELDS + " from " + TABLE + " where `account` = :account")
    Account get(@SQLParam("account") String account);

    @SQL("select " + ACCOUNT_FIELDS + " from " + TABLE + " where `uid` = :uid")
    Account get(@SQLParam("uid") long uid);

    @SQL("select `uid` from " + TABLE + " where `createSID` = :sid and `deviceID` = :deviceID")
    Collection<Long> findUIDsByDeviceID(@SQLParam("sid") int serverID, @SQLParam("deviceID") String deviceID);

    @SQL("select `uid` from " + TABLE + " where `createSID` = :sid and `deviceID` = :deviceID")
    Long findUIDByOpenID(@SQLParam("sid") int serverID, @SQLParam("deviceID") String deviceID);

    @SQL("select `uid` from " + TABLE + " where `account` = :account")
    Long findUID(@SQLParam("account") String account);

    @SQL("select `account` from " + TABLE + " where `uid` = :uid")
    String findAccount(@SQLParam("uid") long uid);

    @ReturnGeneratedKeys
    @SQL("insert ignore into " + TABLE + "(`uid`) values(:uid)")
    int[] insert(@SQLParam("uid") Collection<Long> uid);

    @SQL("select `uid` from " + TABLE + " where :minID <= `uid` and `uid` <= :maxID and `account` is null")
    List<Long> findEmptyUID(@SQLParam("minID") long min, @SQLParam("maxID") long max);

    @SQL("select max(`uid`) from " + TABLE + " where :minID <= `uid` and `uid` <= :maxID")
    Long findMaxUID(@SQLParam("minID") long min, @SQLParam("maxID") long max);

    @SQL("update " + TABLE + " set "
            + "`account`=:account, "
            + "`openID`=:openID, "
            + "`device`=:device, "
            + "`deviceID`=:deviceID, "
            + "`pf`=:pf, "
            + "`zone`=:zone, "
            + "`entry`=:entry, "
            + "`ad`=:ad, "
            + "`createSID`=:createSID, "
            + "`createDate`=:createDate, "
            + "`createAt`=:createAt, "
            + "`onlineAt`=:onlineAt, "
            + "`offlineAt`=:offlineAt "
            + "where `uid` = :uid and `account` is null")
    int update(@SQLParam("uid") long uid,
               @SQLParam("account") String account,
               @SQLParam("openID") String openID,
               @SQLParam("device") String device,
               @SQLParam("deviceID") String deviceID,
               @SQLParam("pf") String pf,
               @SQLParam("zone") long zone,
               @SQLParam("entry") int entry,
               @SQLParam("ad") String ad,
               @SQLParam("createSID") int createSID,
               @SQLParam("createDate") int createDate,
               @SQLParam("createAt") long createAt,
               @SQLParam("onlineAt") Long onlineAt,
               @SQLParam("offlineAt") Long offlineAt);

    @SQL("update " + TABLE + " set `createRoleDate`=:createRoleDate, `name`=:name, `createRoleAt`=:createRoleAt, `onlineAt`=:createRoleAt, `level`=1 where `uid` = :uid")
    int updateCreateRole(@SQLParam("uid") long uid, @SQLParam("name") String name, @SQLParam("createRoleDate") int createRoleDate, @SQLParam("createRoleAt") long createRoleAt);

    @SQL("update " + TABLE + " set `name`=:name where `uid` = :uid")
    int updateName(@SQLParam("uid") long uid, @SQLParam("name") String name);

    @SQL("update " + TABLE + " set `activeDate`=:actionDate, `activeAt`=:actionAt, `device` = :device, `deviceID` = :deviceID, `onlineAt`=:actionAt where `uid` = :uid")
    int updateOnlineAt(@SQLParam("uid") long uid, @SQLParam("actionDate") int activeDate, @SQLParam("actionAt") long onlineAt, @SQLParam("device") String device, @SQLParam("deviceID") String deviceID);

    @SQL("update " + TABLE + " set `activeDate`=:actionDate, `activeAt`=:actionAt, `offlineAt`=:actionAt where `uid` = :uid")
    int updateOfflineAt(@SQLParam("uid") long uid, @SQLParam("actionDate") int actionDate, @SQLParam("actionAt") long offlineAt);

}
