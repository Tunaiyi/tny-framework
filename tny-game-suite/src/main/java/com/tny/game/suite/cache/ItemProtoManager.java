package com.tny.game.suite.cache;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.tny.game.cache.mysql.DBCacheItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({PROTOBUF_MAPPER})
public class ItemProtoManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(ItemProtoManager.class);

    @Resource
    private ItemFindDAO findDAO;

    public UIDRange getUIDRange(String table) {
        return this.findDAO.getUIDRange(table);
    }

    public List<Message> findAll(String table) {
        List<DBCacheItem> dbItems = this.findDAO.findAll(table);
        return parser(table, dbItems);
    }

    public List<Message> findByItemID(String table, Integer... itemID) {
        List<DBCacheItem> dbItems;
        switch (itemID.length) {
            case 0:
                dbItems = this.findDAO.findAll(table);
                break;
            case 1:
                dbItems = this.findDAO.findByItemID(table, itemID[0]);
                break;
            default:
                dbItems = this.findDAO.findByItemID(table, Arrays.asList(itemID));
                break;
        }
        return parser(table, dbItems);
    }

    public List<Message> findByUID(String table, long uid, Integer... itemIDs) {
        List<DBCacheItem> dbItems;
        switch (itemIDs.length) {
            case 0:
                dbItems = this.findDAO.findByUID(table, uid);
                break;
            case 1:
                dbItems = this.findDAO.findByUID(table, uid, itemIDs[0]);
                break;
            default:
                dbItems = this.findDAO.findByUID(table, uid, Arrays.asList(itemIDs));
                break;
        }
        return parser(table, dbItems);
    }

    public List<Message> findByUIDs(String table, List<Long> uids, Integer... itemIDs) {
        List<DBCacheItem> dbItems;
        switch (itemIDs.length) {
            case 0:
                dbItems = this.findDAO.findByUIDs(table, uids);
                break;
            case 1:
                dbItems = this.findDAO.findByUIDs(table, uids, itemIDs[0]);
                break;
            default:
                dbItems = this.findDAO.findByUIDs(table, uids, Arrays.asList(itemIDs));
                break;
        }
        return parser(table, dbItems);
    }

    public List<Message> findByUIDRange(String table, long startUID, long endUID, Integer... itemIDs) {
        List<DBCacheItem> dbItems;
        switch (itemIDs.length) {
            case 0:
                dbItems = this.findDAO.findByUIDRange(table, startUID, endUID);
                break;
            case 1:
                dbItems = this.findDAO.findByUIDRange(table, startUID, endUID, itemIDs[0]);
                break;
            default:
                dbItems = this.findDAO.findByUIDRange(table, startUID, endUID, Arrays.asList(itemIDs));
                break;
        }
        return parser(table, dbItems);
    }

    private List<Message> parser(String table, List<DBCacheItem> dbItems) {
        if (dbItems == null || dbItems.isEmpty())
            return ImmutableList.of();
        return dbItems.stream()
                .map(item -> ProtobufTableMapper.mapper(table).get()
                        .parser(item.getData()))
                .collect(Collectors.toList());
    }

}
