package com.tny.game.suite;

/**
 * Created by Kun Yang on 16/8/15.
 */
public interface SuiteProfiles {

    String WEB = "suite.web";
    String GAME = "suite.game";
    String DOORS = "suite.doors";
    String ACCESS = "suite.access";
    String GM = "suite.gm";
    String LOG = "suite.log";

    String AUTO = "suite.auto";
    String AUTO_SNAP = "suite.auto.snap";
    String AUTO_PERSISTENT = "suite.auto.persistent";
    String PROTOEX = "suite.protoex";
    String TEXT_FILTER = "suite.controller.text_filter";

    String CACHE_ALL = "suite.cache.all";
    String CACHE_ASYNC = "suite.cache.async";
    String CACHE_DB = "suite.cache.db";
    String CACHE_REDIS = "suite.cache.redis";

    String SCHEDULER = "suite.scheduler";
    String SCHEDULER_CACHE = "suite.scheduler_cache";
    String SCHEDULER_DB = "suite.scheduler_db";
    String ITEM = "suite.item";
    String ITEM_OPLOG = "suite.item.oplog";
    String CAPACITY = "suite.capacity";
    String PROTOBUF_MAPPER = "suite.item.protobuf_mapper";

    String SERVER = "suite.server";
    String CLIENT = "suite.client";
    String SERVER_AUTH = "suite.server_auth";

}
