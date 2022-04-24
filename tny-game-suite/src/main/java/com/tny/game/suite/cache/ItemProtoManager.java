package com.tny.game.suite.cache;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.tny.game.cache.mysql.*;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({PROTOBUF_MAPPER})
public class ItemProtoManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(ItemProtoManager.class);

    @Resource
    private ItemFindDAO findDAO;

    public UidRange getUidRange(String table) {
        return this.findDAO.getUidRange(table);
    }

    public List<Message> findAll(String table) {
        List<DBCacheItem> dbItems = this.findDAO.findAll(table);
        return parser(table, dbItems);
    }

    public List<Message> findByItemId(String table, Integer... itemIds) {
        List<DBCacheItem> dbItems;
        switch (itemIds.length) {
            case 0:
                dbItems = this.findDAO.findAll(table);
                break;
            case 1:
                dbItems = this.findDAO.findByItemId(table, itemIds[0]);
                break;
            default:
                dbItems = this.findDAO.findByItemId(table, Arrays.asList(itemIds));
                break;
        }
        return parser(table, dbItems);
    }

    public List<Message> findByUid(String table, long uid, Integer... itemIds) {
        List<DBCacheItem> dbItems;
        switch (itemIds.length) {
            case 0:
                dbItems = this.findDAO.findByUid(table, uid);
                break;
            case 1:
                dbItems = this.findDAO.findByUid(table, uid, itemIds[0]);
                break;
            default:
                dbItems = this.findDAO.findByUid(table, uid, Arrays.asList(itemIds));
                break;
        }
        return parser(table, dbItems);
    }

    public List<Message> findByUids(String table, List<Long> uids, Integer... itemIds) {
        List<DBCacheItem> dbItems;
        switch (itemIds.length) {
            case 0:
                dbItems = this.findDAO.findByUids(table, uids);
                break;
            case 1:
                dbItems = this.findDAO.findByUids(table, uids, itemIds[0]);
                break;
            default:
                dbItems = this.findDAO.findByUids(table, uids, Arrays.asList(itemIds));
                break;
        }
        return parser(table, dbItems);
    }

    public List<Message> findByUidRange(String table, long startUID, long endUID, Integer... itemIds) {
        List<DBCacheItem> dbItems;
        switch (itemIds.length) {
            case 0:
                dbItems = this.findDAO.findByUidRange(table, startUID, endUID);
                break;
            case 1:
                dbItems = this.findDAO.findByUidRange(table, startUID, endUID, itemIds[0]);
                break;
            default:
                dbItems = this.findDAO.findByUidRange(table, startUID, endUID, Arrays.asList(itemIds));
                break;
        }
        return parser(table, dbItems);
    }

    private List<Message> parser(String table, List<DBCacheItem> dbItems) {
        if (dbItems == null || dbItems.isEmpty()) {
            return ImmutableList.of();
        }
        Optional<ProtobufTableMapper> mapperOpt = ProtobufTableMapper.mapper(table);
        mapperOpt.orElseThrow(() -> new NullPointerException(table + "ProtobufTableMapper is null"));
        ProtobufTableMapper mapper = mapperOpt.get();
        return dbItems.stream()
                .map(item -> mapper.parser(item.getData()))
                .collect(Collectors.toList());
    }

}
