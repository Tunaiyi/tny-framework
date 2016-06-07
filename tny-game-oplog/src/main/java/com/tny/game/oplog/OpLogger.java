package com.tny.game.oplog;

import com.tny.game.base.item.Identifiable;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.behavior.Action;

public interface OpLogger {

    void submit();

    OpLogger logReceive(Item<?> item, Action action, long oldNum, long alter, long newNum);

    OpLogger logReceive(long playerID, long id, ItemModel model, Action action, long oldNum, long alter, long newNum);

    OpLogger logReceive(long playerID, long id, int itemID, Action action, long oldNum, long alter, long newNum);

    OpLogger logConsume(Item<?> item, Action action, long oldNum, long alter, long newNum);

    OpLogger logConsume(long playerID, long id, ItemModel model, Action action, long oldNum, long alter, long newNum);

    OpLogger logConsume(long playerID, long id, int itemID, Action action, long oldNum, long alter, long newNum);

    OpLogger logSnapshot(Identifiable item, Action action, SnapperType... types);

    OpLogger logSnapShot(Identifiable item, Action action, Class<? extends Snapper>... snapperTypes);

    boolean isLogging();

}