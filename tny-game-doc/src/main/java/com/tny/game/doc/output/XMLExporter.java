/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.output;

import com.thoughtworks.xstream.XStream;
import com.tny.game.doc.table.*;

import java.io.IOException;

/**
 * xml 格式化
 * Created by Kun Yang on 2017/4/8.
 */
class XMLExporter implements Exporter {

    private final XStream xstream = new XStream();

    XMLExporter() {
        xstream.autodetectAnnotations(true);
        xstream.aliasSystemAttribute(null, "class");
    }

    @Override
    public String output(OutputScheme table) throws IOException {
        return xstream.toXML(table);
    }

    @Override
    public String getHead() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    }

}
