package com.tny.game.suite.login.dao;

import com.tny.game.suite.login.*;
import net.paoding.rose.jade.annotation.*;

import java.util.*;

@DAO
public interface AccountDAO extends AccountDBFields {

    String ACCOUNT_FIELDS
            = "`uid`, `account`, `device`, `deviceId`, `name`, `pf`, `zone`, `entry`, `createSid`, `createAt`, `createRoleAt`, `onlineAt`, `offlineAt`";

    @SQL("select " + ACCOUNT_FIELDS + " from " + TABLE + " where `account` = :account")
    Account get(@SQLParam("account") String account);

    @SQL("select " + ACCOUNT_FIELDS + " from " + TABLE + " where `uid` = :uid")
    Account get(@SQLParam("uid") long uid);

    @SQL("select `uid` from " + TABLE + " where `createSid` = :sid and `deviceId` = :deviceId")
    Collection<Long> findUidsByDeviceId(@SQLParam("sid") int serverId, @SQLParam("deviceId") String deviceId);

    @SQL("select `uid` from " + TABLE + " where `createSid` = :sid and `deviceId` = :deviceId")
    Long findUidByOpenId(@SQLParam("sid") int serverId, @SQLParam("deviceId") String deviceId);

    @SQL("select `uid` from " + TABLE + " where `account` = :account")
    Long findUid(@SQLParam("account") String account);

    @SQL("select `account` from " + TABLE + " where `uid` = :uid")
    String findAccount(@SQLParam("uid") long uid);

    @SQL("SELECT DISTINCT `pf` FROM " + TABLE)
    List<String> findAllPf();

    @ReturnGeneratedKeys
    @SQL("insert ignore into " + TABLE + "(`uid`) values(:uid)")
    int[] insert(@SQLParam("uid") Collection<Long> uid);

    @SQL("select `uid` from " + TABLE + " where :minID <= `uid` and `uid` <= :maxID and `account` is null")
    List<Long> findEmptyUid(@SQLParam("minID") long min, @SQLParam("maxID") long max);

    @SQL("select max(`uid`) from " + TABLE + " where :minID <= `uid` and `uid` <= :maxID")
    Long findMaxUid(@SQLParam("minID") long min, @SQLParam("maxID") long max);

    @SQL("update " + TABLE + " set "
         + "`account`=:account, "
         + "`openId`=:openId, "
         + "`device`=:device, "
         + "`deviceId`=:deviceId, "
         + "`pf`=:pf, "
         + "`zone`=:zone, "
         + "`entry`=:entry, "
         + "`ad`=:ad, "
         + "`createSid`=:createSid, "
         + "`createDate`=:createDate, "
         + "`createAt`=:createAt, "
         + "`onlineAt`=:onlineAt, "
         + "`offlineAt`=:offlineAt "
         + "where `uid` = :uid and `account` is null")
    int updateIfNull(@SQLParam("uid") long uid,
            @SQLParam("account") String account,
            @SQLParam("openId") String openId,
            @SQLParam("device") String device,
            @SQLParam("deviceId") String deviceId,
            @SQLParam("pf") String pf,
            @SQLParam("zone") long zone,
            @SQLParam("entry") int entry,
            @SQLParam("ad") String ad,
            @SQLParam("createSid") int createSid,
            @SQLParam("createDate") int createDate,
            @SQLParam("createAt") long createAt,
            @SQLParam("onlineAt") Long onlineAt,
            @SQLParam("offlineAt") Long offlineAt);

    @SQL("update " + TABLE + " set "
         + "`device`=:device, "
         + "`deviceId`=:deviceId, "
         + "`pf`=:pf, "
         + "`zone`=:zone, "
         + "`entry`=:entry, "
         + "`pf`=:pf, "
         + "`onlineAt`=:onlineAt, "
         + "`offlineAt`=:offlineAt "
         + "where `uid` = :uid")
    int update(@SQLParam("uid") long uid,
            @SQLParam("device") String device,
            @SQLParam("deviceId") String deviceId,
            @SQLParam("pf") String pf,
            @SQLParam("zone") long zone,
            @SQLParam("entry") int entry,
            @SQLParam("onlineAt") Long onlineAt,
            @SQLParam("offlineAt") Long offlineAt);

    @SQL("update " + TABLE +
         " set `createRoleDate`=:createRoleDate, `name`=:name, `createRoleAt`=:createRoleAt, `onlineAt`=:createRoleAt, `level`=1 where `uid` = :uid")
    int updateCreateRole(@SQLParam("uid") long uid, @SQLParam("name") String name, @SQLParam("createRoleDate") int createRoleDate,
            @SQLParam("createRoleAt") long createRoleAt);

    @SQL("update " + TABLE + " set `name`=:name where `uid` = :uid")
    int updateName(@SQLParam("uid") long uid, @SQLParam("name") String name);

    @SQL("update " + TABLE +
         " set `activeDate`=:actionDate, `activeAt`=:actionAt, `pf`=:pf, `device` = :device, `deviceId` = :deviceId, `onlineAt`=:actionAt where `uid` = :uid")
    int updateOnlineAt(@SQLParam("uid") long uid, @SQLParam("actionDate") int activeDate, @SQLParam("actionAt") long onlineAt,
            @SQLParam("pf") String pf, @SQLParam("device") String device, @SQLParam("deviceId") String deviceId);

    @SQL("update " + TABLE + " set `activeDate`=:actionDate, `activeAt`=:actionAt, `offlineAt`=:actionAt where `uid` = :uid")
    int updateOfflineAt(@SQLParam("uid") long uid, @SQLParam("actionDate") int actionDate, @SQLParam("actionAt") long offlineAt);

}
