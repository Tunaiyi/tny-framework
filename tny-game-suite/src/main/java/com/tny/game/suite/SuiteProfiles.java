package com.tny.game.suite;

/**
 * Created by Kun Yang on 16/8/15.
 */
public interface SuiteProfiles {

    String GAME = "suite.game";
    String DOORS = "suite.doors";
    String ACCESS = "suite.access";
    String GM = "suite.gm";
    String LOG = "suite.log";

    String AUTO = "suite.auto";
    String PROTOEX = "suite.protoex";

    String CACHE_ASYNC = "suite.cache.async";
    String CACHE_DB = "suite.cache.db";
    String CACHE_REDIS = "suite.cache.redis";

    String SCHEDULER = "suite.scheduler";
    String SCHEDULER_CACHE = "suite.scheduler_CACHE";
    String ITEM = "suite.item";
    String ITEM_CACHE = "suite.item.cache";
    String ITEM_OPLOG = "suite.item.oplog";

    String SERVER = "suite.server";
    String SERVER_AUTH = "suite.server_auth";

    String GAME_KAFKA = "suite.game.kafka";
    String SERVER_KAFKA = "suite.server.kafka";

}
