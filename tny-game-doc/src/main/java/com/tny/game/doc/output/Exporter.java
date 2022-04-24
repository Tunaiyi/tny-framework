package com.tny.game.doc.output;

import com.tny.game.doc.table.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 2017/4/8.
 */
interface Exporter {

    String output(OutputScheme table) throws IOException;

    default String getHead() {
        return "";
    }

    default String getTail() {
        return "";
    }

}
