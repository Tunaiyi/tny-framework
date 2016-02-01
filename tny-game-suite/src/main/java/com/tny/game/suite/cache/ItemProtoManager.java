package com.tny.game.suite.cache;

import com.tny.game.cache.mysql.DBItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Profile({"suite.cache", "suite.all"})
public class ItemProtoManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(ItemProtoManager.class);

    @Autowired
    private ItemFindDAO findDAO;

    public Long getUIDByName(String name, int sid) {
        return this.findDAO.getUIDByName(name, ShardTable.get(sid));
    }

    public UIDRange getUIDRange(ShardTable table) {
        return this.findDAO.getUIDRange(table);
    }

    public List<DBItem> findAll(ShardTable table) {
        List<DBItem> dbItems = this.findDAO.findAll(table);
        return dbItems;
    }

    public List<DBItem> findByItemID(ShardTable table, Integer... itemID) {
        List<DBItem> dbItems = Collections.emptyList();
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
        return dbItems;
    }

    public List<DBItem> findByUID(ShardTable table, long uid, Integer... itemIDs) {
        List<DBItem> dbItems = Collections.emptyList();
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
        return dbItems;
    }

    public List<DBItem> findByUIDs(ShardTable table, List<Long> uids, Integer... itemIDs) {
        List<DBItem> dbItems = Collections.emptyList();
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
        return dbItems;
    }

    public List<DBItem> findByUIDRange(ShardTable table, long startUID, long endUID, Integer... itemIDs) {
        List<DBItem> dbItems = Collections.emptyList();
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
        return dbItems;
    }

}
