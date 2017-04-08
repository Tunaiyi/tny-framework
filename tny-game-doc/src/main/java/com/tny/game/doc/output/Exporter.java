package com.tny.game.doc.output;

import com.tny.game.doc.table.ConfigTable;

/**
 * Created by Kun Yang on 2017/4/8.
 */
interface Exporter {

    String output(ConfigTable table);

    String getHead();
}
