package com.tny.game.doc.output;

import com.thoughtworks.xstream.XStream;
import com.tny.game.doc.table.ConfigTable;

/**
 * xml 格式化
 * Created by Kun Yang on 2017/4/8.
 */
class XMLExporter implements Exporter {

    private XStream xstream = new XStream();

    XMLExporter() {
        xstream.autodetectAnnotations(true);
        xstream.aliasSystemAttribute(null, "class");
    }

    @Override
    public String output(ConfigTable table) {
        return xstream.toXML(table);
    }

    @Override
    public String getHead() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    }
}