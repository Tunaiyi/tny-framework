package com.tny.game.oplog;

import com.tny.game.base.item.Identifiable;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.behavior.Action;

public interface OpLogger {

    void submit();

    OpLogger logReceive(Item<?> item, Action action, int oldNum, int alter, int newNum);

    OpLogger logReceive(long playerID, long id, ItemModel model, Action action, int oldNum, int alter, int newNum);

    OpLogger logConsume(Item<?> item, Action action, int oldNum, int alter, int newNum);

    OpLogger logConsume(long playerID, long id, ItemModel model, Action action, int oldNum, int alter, int newNum);

    OpLogger logSnapshot(Identifiable item, Action action, SnapperType... types);

    boolean isLogging();

}